package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class RegularizarLotePalletDTO {
  private int palletId;
  private int loteId;
  private int usuarioIdMovimiento;
}
