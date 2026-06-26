package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class IngresoDTO {
  private String serial;
  private String mac;
  private String serial3;
  private String serial4;
  private String serial5;
  private String codigoSap;
  private String descripcion;
  private String tipologia;
  private String tipoOrigen;
  private String fecha;
  private String estadoCliente;
  private String lote;
  private String modelo;
  private Integer reingreso;
  private String smartCard;
  private Integer truckRollId;
}
