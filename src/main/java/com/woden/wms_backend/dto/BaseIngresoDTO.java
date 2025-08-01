package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class BaseIngresoDTO {
  private String serial;
  private String codigoSap;
  private String estadoSap;
  private String estadoRR;
  private String Lote;
}
