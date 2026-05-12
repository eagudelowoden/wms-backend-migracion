package com.woden.wms_backend.dto.clientDTO.ingreso;

public record PackingEntryRequest(
    Integer estadoId,
    Integer palletId,
    Integer cajaEmpaqueId,
    Integer usuarioIdMovimiento,
    String serial,
    Integer loteId) {
}
