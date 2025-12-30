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
   * 🏗️ GENERA CÓDIGO ZPL COMPLETO
   * Lógica 100% idéntica al código Swing
   */
  public String generarCodigoZpl(EtiquetadoRequestDTO request) {
    StringBuilder zplFinal = new StringBuilder();
    String zplCommand = "";
    String zplCommand1 = "";

    List<IngresoModel> seriales = request.getListaSeriales();
    int totalSeriales = seriales.size();
    int porImpresion = request.getEtiqueta().getImpresion();

    String labelDate = request.getDatosGenerales().getFecha();

    // 🔹 CASO 1: Cantidad de seriales <= impresión por hoja
    if (totalSeriales <= porImpresion) {
      String plantilla = leerPlantilla(
          request.getRutaPlantillas(),
          request.getEtiqueta().getNombre(),
          totalSeriales);

      zplCommand = plantilla;

      // Procesar cada serial
      for (int i = 0; i < totalSeriales; i++) {
        IngresoModel ingresoModel = seriales.get(i);

        // A. Reemplazar campos dinámicos configurados
        for (EtiquetaCampoModel ecm : request.getCamposConfigurados()) {
          String sufijo = obtenerSufijo(i);
          String valor = obtenerValorCampo(ingresoModel, ecm);

          if (i >= 9) {
            zplCommand = switchCaseEtiquetaCampo(i, ingresoModel, zplCommand, ecm, request.getCamposConfigurados());
          } else {
            zplCommand = zplCommand.replace(ecm.getNombre() + sufijo, valor);
          }
        }

        // B. Obtener valores adicionales del modelo
        DatosEtiqueta datosEtiqueta = getLabelValues(ingresoModel);

        // C. Reemplazar variables estándar
        if (i >= 9) {
          zplCommand = switchCaseLabelVariables(i, ingresoModel, zplCommand, datosEtiqueta, labelDate);
        } else {
          String sufijo = obtenerSufijo(i);
          zplCommand = reemplazarVariablesEstandar(zplCommand, sufijo, ingresoModel, datosEtiqueta, labelDate);

          // D. Si hay variables de label activas, reemplazarlas también
          if (this.searchLabelVariables == 1) {
            DatosEtiquetaRow datosRow = getLabelRowValues(ingresoModel);
            zplCommand = updateZplCommand(zplCommand, sufijo, datosEtiqueta, datosRow);
          }
        }
      }

      zplFinal.append(zplCommand);

    } else {
      // 🔹 CASO 2: Cantidad de seriales > impresión por hoja
      int cociente = totalSeriales / porImpresion;
      int residuo = totalSeriales % porImpresion;
      int contador = 0;

      // Procesar hojas completas
      for (int h = 0; h < cociente; h++) {
        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            porImpresion);

        zplCommand = plantilla;

        for (int j = 0; j < porImpresion; j++) {
          IngresoModel ingresoModel = seriales.get(contador);

          // Campos configurados
          for (EtiquetaCampoModel ecm : request.getCamposConfigurados()) {
            String sufijo = obtenerSufijo(j);
            String valor = obtenerValorCampo(ingresoModel, ecm);

            zplCommand = zplCommand.replace(ecm.getNombre() + sufijo, valor);

            if (j >= 9) {
              zplCommand = switchCaseEtiquetaCampo(j, ingresoModel, zplCommand, ecm, request.getCamposConfigurados());
            }
          }

          // Valores adicionales
          DatosEtiqueta datosEtiqueta = getLabelValues(ingresoModel);
          String sufijo = obtenerSufijo(j);

          zplCommand = reemplazarVariablesEstandar(zplCommand, sufijo, ingresoModel, datosEtiqueta, labelDate);

          if (this.searchLabelVariables == 1) {
            DatosEtiquetaRow datosRow = getLabelRowValues(ingresoModel);
            zplCommand = updateZplCommand(zplCommand, sufijo, datosEtiqueta, datosRow);
          }

          if (j >= 9) {
            zplCommand = switchCaseLabelVariables(j, ingresoModel, zplCommand, datosEtiqueta, labelDate);
          }

          contador++;
        }

        zplFinal.append(zplCommand);
      }

      // Procesar residuo
      if (residuo > 0) {
        String plantilla = leerPlantilla(
            request.getRutaPlantillas(),
            request.getEtiqueta().getNombre(),
            residuo);

        zplCommand1 = plantilla;

        for (int i = 0; i < residuo; i++) {
          IngresoModel ingresoModel = seriales.get(contador);

          // Campos configurados
          for (EtiquetaCampoModel ecm : request.getCamposConfigurados()) {
            String sufijo = obtenerSufijo(i);
            String valor = obtenerValorCampo(ingresoModel, ecm);

            if (i >= 9) {
              zplCommand1 = switchCaseEtiquetaCampo(i, ingresoModel, zplCommand1, ecm, request.getCamposConfigurados());
            } else {
              zplCommand1 = zplCommand1.replace(ecm.getNombre() + sufijo, valor);
            }
          }

          // Valores adicionales
          DatosEtiqueta datosEtiqueta = getLabelValues(ingresoModel);

          if (i >= 9) {
            zplCommand1 = switchCaseLabelVariables(i, ingresoModel, zplCommand1, datosEtiqueta, labelDate);
          } else {
            String sufijo = obtenerSufijo(i);
            zplCommand1 = reemplazarVariablesEstandar(zplCommand1, sufijo, ingresoModel, datosEtiqueta, labelDate);

            if (this.searchLabelVariables == 1) {
              DatosEtiquetaRow datosRow = getLabelRowValues(ingresoModel);
              zplCommand1 = updateZplCommand(zplCommand1, sufijo, datosEtiqueta, datosRow);
            }
          }

          contador++;
        }

        zplFinal.append(zplCommand1);
      }
    }

    return zplFinal.toString();
  }

  /**
   * 🔄 Reemplaza variables estándar (fecha, unitSerial3, unitSerial4,
   * descripcion)
   */
  private String reemplazarVariablesEstandar(String zpl, String sufijo, IngresoModel ingreso,
      DatosEtiqueta datos, String fecha) {
    zpl = zpl.replace("fecha" + sufijo, fecha != null ? fecha : "");
    zpl = zpl.replace("unitSerial3" + sufijo, ingreso.getSerial3() != null ? ingreso.getSerial3() : "");
    zpl = zpl.replace("unitSerial4" + sufijo, ingreso.getSerial4() != null ? ingreso.getSerial4() : "");
    zpl = zpl.replace("descripcion" + sufijo, cortarString(ingreso.getDescripcion(), 35));
    return zpl;
  }

  /**
   * 🔄 Actualiza ZPL con TODAS las variables (igual que updateZplCommand de
   * Swing)
   */
  private String updateZplCommand(String zpl, String sufijo, DatosEtiqueta datos, DatosEtiquetaRow datosRow) {
    zpl = zpl.replace("familia" + sufijo, datosRow.family != null ? datosRow.family : "");
    zpl = zpl.replace("descripcion" + sufijo, datosRow.description != null ? datosRow.description : "");
    zpl = zpl.replace("codigosap" + sufijo, datosRow.sapCode != null ? datosRow.sapCode : "");
    zpl = zpl.replace("usuario" + sufijo, datosRow.user != null ? datosRow.user : "");
    zpl = zpl.replace("tipologia" + sufijo, datos.typology != null ? datos.typology : "");
    zpl = zpl.replace("modelo" + sufijo, datosRow.model != null ? datosRow.model : "");
    zpl = zpl.replace("codProveedor" + sufijo, datosRow.supplierCod != null ? datosRow.supplierCod : "");
    zpl = zpl.replace("proveedor" + sufijo, datosRow.supplier != null ? datosRow.supplier : "");
    zpl = zpl.replace("lote" + sufijo, datos.batch != null ? datos.batch : "");
    zpl = zpl.replace("passModel" + sufijo, datosRow.passModel != null ? datosRow.passModel : "");
    zpl = zpl.replace("codeinModel" + sufijo, datosRow.codeInModel != null ? datosRow.codeInModel : "");
    zpl = zpl.replace("modelCodigo" + sufijo, datos.modelCode != null ? datos.modelCode : "");
    zpl = zpl.replace("modelDescripcion" + sufijo, datos.modelDescription != null ? datos.modelDescription : "");
    zpl = zpl.replace("modelDetalle" + sufijo, datos.modelDetail != null ? datos.modelDetail : "");
    zpl = zpl.replace("unitSerial3" + sufijo, datos.unitSerial3 != null ? datos.unitSerial3 : "");
    zpl = zpl.replace("unitSerial4" + sufijo, datos.unitSerial4 != null ? datos.unitSerial4 : "");
    return zpl;
  }

  /**
   * 📊 Obtiene valores de label (como getLabelValues de Swing)
   */
  private DatosEtiqueta getLabelValues(IngresoModel ingresoModel) {
    DatosEtiqueta datos = new DatosEtiqueta();

    if (this.searchLabelVariables == 1) {
      try {
        // Obtener modelo del maestro
        // String modeloMaestro = maestroService.getModelMaster(ingresoModel.getCodigoSap());
        // Integer modelId = modeloMaestro != null ? getIdModels(modeloMaestro) : null;

        datos.typology = ingresoModel.getTipologia();
        datos.batch = ingresoModel.getLote() != null ? ingresoModel.getLote() : "";
        datos.unitSerial3 = ingresoModel.getSerial3() != null ? ingresoModel.getSerial3() : "";
        datos.unitSerial4 = ingresoModel.getSerial4() != null ? ingresoModel.getSerial4() : "";

        // if (modelId != null) {
        //   datos.modelCode = maestroService.getMasterCodeById(modelId);
        //   datos.modelDescription = maestroService.getMasterDescriptionById(modelId);
        //   datos.modelDetail = maestroService.getMasterDetailById(modelId);
        // }
      } catch (Exception e) {
        System.err.println("Error obteniendo label values: " + e.getMessage());
      }
    }

    return datos;
  }

  /**
   * 📊 Obtiene valores de fila (como getLabelRowValues de Swing)
   */
  private DatosEtiquetaRow getLabelRowValues(IngresoModel ingresoModel) {
    DatosEtiquetaRow datos = new DatosEtiquetaRow();

    if (this.searchLabelVariables == 1) {
      try {
        String codigoSap = ingresoModel.getCodigoSap();
        String serial = ingresoModel.getSerial();
        String mac = ingresoModel.getMac();

        // datos.family = maestroService.getFamilyMaster(codigoSap);
        // datos.description = cortarString(ingresoModel.getDescripcion(), 35);
        // datos.sapCode = codigoSap;
        // datos.model = maestroService.getModelMaster(codigoSap);
        // datos.supplierCod = maestroService.getProviderMaster(codigoSap);
        // datos.supplier = maestroService.getProviderDescriptionMaster(codigoSap);
        datos.passModel = serial != null && serial.length() > 12 ? serial.substring(0, 12) : serial;
        datos.codeInModel = mac != null && mac.length() >= 4 ? mac.substring(mac.length() - 4) : mac;
        // El usuario vendría del request
        datos.user = "";
      } catch (Exception e) {
        System.err.println("Error obteniendo row values: " + e.getMessage());
      }
    }

    return datos;
  }

  /**
   * 🔄 Switch case para etiquetas >= 9 (igual que en Swing)
   */
  private String switchCaseEtiquetaCampo(int index, IngresoModel ingreso, String zpl,
      EtiquetaCampoModel ecm, List<EtiquetaCampoModel> todosCampos) {
    String valor = obtenerValorCampo(ingreso, ecm);
    String sufijo = obtenerSufijoSwitch(index);

    zpl = zpl.replace(ecm.getNombre() + sufijo, valor);
    return zpl;
  }

  /**
   * 🔄 Switch case para variables de label >= 9
   */
  private String switchCaseLabelVariables(int index, IngresoModel ingreso, String zpl,
      DatosEtiqueta datos, String fecha) {
    String sufijo = obtenerSufijoSwitch(index);

    if (this.searchLabelVariables == 1) {
      DatosEtiquetaRow datosRow = getLabelRowValues(ingreso);
      zpl = updateZplCommand(zpl, sufijo, datos, datosRow);
    }

    return zpl;
  }

  /**
   * 🔑 Obtiene sufijo para índices >= 9 (0, A, B, C...)
   */
  private String obtenerSufijoSwitch(int index) {
    if (index == 9)
      return "0";
    if (index >= 10)
      return String.valueOf((char) ('A' + (index - 10)));
    return String.valueOf(index + 1);
  }

  /**
   * 🔑 Obtiene sufijo para índices 0-8 (1-9)
   */
  private String obtenerSufijo(int index) {
    if (index < 9)
      return String.valueOf(index + 1);
    if (index == 9)
      return "0";
    return String.valueOf((char) ('A' + (index - 10)));
  }

  /**
   * 🔍 Obtiene valor de campo usando reflexión o valores directos
   */
  private String obtenerValorCampo(IngresoModel model, EtiquetaCampoModel campo) {
    try {
      String nombreCampo = campo.getNombre().toLowerCase();
      Field field = model.getClass().getDeclaredField(nombreCampo);
      field.setAccessible(true);
      Object valor = field.get(model);
      return valor != null ? valor.toString() : "";
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Si no existe el campo por reflexión, intentar mapeo manual
      return obtenerValorPorColumna(model, campo.getValor());
    }
  }

  /**
   * 📊 Mapeo manual de columnas (como getValueAt de Swing)
   */
  private String obtenerValorPorColumna(IngresoModel model, String columnaIndex) {
    switch (columnaIndex) {
      case "0":
        return model.getSerial() != null ? model.getSerial() : "";
      case "1":
        return model.getMac() != null ? model.getMac() : "";
      // case "2":
      //   return model.getSerial2() != null ? model.getSerial2() : "";
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
   * 📁 Lee plantilla desde disco
   */
  private String leerPlantilla(String base, String nombre, int cantidad) {
    String path = base + File.separator + nombre + File.separator + "codigo" + cantidad + ".prn";
    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      StringBuilder sb = new StringBuilder();
      String linea;
      while ((linea = br.readLine()) != null)
        sb.append(linea).append("\n");
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException("No se encontró la plantilla: " + path);
    }
  }

  /**
   * ✂️ Corta string a longitud máxima
   */
  private String cortarString(String str, int len) {
    if (str == null)
      return "";
    return str.length() > len ? str.substring(0, len) : str;
  }

  /**
   * 🔍 Obtiene ID de modelo (simulación)
   */
  private Integer getIdModels(String modeloNombre) {
    // Aquí deberías implementar la lógica para obtener el ID del modelo
    // Por ahora retorno null, ajusta según tu base de datos
    return null;
  }

  // ============================================
  // CLASES INTERNAS PARA DATOS
  // ============================================

  private static class DatosEtiqueta {
    String typology;
    String modelCode;
    String modelDescription;
    String modelDetail;
    String batch;
    String unitSerial3;
    String unitSerial4;
  }

  private static class DatosEtiquetaRow {
    String family;
    String description;
    String sapCode;
    String user;
    String model;
    String supplierCod;
    String supplier;
    String passModel;
    String codeInModel;
  }
}