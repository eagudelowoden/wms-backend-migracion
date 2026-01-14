package com.woden.wms_backend.dto.clientDTO;

import java.util.List;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.models.Entity.EtiquetaModel;

import lombok.Data;

@Data
public class EtiquetadoRequestDTO {
  private String rutaPlantillas;
  private EtiquetaModel etiqueta;
  private List<EtiquetaCampoModel> camposConfigurados;
  private List<IngresoImpresionDTO> listaSeriales;
  private EtiquetaDatosGeneralesDTO datosGenerales;
  private Boolean lecturaVariables;

  private DatosAdicionalesDTO datosAdicionales;

  @Data
  public static class EtiquetaDatosGeneralesDTO {
    private String fecha;
    private String usuario;
  }

  @Data
  public static class DatosAdicionalesDTO {
    private List<DatosMaestroDTO> datosMaestro;
  }

  @Data
  public static class DatosMaestroDTO {
    private String serial;

    private String familia;
    private String fecha;
    private String usuario;
    private String modelo;
    private String codProveedor;
    private String proveedor;
    private String passModel;
    private String codeInModel;
    private String modelDescripcion;
    private String modelCodigo;
    private String modelDetalle;

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

    private String variable1;
    private String variable2;
    private String variable3;
    private String variable4;
  }

}