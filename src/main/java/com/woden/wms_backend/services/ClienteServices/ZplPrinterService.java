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

// import javax.print.*;
// import javax.print.attribute.*;
// import javax.print.attribute.standard.*;

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

import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.EtiquetaDatosGeneralesDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Service
public class ZplPrinterService {

  public String generarZpl(
      String plantillaBasePath,
      EtiquetaModel etiqueta,
      List<EtiquetaCampoModel> campos,
      List<IngresoModel> seriales,
      EtiquetaDatosGeneralesDTO datosGenerales) {

    StringBuilder zplFinal = new StringBuilder();

    int totalSeriales = seriales.size();
    int porImpresion = etiqueta.getImpresion();
    int cociente = totalSeriales / porImpresion;
    int residuo = totalSeriales % porImpresion;

    int contador = 0;

    // 🧱 1. Procesar bloques completos
    for (int i = 0; i < cociente; i++) {
      String plantillaPath = String.format("%s\\%s\\codigo%d.prn", plantillaBasePath, etiqueta.getNombre(),
          porImpresion);
      String zpl = leerPlantilla(plantillaPath);

      zpl = reemplazarCampos(zpl, seriales.subList(contador, contador + porImpresion), campos);
      zpl = reemplazarDatosGenerales(zpl, datosGenerales);
      zplFinal.append(zpl).append("\n^XZ###DELIMITER_ZPL###^XA\n");
      contador += porImpresion;
    }

    // 🧩 2. Procesar bloque restante (residuo)
    if (residuo > 0) {
      String plantillaPath = String.format("%s\\%s\\codigo%d.prn", plantillaBasePath, etiqueta.getNombre(), residuo);
      String zpl = leerPlantilla(plantillaPath);
      zpl = reemplazarCampos(zpl, seriales.subList(contador, contador + residuo), campos);
      zpl = reemplazarDatosGenerales(zpl, datosGenerales);
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
    for (int i = 0; i < seriales.size(); i++) {
      IngresoModel ingreso = seriales.get(i);

      for (EtiquetaCampoModel ecm : campos) {
        String key = getKey(i, ecm.getNombre());
        String nombreCampo = ecm.getNombre();
        String valorCampo = obtenerValorCampo(ingreso, nombreCampo);

        if (valorCampo != null) {
          zpl = zpl.replace(key, valorCampo);
        }
      }
    }
    return zpl;
  }

  private String obtenerValorCampo(IngresoModel ingreso, String nombreCampo) {
    try {
      // Convierte el nombre a formato de propiedad (por si viene en mayúsculas)
      String propiedad = nombreCampo.substring(0, 1).toLowerCase() + nombreCampo.substring(1);
      Field field = ingreso.getClass().getDeclaredField(propiedad);
      field.setAccessible(true);
      Object valor = field.get(ingreso);
      return valor != null ? valor.toString() : "";
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.err.println("⚠️ Campo no encontrado en IngresoModel: " + nombreCampo);
      return "";
    }
  }

  private String reemplazarDatosGenerales(String zpl, EtiquetaDatosGeneralesDTO datos) {
    zpl = zpl.replace("familia", Objects.toString(datos.getFamilia(), ""));
    zpl = zpl.replace("descripcion", Objects.toString(datos.getDescripcion(), ""));
    zpl = zpl.replace("codigosap", Objects.toString(datos.getCodigosap(), ""));
    zpl = zpl.replace("usuario", Objects.toString(datos.getUsuario(), ""));
    zpl = zpl.replace("tipologia", Objects.toString(datos.getTipologia(), ""));
    zpl = zpl.replace("modelo", Objects.toString(datos.getModelo(), ""));
    zpl = zpl.replace("codProveedor", Objects.toString(datos.getCodProveedor(), ""));
    zpl = zpl.replace("proveedor", Objects.toString(datos.getProveedor(), ""));
    zpl = zpl.replace("lote", Objects.toString(datos.getLote(), ""));
    zpl = zpl.replace("pNumberBox", Objects.toString(datos.getNumberBox(), ""));
    zpl = zpl.replace("pallet", Objects.toString(datos.getPallet(), ""));
    zpl = zpl.replace("caja", Objects.toString(datos.getCaja(), ""));
    zpl = zpl.replace("fecha", Objects.toString(datos.getFecha(), ""));

    if (datos.getSmartCardSerial() != null) {
      zpl = zpl.replace("smartCardSerial", Objects.toString(datos.getSmartCardSerial(), ""));
      zpl = zpl.replace("smartCardCodigoSap", Objects.toString(datos.getSmartCardCodigoSap(), ""));
      zpl = zpl.replace("smartCardDescripcion", Objects.toString(datos.getSmartCardDescripcion(), ""));
    }

    return zpl;
  }

  private String getKey(int index, String nombre) {
    if (index < 9) {
      return nombre + (index + 1);
    }

    if (index == 9) {
      return nombre + "0";
    }

    char letra = (char) ('A' + (index - 10));
    return nombre + letra;
  }

  public void imprimirZpl(String zpl) throws PrintException {
    byte[] by = zpl.getBytes();
    DocFlavor docFormat = DocFlavor.BYTE_ARRAY.AUTOSENSE;
    SimpleDoc doc = new SimpleDoc(by, docFormat, null);

    // Mostrar selector de impresora (opcional)
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

    // Aquí dibujamos el texto ZPL crudo como ejemplo
    // Para un render real, se puede usar librerías que conviertan ZPL a imagen y
    // luego a PDF
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
