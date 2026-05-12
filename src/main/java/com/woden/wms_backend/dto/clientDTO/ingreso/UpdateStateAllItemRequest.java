package com.woden.wms_backend.dto.clientDTO.ingreso;

public record UpdateStateAllItemRequest(
    Integer estadoId,
    Integer usuarioIdMovimiento,
    String serial) {
}
