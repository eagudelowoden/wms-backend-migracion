package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class BaseEmpaqueDTO {
  private String serial;
  private String codigoSap;
  private String estadoSap;
  private String estadoRR;
  private String Lote;
}
