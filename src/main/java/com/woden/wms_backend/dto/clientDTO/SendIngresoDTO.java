package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class SendIngresoDTO {
  private Integer palletId;
  private Integer estadoId;
  private Integer tipologiaId;
  private Integer opcion;
  private Integer usuarioId;
}
