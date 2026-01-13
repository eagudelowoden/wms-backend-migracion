package com.woden.wms_backend.dto.clientDTO;

import java.util.List;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.models.Entity.IngresoModel;

import lombok.Data;

@Data
public class EtiquetadoRequestDTO {
  private String rutaPlantillas;
  private EtiquetaModel etiqueta;
  private List<EtiquetaCampoModel> camposConfigurados;
  private List<IngresoImpresionDTO> listaSeriales;
  private EtiquetaDatosGeneralesDTO datosGenerales;
  private Boolean lecturaVariables;

  // ✅ NUEVO: Datos adicionales que vienen del frontend (ya consultados)
  private DatosAdicionalesDTO datosAdicionales;

  @Data
  public static class EtiquetaDatosGeneralesDTO {
    private String fecha;
    private String usuario;
  }

  /**
   * Contiene TODOS los datos consultados del maestro para cada serial
   * El frontend solo consulta y envía, el backend reemplaza
   */
  @Data
  public static class DatosAdicionalesDTO {
    // Datos del maestro (uno por cada serial, en el mismo orden que listaSeriales)
    private List<DatosMaestroDTO> datosMaestro;
  }

  @Data
  public static class DatosMaestroDTO {
    // Identificación
    private String serial;
    private String codigoSap;

    // Datos consultados del maestro
    private String familia;
    private String modelo;
    private String codProveedor;
    private String proveedor;

    // Datos calculados
    private String passModel; // Serial truncado a 12 chars
    private String codeInModel; // Últimos 4 chars del MAC
  }

  @Data
  public static class IngresoImpresionDTO {

    private String serial;
    private String mac;
    private String codigoSap;
    private String descripcion;
    private String lote;
    private String serial3;
    private String serial4;
    private String tipologia;

    // 👇 variables SOLO de impresión
    private String variable1;
    private String variable2;
    private String variable3;
    private String variable4;
  }

}