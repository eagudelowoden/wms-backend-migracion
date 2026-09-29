package com.woden.wms_backend.services.ClienteServices;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterName;

import java.awt.print.PrinterJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.EtiquetaDatosGeneralesDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.PrnPathResolverService;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Service
public class ZplPrinterService {

  @Autowired
  private PrnPathResolverService prnPathResolver;

  @Autowired
  private EtiquetaRepository etiquetaRepository;

  private boolean esReemplazoBoxActivo() {
    Integer clientId = ClientDatabaseContext.getCurrentClientId();
    if (clientId == null) return false;
    try {
      Integer tiene = etiquetaRepository.tieneValidacionDirecta(clientId, "REEMPLAZAR_VARIABLE_BOX_PRN");
      return tiene != null && tiene > 0;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean esTipoEmpaque(String tipo) {
    return tipo != null && tipo.toUpperCase(Locale.ROOT).startsWith("EMPAQUE");
  }

  private String reemplazarVariableBox(String zpl, String valor) {
    String valorSeguro = java.util.regex.Matcher.quoteReplacement(
            valor != null ? valor : ""
    );
    return zpl.replaceAll(
            "(?:(?<![0-9A-Za-z])|(?<=\\^FD))Box(?![0-9A-Za-z])",
            valorSeguro
    );
  }

  // ✅ Helper central: reemplaza placeholder exacto, sin afectar variantes con sufijo
  private String reemplazarSeguro(String zpl, String placeholder, String valor) {
    String valorSeguro = java.util.regex.Matcher.quoteReplacement(
            valor != null ? valor : ""
    );
    return zpl.replaceAll(
            java.util.regex.Pattern.quote(placeholder) + "(?![0-9A-Za-z])",
            valorSeguro
    );
  }

  public String generarZpl(
          String plantillaBasePath,
          EtiquetaModel etiqueta,
          List<EtiquetaCampoModel> campos,
          List<IngresoModel> seriales,
          EtiquetaDatosGeneralesDTO datosGenerales) {

    if ((plantillaBasePath == null || plantillaBasePath.isBlank())
        && etiqueta != null && etiqueta.getTipo() != null) {
      String resolved = prnPathResolver.resolvePath(etiqueta.getTipo());
      if (resolved != null) {
        plantillaBasePath = resolved;
      }
    }

    StringBuilder zplFinal = new StringBuilder();

    boolean reemplazarBox = esTipoEmpaque(etiqueta.getTipo()) && esReemplazoBoxActivo();

    int totalSeriales = seriales.size();
    int porImpresion = etiqueta.getImpresion();
    int cociente = totalSeriales / porImpresion;
    int residuo = totalSeriales % porImpresion;
    int contador = 0;

    // 🧱 1. Procesar bloques completos
    for (int i = 0; i < cociente; i++) {
      String plantillaPath = String.format("%s\\%s\\codigo%d.prn",
              plantillaBasePath, etiqueta.getNombre(), porImpresion);
      String zpl = leerPlantilla(plantillaPath);
      List<IngresoModel> subl = seriales.subList(contador, contador + porImpresion);
      zpl = reemplazarCampos(zpl, subl, campos);
      zpl = reemplazarDatosGenerales(zpl, datosGenerales);
      zpl = reemplazarGeneralConSufijos(zpl, subl, datosGenerales);
      if (reemplazarBox) {
        zpl = reemplazarVariableBox(zpl, datosGenerales.getCaja());
      }
      zplFinal.append(zpl).append("\n^XZ###DELIMITER_ZPL###^XA\n");
      contador += porImpresion;
    }

    // 🧩 2. Procesar bloque restante (residuo)
    if (residuo > 0) {
      String plantillaPath = String.format("%s\\%s\\codigo%d.prn",
              plantillaBasePath, etiqueta.getNombre(), residuo);
      String zpl = leerPlantilla(plantillaPath);
      List<IngresoModel> subl = seriales.subList(contador, contador + residuo);
      zpl = reemplazarCampos(zpl, subl, campos);
      zpl = reemplazarDatosGenerales(zpl, datosGenerales);
      zpl = reemplazarGeneralConSufijos(zpl, subl, datosGenerales);
      if (reemplazarBox) {
        zpl = reemplazarVariableBox(zpl, datosGenerales.getCaja());
      }
      zplFinal.append(zpl);
    }

    return zplFinal.toString();
  }

  private String leerPlantilla(String ruta) {
    try (BufferedReader br = new BufferedReader(new FileReader(new File(ruta)))) {
      return br.lines().reduce("", (a, b) -> a + b + "\n");
    } catch (IOException e) {
      throw new RuntimeException("Error al leer plantilla ZPL: " + e.getMessage());
    }
  }

  private String reemplazarCampos(String zpl, List<IngresoModel> seriales, List<EtiquetaCampoModel> campos) {

    // ✅ Ordenar campos de más largo a más corto para evitar reemplazos parciales
    List<EtiquetaCampoModel> camposOrdenados = campos.stream()
            .sorted((a, b) -> b.getNombre().length() - a.getNombre().length())
            .collect(java.util.stream.Collectors.toList());

    for (int i = 0; i < seriales.size(); i++) {
      IngresoModel ingreso = seriales.get(i);
      for (EtiquetaCampoModel ecm : camposOrdenados) {
        String key = getKey(i, ecm.getNombre());
        String valorCampo = obtenerValorCampo(ingreso, ecm.getNombre());
        if (valorCampo != null) {
          zpl = zpl.replace(key, valorCampo);
        }
      }
    }
    return zpl;
  }

  private String obtenerValorCampo(IngresoModel ingreso, String nombreCampo) {

    if ("virtual".equalsIgnoreCase(nombreCampo)) {
      String serial3 = ingreso.getSerial3();
      boolean tieneSerial3 = serial3 != null
              && !serial3.isEmpty()
              && !serial3.equalsIgnoreCase("nan")
              && !serial3.equals("0");
      return tieneSerial3 ? serial3 : (ingreso.getSmartCard() != null ? ingreso.getSmartCard() : "");
    }

    if ("smartCardSerial".equalsIgnoreCase(nombreCampo)) {
      return ingreso.getSmartCard() != null ? ingreso.getSmartCard() : "";
    }

    if (esNumerico(nombreCampo)) {
      return getValueByLegacyEmpaqueIndex(ingreso, nombreCampo);
    }

    String propiedad = resolverPropiedad(nombreCampo);
    try {
      java.lang.reflect.Field field = ingreso.getClass().getDeclaredField(propiedad);
      field.setAccessible(true);
      Object valor = field.get(ingreso);
      if (valor == null) return "";
      String strVal = valor.toString();
      if (strVal.isEmpty() && "codigosap".equalsIgnoreCase(nombreCampo)) return null;
      return strVal;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.err.println("Campo no encontrado en IngresoModel: " + nombreCampo + " (buscado: " + propiedad + ")");
      return "";
    }
  }

  private boolean esNumerico(String valor) {
    try {
      Integer.parseInt(valor);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String resolverPropiedad(String nombreCampo) {
    switch (nombreCampo.toLowerCase()) {
      case "codigosap": return "codigoSap";
      case "lote":       return "Lote";
      default:
        return nombreCampo.substring(0, 1).toLowerCase() + nombreCampo.substring(1);
    }
  }

  private String getValueByLegacyEmpaqueIndex(IngresoModel ingreso, String columnaIndex) {
    switch (columnaIndex) {
      case "0":  return ingreso.getSerial() != null ? ingreso.getSerial() : "";
      case "1":  return ingreso.getMac() != null ? ingreso.getMac() : "";
      case "2":  return ingreso.getSmartCard() != null ? ingreso.getSmartCard() : "";
      case "3":  return ingreso.getSerial3() != null ? ingreso.getSerial3() : "";
      case "4":  return ingreso.getSerial4() != null ? ingreso.getSerial4() : "";
      case "5":  return ingreso.getCodigoSap() != null ? ingreso.getCodigoSap() : "";
      case "6":  return ingreso.getDescripcion() != null ? ingreso.getDescripcion() : "";
      case "7":  return ingreso.getNivel() != null ? ingreso.getNivel() : "";
      case "8":  return ingreso.getLote() != null ? ingreso.getLote() : "";
      case "9":  return ingreso.getNumeroSmartcard() != null ? ingreso.getNumeroSmartcard() : "";
      case "10": return ingreso.getModelo() != null ? ingreso.getModelo() : "";
      default:   return "";
    }
  }

  private String reemplazarDatosGenerales(String zpl, EtiquetaDatosGeneralesDTO datos) {

    System.out.println("========== DEBUG EtiquetaDatosGeneralesDTO ==========");
    System.out.println("familia:              " + datos.getFamilia());
    System.out.println("descripcion:          " + datos.getDescripcion());
    System.out.println("codigosap:            " + datos.getCodigosap());
    System.out.println("usuario:              " + datos.getUsuario());
    System.out.println("tipologia:            " + datos.getTipologia());
    System.out.println("modelo:               " + datos.getModelo());
    System.out.println("codProveedor:         " + datos.getCodProveedor());
    System.out.println("proveedor:            " + datos.getProveedor());
    System.out.println("lote:                 " + datos.getLote());
    System.out.println("numberBox:            " + datos.getNumberBox());
    System.out.println("pallet:               " + datos.getPallet());
    System.out.println("caja:                 " + datos.getCaja());
    System.out.println("fecha:                " + datos.getFecha());
    System.out.println("smartCardSerial:      " + datos.getSmartCardSerial());
    System.out.println("smartCardCodigoSap:   " + datos.getSmartCardCodigoSap());
    System.out.println("smartCardDescripcion: " + datos.getSmartCardDescripcion());
    System.out.println("etiquetaUnitaria:     " + datos.getEtiquetaUnitaria());
    System.out.println("modelCodigo:          " + datos.getModelCodigo());
    System.out.println("modelDescripcion:     " + datos.getModelDescripcion());
    System.out.println("modelDetalle:         " + datos.getModelDetalle());
    System.out.println("=====================================================");

    // ✅ Primero los más específicos (smartCard* y model*) para evitar colisiones
    if (datos.getSmartCardSerial() != null) {
      zpl = reemplazarSeguro(zpl, "smartCardSerial",      datos.getSmartCardSerial());
      zpl = reemplazarSeguro(zpl, "smartCardCodigoSap",   datos.getSmartCardCodigoSap());
      zpl = reemplazarSeguro(zpl, "smartCardDescripcion", datos.getSmartCardDescripcion());
    }

    if (datos.getEtiquetaUnitaria() != null) {
      zpl = reemplazarSeguro(zpl, "codProveedor",     datos.getCodProveedor());
      zpl = reemplazarSeguro(zpl, "proveedor",        datos.getProveedor());
      zpl = reemplazarSeguro(zpl, "modelCodigo",      datos.getModelCodigo());
      zpl = reemplazarSeguro(zpl, "modelDescripcion", datos.getModelDescripcion());
      zpl = reemplazarSeguro(zpl, "modelDetalle",     datos.getModelDetalle());
    }

    // ✅ Luego los generales
    zpl = reemplazarSeguro(zpl, "familia",    datos.getFamilia());
    zpl = reemplazarSeguro(zpl, "descripcion", datos.getDescripcion());
    zpl = reemplazarSeguro(zpl, "codigosap",  datos.getCodigosap());
    zpl = reemplazarSeguro(zpl, "usuario",    datos.getUsuario());
    zpl = reemplazarSeguro(zpl, "tipologia",  datos.getTipologia());
    zpl = reemplazarSeguro(zpl, "modelo",     datos.getModelo());
    zpl = reemplazarSeguro(zpl, "lote",       datos.getLote());
    zpl = reemplazarSeguro(zpl, "pNumberBox", datos.getNumberBox());
    zpl = reemplazarSeguro(zpl, "pallet",     datos.getPallet());
    zpl = reemplazarSeguro(zpl, "caja",       "Caja: " + Objects.toString(datos.getCaja(), ""));
    zpl = reemplazarSeguro(zpl, "fecha",      datos.getFecha());

    return zpl;
  }

  private String reemplazarGeneralConSufijos(String zpl, List<IngresoModel> seriales, EtiquetaDatosGeneralesDTO datos) {
    for (int i = 0; i < seriales.size(); i++) {
      IngresoModel ingreso = seriales.get(i);
      String suffix = getKey(i, "");
      zpl = reemplazarSeguro(zpl, "serial" + suffix, nvl(ingreso.getSerial()));
      zpl = reemplazarSeguro(zpl, "mac" + suffix, nvl(ingreso.getMac()));
      zpl = reemplazarSeguro(zpl, "smartCard" + suffix, nvl(ingreso.getSmartCard()));
      zpl = reemplazarSeguro(zpl, "serial3" + suffix, nvl(ingreso.getSerial3()));
      zpl = reemplazarSeguro(zpl, "serial4" + suffix, nvl(ingreso.getSerial4()));
      zpl = reemplazarSeguro(zpl, "codigosap" + suffix, nvl(ingreso.getCodigoSap()));
      zpl = reemplazarSeguro(zpl, "descripcion" + suffix, nvl(ingreso.getDescripcion()));
      zpl = reemplazarSeguro(zpl, "nivel" + suffix, nvl(ingreso.getNivel()));
      zpl = reemplazarSeguro(zpl, "lote" + suffix, nvl(ingreso.getLote()));
      zpl = reemplazarSeguro(zpl, "modelo" + suffix, nvl(ingreso.getModelo()));
      zpl = reemplazarSeguro(zpl, "tipologia" + suffix, nvl(ingreso.getTipologia()));
    }
    return zpl;
  }

  private String getKey(int index, String nombre) {
    if (index < 9)  return nombre + (index + 1);
    if (index == 9) return nombre + "0";
    char letra = (char) ('A' + (index - 10));
    return nombre + letra;
  }

  private String nvl(String val) {
    return val != null ? val : "";
  }

  public void imprimirZpl(String zpl) throws PrintException {
    byte[] by = zpl.getBytes();
    DocFlavor docFormat = DocFlavor.BYTE_ARRAY.AUTOSENSE;
    SimpleDoc doc = new SimpleDoc(by, docFormat, null);

    PrinterJob pj = PrinterJob.getPrinterJob();
    if (pj.printDialog()) {
      String impresora = pj.getPrintService().getName();
      HashAttributeSet attributeSet = new HashAttributeSet();
      attributeSet.add(new PrinterName(impresora, Locale.getDefault()));
      PrintService[] services = PrintServiceLookup.lookupPrintServices(docFormat, attributeSet);
      if (services.length > 0) {
        DocPrintJob printJob = services[0].createPrintJob();
        printJob.print(doc, new HashPrintRequestAttributeSet());
      } else {
        throw new PrintException("No se encontró la impresora: " + impresora);
      }
    }
  }

  public ByteArrayOutputStream convertirZplAPdf(String zpl) throws Exception {
    PDDocument doc = new PDDocument();
    PDPage page = new PDPage(PDRectangle.LETTER);
    doc.addPage(page);
    PDPageContentStream content = new PDPageContentStream(doc, page);
    content.beginText();
    content.setFont(PDType1Font.COURIER, 8);
    content.setLeading(12f);
    content.newLineAtOffset(20, 700);
    for (String line : zpl.split("\n")) {
      content.showText(line);
      content.newLine();
    }
    content.endText();
    content.close();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    doc.save(baos);
    doc.close();
    return baos;
  }
}