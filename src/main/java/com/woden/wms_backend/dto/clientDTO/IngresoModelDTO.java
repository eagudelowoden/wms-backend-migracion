package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class IngresoModelDTO {
  private Integer id;
  private String serial;
  private String mac;
  private String serial3;
  private String serial4;
  private String codigoSap;
  private String descripcion;
  private String nombreUsuario;
  private Integer tipoOrigenId;
  private String tipologia;
  private String fecha;
  private String lote;
}