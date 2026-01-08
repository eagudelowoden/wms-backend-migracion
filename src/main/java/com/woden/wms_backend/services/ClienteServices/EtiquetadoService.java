package com.woden.wms_backend.services.ClienteServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.EtiquetadoRequestDTO;
import com.woden.wms_backend.dto.clientDTO.EtiquetadoRequestDTO.DatosMaestroDTO;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EtiquetadoService extends BaseService<EtiquetadoModel, Integer> {

  @Autowired
  private EtiquetadoRepository etiquetadoRepository;

  public Integer insertEtiquetado(String serial, String mac, String variable1, String variable2,
      String variable3, String variable4, Integer reImpresion, Integer usuarioId, String fecha) {
    try {
      etiquetadoRepository.insertEtiquetado(serial, mac, variable1, variable2, variable3,
          variable4, reImpresion, usuarioId, fecha);
      return 1;
    } catch (Exception e) {
      System.err.println("Error insertando etiquetado: " + e.getMessage());
      return 0;
    }
  }

  /**
   * 🏗️ GENERA CÓDIGO ZPL COMPLETO
   */
  public String generarCodigoZpl(EtiquetadoRequestDTO request) {
    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║          🏗️  INICIANDO GENERACIÓN DE ZPL                      ║");
    System.out.println("╚════════════════════════════════════════════════════════════════╝");

    StringBuilder zplFinal = new StringBuilder();

    List<IngresoModel> seriales = request.getListaSeriales();
    int totalSeriales = seriales.size();
    int porImpresion = request.getEtiqueta().getImpresion();

    String labelDate = request.getDatosGenerales().getFecha();
    String usuario = request.getDatosGenerales().getUsuario();
    Boolean usarLecturaVariables = request.getLecturaVariables() != null && request.getLecturaVariables();

    System.out.println("📊 DATOS DE ENTRADA:");
    System.out.println("   • Total seriales: " + totalSeriales);
    System.out.println("   • Impresión por hoja: " + porImpresion);
    System.out.println("   • Fecha: " + labelDate);
    System.out.println("   • Usuario: " + usuario);
    System.out.println("   • Lectura variables: " + usarLecturaVariables);
    System.out.println();

    List<DatosMaestroDTO> datosMaestro = usarLecturaVariables && request.getDatosAdicionales() != null
        ? request.getDatosAdicionales().getDatosMaestro()
        : null;

    if (usarLecturaVariables && datosMaestro != null) {
      System.out.println("📦 DATOS MAESTRO RECIBIDOS:");
      for (int i = 0; i < datosMaestro.size(); i++) {
        DatosMaestroDTO d = datosMaestro.get(i);
        System.out.println("   Serial " + (i + 1) + ": " + d.getSerial());
        System.out.println("      Familia: " + d.getFamilia());
        System.out.println("      Variable1: '" + d.getVariable1() + "'");
        System.out.println("      Variable2: '" + d.getVariable2() + "'");
        System.out.println("      Variable3: '" + d.getVariable3() + "'");
        System.out.println("      Variable4: '" + d.getVariable4() + "'");
      }
      System.out.println();
    }

    // 🔹 CASO 1: Seriales <= impresión por hoja
    if (totalSeriales <= porImpresion) {
      System.out.println("🔹 CASO 1: UNA hoja con " + totalSeriales + " etiquetas\n");

      String plantilla = leerPlantilla(
          request.getRutaPlantillas(),
          request.getEtiqueta().getNombre(),
          totalSeriales);

      String zplCommand = plantilla;

      for (int i = 0; i < totalSeriales; i++) {
        IngresoModel serial = seriales.get(i);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏷️  SERIAL " + (i + 1) + "/" + totalSeriales + ": " + serial.getSerial());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (i >= 9) {
          zplCommand = procesarSerialConSwitch(zplCommand, serial, i,
              request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
        } else {
          zplCommand = procesarSerialNormal(zplCommand, serial, i,
              request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
        }

        System.out.println();
      }

      zplFinal.append(zplCommand);

    } else {
      // 🔹 CASO 2: Múltiples hojas
      int cociente = totalSeriales / porImpresion;
      int residuo = totalSeriales % porImpresion;
      int contador = 0;

      System.out.println("🔹 CASO 2: " + cociente + " hojas completas + " + residuo + " residuo\n");

      for (int h = 0; h < cociente; h++) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       📄 HOJA " + (h + 1) + "/" + cociente + "              ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            porImpresion);

        String zplCommand = plantilla;

        for (int j = 0; j < porImpresion; j++) {
          IngresoModel serial = seriales.get(contador);

          System.out.println("🏷️  SERIAL " + (contador + 1) + " [Pos " + (j + 1) + "]: " + serial.getSerial());

          if (j >= 9) {
            zplCommand = procesarSerialConSwitch(zplCommand, serial, contador,
                request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
          } else {
            zplCommand = procesarSerialNormal(zplCommand, serial, j,
                request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
          }

          contador++;
        }

        zplFinal.append(zplCommand);
      }

      if (residuo > 0) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║       📄 HOJA RESIDUO (" + residuo + ")        ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            residuo);

        String zplCommand = plantilla;

        for (int i = 0; i < residuo; i++) {
          IngresoModel serial = seriales.get(contador);

          System.out.println("🏷️  SERIAL " + (contador + 1) + " [Residuo " + (i + 1) + "]: " + serial.getSerial());

          if (i >= 9) {
            zplCommand = procesarSerialConSwitch(zplCommand, serial, contador,
                request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
          } else {
            zplCommand = procesarSerialNormal(zplCommand, serial, i,
                request.getCamposConfigurados(), datosMaestro, labelDate, usuario, usarLecturaVariables);
          }

          contador++;
        }

        zplFinal.append(zplCommand);
      }
    }

    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║          ✅ ZPL COMPLETADO (" + zplFinal.length() + " chars)");
    System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

    return zplFinal.toString();
  }

  /**
   * ✅ PROCESA SERIAL NORMAL (índices 0-8)
   */
  private String procesarSerialNormal(String zpl, IngresoModel serial, int index,
      List<EtiquetaCampoModel> campos, List<DatosMaestroDTO> datosMaestro,
      String labelDate, String usuario, boolean usarLecturaVariables) {

    String sufijo = String.valueOf(index + 1);

    System.out.println("   ➜ Sufijo: " + sufijo);
    System.out.println("   📋 PASO 1: Campos configurados");

    // 1. Campos configurados
    for (EtiquetaCampoModel campo : campos) {
      String nombreVar = campo.getNombre() + sufijo;
      String valor = obtenerValorPorColumna(serial, campo.getValor());
      System.out.println("      • " + nombreVar + " = '" + valor + "'");
      zpl = zpl.replace(nombreVar, valor);
    }

    // 2. Variables estándar (SIEMPRE)
    System.out.println("   📋 PASO 2: Variables estándar");
    String fechaVal = labelDate != null ? labelDate : "";
    String unitSerial3Val = serial.getSerial3() != null ? serial.getSerial3() : "";
    String unitSerial4Val = serial.getSerial4() != null ? serial.getSerial4() : "";

    System.out.println("      • fecha" + sufijo + " = '" + fechaVal + "'");
    System.out.println("      • unitSerial3" + sufijo + " = '" + unitSerial3Val + "'");
    System.out.println("      • unitSerial4" + sufijo + " = '" + unitSerial4Val + "'");

    zpl = zpl.replace("fecha" + sufijo, fechaVal);
    zpl = zpl.replace("unitSerial3" + sufijo, unitSerial3Val);
    zpl = zpl.replace("unitSerial4" + sufijo, unitSerial4Val);

    // 3. Variables adicionales
    if (usarLecturaVariables && datosMaestro != null && index < datosMaestro.size()) {
      System.out.println("   📋 PASO 3: Variables adicionales (lectura ACTIVA)");
      DatosMaestroDTO datos = datosMaestro.get(index);
      zpl = updateZplCommand(zpl, sufijo, serial, datos, usuario, labelDate);
    } else {
      System.out.println("   📋 PASO 3: OMITIDO (lectura INACTIVA)");
    }

    return zpl;
  }

  /**
   * ✅ PROCESA SERIAL CON SWITCH (índices >= 9)
   */
  private String procesarSerialConSwitch(String zpl, IngresoModel serial, int index,
      List<EtiquetaCampoModel> campos, List<DatosMaestroDTO> datosMaestro,
      String labelDate, String usuario, boolean usarLecturaVariables) {

    String sufijo = obtenerSufijoSwitch(index);

    System.out.println("   ➜ Sufijo (switch): " + sufijo);
    System.out.println("   📋 PASO 1: Campos configurados (switch)");

    for (EtiquetaCampoModel campo : campos) {
      String nombreVar = campo.getNombre() + sufijo;
      String valor = obtenerValorPorColumna(serial, campo.getValor());
      System.out.println("      • " + nombreVar + " = '" + valor + "'");
      zpl = zpl.replace(nombreVar, valor);
    }

    if (usarLecturaVariables && datosMaestro != null && index < datosMaestro.size()) {
      System.out.println("   📋 PASO 2: Variables adicionales (switchCase)");
      DatosMaestroDTO datos = datosMaestro.get(index);
      zpl = updateZplCommand(zpl, sufijo, serial, datos, usuario, labelDate);
    } else {
      System.out.println("   📋 PASO 2: Variables básicas solamente");
      zpl = zpl.replace("fecha" + sufijo, labelDate != null ? labelDate : "");
      zpl = zpl.replace("unitSerial3" + sufijo, serial.getSerial3() != null ? serial.getSerial3() : "");
      zpl = zpl.replace("unitSerial4" + sufijo, serial.getSerial4() != null ? serial.getSerial4() : "");
    }

    return zpl;
  }

  /**
   * ✅ UPDATE ZPL COMMAND - TODAS LAS VARIABLES
   */
  private String updateZplCommand(String zpl, String sufijo, IngresoModel serial,
      DatosMaestroDTO datos, String usuario, String labelDate) {

    System.out.println("      🔄 updateZplCommand() - Reemplazando:");

    // Variables del maestro
    zpl = zpl.replace("familia" + sufijo, datos.getFamilia() != null ? datos.getFamilia() : "");
    System.out.println("         ✓ familia" + sufijo + " = '" + datos.getFamilia() + "'");

    zpl = zpl.replace("descripcion" + sufijo, cortarString(serial.getDescripcion(), 35));
    zpl = zpl.replace("fecha" + sufijo, labelDate != null ? labelDate : "");
    zpl = zpl.replace("codigosap" + sufijo, serial.getCodigoSap() != null ? serial.getCodigoSap() : "");
    zpl = zpl.replace("usuario" + sufijo, usuario != null ? usuario : "");
    zpl = zpl.replace("tipologia" + sufijo, serial.getTipologia() != null ? serial.getTipologia() : "");
    zpl = zpl.replace("modelo" + sufijo, datos.getModelo() != null ? datos.getModelo() : "");
    zpl = zpl.replace("codProveedor" + sufijo, datos.getCodProveedor() != null ? datos.getCodProveedor() : "");
    zpl = zpl.replace("proveedor" + sufijo, datos.getProveedor() != null ? datos.getProveedor() : "");
    zpl = zpl.replace("lote" + sufijo, serial.getLote() != null ? serial.getLote() : "");
    zpl = zpl.replace("passModel" + sufijo, datos.getPassModel() != null ? datos.getPassModel() : "");
    zpl = zpl.replace("codeinModel" + sufijo, datos.getCodeInModel() != null ? datos.getCodeInModel() : "");
    zpl = zpl.replace("unitSerial3" + sufijo, serial.getSerial3() != null ? serial.getSerial3() : "");
    zpl = zpl.replace("unitSerial4" + sufijo, serial.getSerial4() != null ? serial.getSerial4() : "");

    // ✅ CRÍTICO: Variables 1-4 con underscore
    String var1 = datos.getVariable1() != null ? datos.getVariable1() : "";
    String var2 = datos.getVariable2() != null ? datos.getVariable2() : "";
    String var3 = datos.getVariable3() != null ? datos.getVariable3() : "";
    String var4 = datos.getVariable4() != null ? datos.getVariable4() : "";

    System.out.println("         ✓ variable1_" + sufijo + " = '" + var1 + "'");
    System.out.println("         ✓ variable2_" + sufijo + " = '" + var2 + "'");
    System.out.println("         ✓ variable3_" + sufijo + " = '" + var3 + "'");
    System.out.println("         ✓ variable4_" + sufijo + " = '" + var4 + "'");

    zpl = zpl.replace("variable1_" + sufijo, var1);
    zpl = zpl.replace("variable2_" + sufijo, var2);
    zpl = zpl.replace("variable3_" + sufijo, var3);
    zpl = zpl.replace("variable4_" + sufijo, var4);

    return zpl;
  }

  private String obtenerSufijoSwitch(int index) {
    if (index == 9)
      return "0";
    if (index >= 10)
      return String.valueOf((char) ('A' + (index - 10)));
    return String.valueOf(index + 1);
  }

  private String obtenerValorPorColumna(IngresoModel model, String columnaIndex) {
    switch (columnaIndex) {
      case "0":
        return model.getSerial() != null ? model.getSerial() : "";
      case "1":
        return model.getMac() != null ? model.getMac() : "";
      case "3":
        return model.getSerial3() != null ? model.getSerial3() : "";
      case "4":
        return model.getSerial4() != null ? model.getSerial4() : "";
      case "5":
        return model.getTipologia() != null ? model.getTipologia() : "";
      case "6":
        return model.getCodigoSap() != null ? model.getCodigoSap() : "";
      case "7":
        return model.getDescripcion() != null ? model.getDescripcion() : "";
      case "8":
        return model.getLote() != null ? model.getLote() : "";
      default:
        return "";
    }
  }

  private String leerPlantilla(String base, String nombre, int cantidad) {
    String path = base + File.separator + nombre + File.separator + "codigo" + cantidad + ".prn";
    System.out.println("\n📁 Leyendo: " + path);

    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      StringBuilder sb = new StringBuilder();
      String linea;
      while ((linea = br.readLine()) != null) {
        sb.append(linea).append("\n");
      }
      System.out.println("   ✅ Cargada: " + sb.length() + " chars\n");
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException("Plantilla no encontrada: " + path);
    }
  }

  private String cortarString(String str, int len) {
    if (str == null)
      return "";
    return str.length() > len ? str.substring(0, len) : str;
  }
}