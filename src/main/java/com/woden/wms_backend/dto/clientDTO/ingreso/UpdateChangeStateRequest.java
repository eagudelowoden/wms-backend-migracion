package com.woden.wms_backend.dto.clientDTO.ingreso;

public record UpdateChangeStateRequest(
    String serial1,
    String serial2,
    String mac,
    Integer estadoId) {
}
