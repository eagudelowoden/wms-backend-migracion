package com.woden.wms_backend.dto.clientDTO.ingreso;

public record ClassificationItemRequest(
    String serial,
    Integer estadoId,
    Integer nivelId,
    Integer usuarioIdMovimiento) {
}
