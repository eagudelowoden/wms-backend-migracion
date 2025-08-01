package com.woden.wms_backend.dto.clientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PedidoDTO {
  private Integer id;
  private String producto;
  private String descripcion;
  private Integer cantidad;
  private Integer remitenteId;
}
