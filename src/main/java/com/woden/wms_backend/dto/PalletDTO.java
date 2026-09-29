package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class PalletDTO {
  private Integer id;
  private String numero;
  private String codigoSap;
  private String descripcion;
  private Integer cantidad;
  private String tipologia;
  private String lote;
  private Boolean kitIngreso;
  private String origen;
}
