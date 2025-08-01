package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class IngresoTransitoDTO {
  private String serial;
  private String mac;
  private String codigoSap;
  private String descripcion;
  private String tipologia;
  private String lote;
  private String guia;
  private String documento;
  private String fecha;
}
