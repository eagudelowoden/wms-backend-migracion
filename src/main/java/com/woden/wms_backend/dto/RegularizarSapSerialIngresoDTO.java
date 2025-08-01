package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class RegularizarSapSerialIngresoDTO {
  private Integer codigoSapId;
  private String serial;
  private Integer usuarioIdMovimiento;
}
