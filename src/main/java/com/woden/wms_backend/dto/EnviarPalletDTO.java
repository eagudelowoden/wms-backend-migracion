package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class EnviarPalletDTO {
  private Integer palletId;
  private Integer destinoId;
  private Integer estadoId;
  private Integer tipologiaId;
  private Integer posicionId;
  private Integer estado;
  private Integer opcion;
  private Integer usuarioId;
}
