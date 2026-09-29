package com.woden.wms_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoteDTO {
  private Integer id;

  @JsonProperty("nombreLote")
  private String nombre;

  private String descripcion;
  private Integer activo;
}
