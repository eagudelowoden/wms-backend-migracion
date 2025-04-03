package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class PalletDTO {
  private Integer id;
  private String numero;
  private String codigoSap;
  private Integer cantidad;
  private String tipologia;
  private String lote;
}
