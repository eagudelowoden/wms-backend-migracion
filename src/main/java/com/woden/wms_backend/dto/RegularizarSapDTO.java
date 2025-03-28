package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class RegularizarSapDTO {
  private String serial;
  private int codigoSapId;
  private int usuarioIdMovimiento;
}
