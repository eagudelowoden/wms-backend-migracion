package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class SendPalletDTO {
  private Integer palletId;
  private Integer destinoId;
  private Integer tipologiaId;
  private Integer posicionId;
  private Integer estado; // normalmente 1
}
