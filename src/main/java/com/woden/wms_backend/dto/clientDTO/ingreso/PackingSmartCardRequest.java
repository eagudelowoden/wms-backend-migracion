package com.woden.wms_backend.dto.clientDTO.ingreso;

public record PackingSmartCardRequest(
    Integer estadoId,
    Integer palletId,
    Integer cajaEmpaqueId,
    Integer usuarioId,
    String serial,
    Integer loteId,
    Integer smartCardId,
    String smartCard) {
}
