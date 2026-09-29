package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class RegularizarLoteSerialDTO {
  private String serial;
  private int loteId;
  private int usuarioIdMovimiento;
}
