package com.woden.wms_backend.dto.clientDTO.ingreso;

public record UpdateStateOneEntryRequest(
    Integer estadoId,
    Integer nivelId,
    Integer usuarioIdMovimiento,
    String fecha,
    String serial) {
}
