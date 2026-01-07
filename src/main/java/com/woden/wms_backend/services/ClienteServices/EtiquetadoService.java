package com.woden.wms_backend.services.ClienteServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

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

  // Variables para datos adicionales (igual que en Swing)
  private int searchLabelVariables = 1; // Flag para activar búsqueda de variables adicionales

  /**
   * 💾 Inserta registro de etiquetado en BD
   */
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
   * 🏗️ GENERA CÓDIGO ZPL COMPLETO CON LOGS DETALLADOS
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
    System.out.println("   • Usar lectura variables: " + usarLecturaVariables);
    System.out.println("   • Ruta plantillas: " + request.getRutaPlantillas());
    System.out.println("   • Etiqueta: " + request.getEtiqueta().getNombre());
    System.out.println("   • Campos configurados: " + request.getCamposConfigurados().size());

    // Obtener datos adicionales del frontend
    List<DatosMaestroDTO> datosMaestro = usarLecturaVariables && request.getDatosAdicionales() != null
        ? request.getDatosAdicionales().getDatosMaestro()
        : null;

    if (usarLecturaVariables) {
      System.out.println("   • Datos maestro recibidos: " + (datosMaestro != null ? datosMaestro.size() : 0));
    }
    System.out.println();

    // 🔹 CASO 1: Seriales <= impresión por hoja
    if (totalSeriales <= porImpresion) {
      System.out
          .println("🔹 CASO 1: Total seriales (" + totalSeriales + ") <= Impresión por hoja (" + porImpresion + ")");
      System.out.println("   ➜ Generando UNA sola hoja con " + totalSeriales + " etiquetas\n");

      String plantilla = leerPlantilla(
          request.getRutaPlantillas(),
          request.getEtiqueta().getNombre(),
          totalSeriales);

      String zplCommand = plantilla;
      System.out.println("✅ Plantilla cargada: " + plantilla.length() + " caracteres\n");

      for (int i = 0; i < totalSeriales; i++) {
        IngresoModel serial = seriales.get(i);
        String sufijo = obtenerSufijo(i);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏷️  PROCESANDO SERIAL " + (i + 1) + "/" + totalSeriales);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   Serial: " + serial.getSerial());
        System.out.println("   MAC: " + serial.getMac());
        System.out.println("   Sufijo: " + sufijo);
        System.out.println();

        // A. Reemplazar campos configurados
        System.out.println("   📋 PASO 1: Reemplazando campos configurados");
        zplCommand = reemplazarCamposConfigurados(zplCommand, serial,
            request.getCamposConfigurados(), sufijo);

        // B. Reemplazar variables estándar
        System.out.println("   📋 PASO 2: Reemplazando variables estándar");
        zplCommand = reemplazarVariablesEstandar(zplCommand, serial, sufijo, labelDate);

        // C. Reemplazar variables adicionales (si existen)
        if (usarLecturaVariables && datosMaestro != null && i < datosMaestro.size()) {
          System.out.println("   📋 PASO 3: Reemplazando variables adicionales (lectura maestro ACTIVA)");
          DatosMaestroDTO datos = datosMaestro.get(i);
          zplCommand = reemplazarVariablesAdicionales(zplCommand, serial, datos, sufijo, usuario);
        } else {
          System.out.println("   📋 PASO 3: Omitiendo variables adicionales (lectura maestro INACTIVA)");
        }

        System.out.println();
      }

      zplFinal.append(zplCommand);

    } else {
      // 🔹 CASO 2: Seriales > impresión por hoja
      int cociente = totalSeriales / porImpresion;
      int residuo = totalSeriales % porImpresion;
      int contador = 0;

      System.out
          .println("🔹 CASO 2: Total seriales (" + totalSeriales + ") > Impresión por hoja (" + porImpresion + ")");
      System.out.println("   ➜ Generando " + cociente + " hojas completas");
      if (residuo > 0) {
        System.out.println("   ➜ Más 1 hoja con " + residuo + " etiquetas (residuo)");
      }
      System.out.println();

      // Procesar hojas completas
      for (int h = 0; h < cociente; h++) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out
            .println("║              📄 HOJA " + (h + 1) + "/" + cociente + " (completa)                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            porImpresion);

        String zplCommand = plantilla;
        System.out.println("✅ Plantilla cargada: " + plantilla.length() + " caracteres\n");

        for (int j = 0; j < porImpresion; j++) {
          IngresoModel serial = seriales.get(contador);
          String sufijo = obtenerSufijo(j);

          System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
          System.out.println(
              "🏷️  PROCESANDO SERIAL " + (contador + 1) + "/" + totalSeriales + " (Posición " + (j + 1) + " en hoja)");
          System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
          System.out.println("   Serial: " + serial.getSerial());
          System.out.println("   Sufijo: " + sufijo);
          System.out.println();

          zplCommand = reemplazarCamposConfigurados(zplCommand, serial,
              request.getCamposConfigurados(), sufijo);
          zplCommand = reemplazarVariablesEstandar(zplCommand, serial, sufijo, labelDate);

          if (usarLecturaVariables && datosMaestro != null && contador < datosMaestro.size()) {
            System.out.println("   📋 PASO 3: Reemplazando variables adicionales");
            DatosMaestroDTO datos = datosMaestro.get(contador);
            zplCommand = reemplazarVariablesAdicionales(zplCommand, serial, datos, sufijo, usuario);
          }

          contador++;
          System.out.println();
        }

        zplFinal.append(zplCommand);
      }

      // Procesar residuo
      if (residuo > 0) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              📄 HOJA RESIDUO (con " + residuo + " etiquetas)              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            residuo);

        String zplCommand = plantilla;
        System.out.println("✅ Plantilla cargada: " + plantilla.length() + " caracteres\n");

        for (int i = 0; i < residuo; i++) {
          IngresoModel serial = seriales.get(contador);
          String sufijo = obtenerSufijo(i);

          System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
          System.out.println("🏷️  PROCESANDO SERIAL " + (contador + 1) + "/" + totalSeriales + " (Residuo " + (i + 1)
              + "/" + residuo + ")");
          System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
          System.out.println("   Serial: " + serial.getSerial());
          System.out.println("   Sufijo: " + sufijo);
          System.out.println();

          zplCommand = reemplazarCamposConfigurados(zplCommand, serial,
              request.getCamposConfigurados(), sufijo);
          zplCommand = reemplazarVariablesEstandar(zplCommand, serial, sufijo, labelDate);

          if (usarLecturaVariables && datosMaestro != null && contador < datosMaestro.size()) {
            System.out.println("   📋 PASO 3: Reemplazando variables adicionales");
            DatosMaestroDTO datos = datosMaestro.get(contador);
            zplCommand = reemplazarVariablesAdicionales(zplCommand, serial, datos, sufijo, usuario);
          }

          contador++;
          System.out.println();
        }

        zplFinal.append(zplCommand);
      }
    }

    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║          ✅ GENERACIÓN DE ZPL COMPLETADA                       ║");
    System.out.println("╚════════════════════════════════════════════════════════════════╝");
    System.out.println("📏 ZPL Final: " + zplFinal.length() + " caracteres");
    System.out.println();

    return zplFinal.toString();
  }

  /**
   * Reemplaza campos configurados dinámicamente
   */
  private String reemplazarCamposConfigurados(String zpl, IngresoModel serial,
      List<EtiquetaCampoModel> campos, String sufijo) {
    System.out.println("      🔹 Campos configurados:");

    for (EtiquetaCampoModel campo : campos) {
      String nombreVariable = campo.getNombre() + sufijo;
      String valor = obtenerValorPorColumna(serial, campo.getValor());

      System.out.println("         • " + nombreVariable + " = '" + valor + "'");
      zpl = zpl.replace(nombreVariable, valor);
    }

    return zpl;
  }

  /**
   * Reemplaza variables estándar básicas
   */
  private String reemplazarVariablesEstandar(String zpl, IngresoModel serial,
      String sufijo, String fecha) {
    System.out.println("      🔹 Variables estándar:");

    String fechaVal = fecha != null ? fecha : "";
    String unitSerial3Val = serial.getSerial3() != null ? serial.getSerial3() : "";
    String unitSerial4Val = serial.getSerial4() != null ? serial.getSerial4() : "";
    String descripcionVal = cortarString(serial.getDescripcion(), 35);

    System.out.println("         • fecha" + sufijo + " = '" + fechaVal + "'");
    System.out.println("         • unitSerial3" + sufijo + " = '" + unitSerial3Val + "'");
    System.out.println("         • unitSerial4" + sufijo + " = '" + unitSerial4Val + "'");
    System.out.println("         • descripcion" + sufijo + " = '" + descripcionVal + "'");

    zpl = zpl.replace("fecha" + sufijo, fechaVal);
    zpl = zpl.replace("unitSerial3" + sufijo, unitSerial3Val);
    zpl = zpl.replace("unitSerial4" + sufijo, unitSerial4Val);
    zpl = zpl.replace("descripcion" + sufijo, descripcionVal);

    return zpl;
  }

  /**
   * ✅ Reemplaza TODAS las variables adicionales que vienen del frontend
   */
  private String reemplazarVariablesAdicionales(String zpl, IngresoModel serial,
      DatosMaestroDTO datos, String sufijo, String usuario) {

    System.out.println("      🔹 Variables adicionales (del maestro):");

    // Variables del maestro
    String familiaVal = datos.getFamilia() != null ? datos.getFamilia() : "";
    String modeloVal = datos.getModelo() != null ? datos.getModelo() : "";
    String codProveedorVal = datos.getCodProveedor() != null ? datos.getCodProveedor() : "";
    String proveedorVal = datos.getProveedor() != null ? datos.getProveedor() : "";

    System.out.println("         • familia" + sufijo + " = '" + familiaVal + "'");
    System.out.println("         • modelo" + sufijo + " = '" + modeloVal + "'");
    System.out.println("         • codProveedor" + sufijo + " = '" + codProveedorVal + "'");
    System.out.println("         • proveedor" + sufijo + " = '" + proveedorVal + "'");

    zpl = zpl.replace("familia" + sufijo, familiaVal);
    zpl = zpl.replace("modelo" + sufijo, modeloVal);
    zpl = zpl.replace("codProveedor" + sufijo, codProveedorVal);
    zpl = zpl.replace("proveedor" + sufijo, proveedorVal);

    // Variables calculadas
    String passModelVal = datos.getPassModel() != null ? datos.getPassModel() : "";
    String codeInModelVal = datos.getCodeInModel() != null ? datos.getCodeInModel() : "";

    System.out.println("         • passModel" + sufijo + " = '" + passModelVal + "'");
    System.out.println("         • codeinModel" + sufijo + " = '" + codeInModelVal + "'");

    zpl = zpl.replace("passModel" + sufijo, passModelVal);
    zpl = zpl.replace("codeinModel" + sufijo, codeInModelVal);

    // Variables del serial
    String codigoSapVal = serial.getCodigoSap() != null ? serial.getCodigoSap() : "";
    String tipologiaVal = serial.getTipologia() != null ? serial.getTipologia() : "";
    String loteVal = serial.getLote() != null ? serial.getLote() : "";

    System.out.println("         • codigosap" + sufijo + " = '" + codigoSapVal + "'");
    System.out.println("         • tipologia" + sufijo + " = '" + tipologiaVal + "'");
    System.out.println("         • lote" + sufijo + " = '" + loteVal + "'");

    zpl = zpl.replace("codigosap" + sufijo, codigoSapVal);
    zpl = zpl.replace("tipologia" + sufijo, tipologiaVal);
    zpl = zpl.replace("lote" + sufijo, loteVal);

    // Usuario
    String usuarioVal = usuario != null ? usuario : "";
    System.out.println("         • usuario" + sufijo + " = '" + usuarioVal + "'");
    zpl = zpl.replace("usuario" + sufijo, usuarioVal);

    // ✅ NUEVO: Variables 1, 2, 3, 4 (si existen en el modelo)
    if (datos.getVariable1() != null) {
      System.out.println("         • variable1" + sufijo + " = '" + datos.getVariable1() + "'");
      zpl = zpl.replace("variable1" + sufijo, datos.getVariable1());
    }
    if (datos.getVariable2() != null) {
      System.out.println("         • variable2" + sufijo + " = '" + datos.getVariable2() + "'");
      zpl = zpl.replace("variable2" + sufijo, datos.getVariable2());
    }
    if (datos.getVariable3() != null) {
      System.out.println("         • variable3" + sufijo + " = '" + datos.getVariable3() + "'");
      zpl = zpl.replace("variable3" + sufijo, datos.getVariable3());
    }
    if (datos.getVariable4() != null) {
      System.out.println("         • variable4" + sufijo + " = '" + datos.getVariable4() + "'");
      zpl = zpl.replace("variable4" + sufijo, datos.getVariable4());
    }

    return zpl;
  }

  /**
   * Obtiene sufijo según índice (1-9, 0, A-J)
   */
  private String obtenerSufijo(int index) {
    if (index < 9) {
      return String.valueOf(index + 1);
    } else if (index == 9) {
      return "0";
    } else if (index < 20) {
      return String.valueOf((char) ('A' + (index - 10)));
    }
    return String.valueOf(index + 1);
  }

  /**
   * Mapeo de columnas del modelo
   */
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

  /**
   * Lee plantilla desde disco
   */
  private String leerPlantilla(String base, String nombre, int cantidad) {
    String path = base + File.separator + nombre + File.separator +
        "codigo" + cantidad + ".prn";

    System.out.println("📁 Leyendo plantilla: " + path);

    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      StringBuilder sb = new StringBuilder();
      String linea;
      while ((linea = br.readLine()) != null) {
        sb.append(linea).append("\n");
      }
      return sb.toString();
    } catch (IOException e) {
      System.err.println("❌ ERROR: No se encontró la plantilla: " + path);
      throw new RuntimeException("No se encontró la plantilla: " + path);
    }
  }

  /**
   * Corta string a longitud máxima
   */
  private String cortarString(String str, int len) {
    if (str == null)
      return "";
    return str.length() > len ? str.substring(0, len) : str;
  }
}