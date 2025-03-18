package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class CerrarCompletoDTO {
  private Integer palletId;
  private Integer estadoId;
  private Integer destinoId;
  private Integer tipologiaId;
  private Integer usuarioId;
  private Integer opcion;
  private Integer posicionId;
}
