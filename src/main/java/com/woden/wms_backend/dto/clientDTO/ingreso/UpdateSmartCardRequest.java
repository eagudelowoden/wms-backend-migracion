package com.woden.wms_backend.dto.clientDTO.ingreso;

public record UpdateSmartCardRequest(
    Integer smartCardId,
    String smartCardNuevo,
    String serial) {
}
