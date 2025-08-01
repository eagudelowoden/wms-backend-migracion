package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class WorkFlowDestinoDTO {
  private String origen;
    private String opcion;
    private String descripcion;
    private int tipologiaId;
}
