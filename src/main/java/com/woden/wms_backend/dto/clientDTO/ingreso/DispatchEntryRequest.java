package com.woden.wms_backend.dto.clientDTO.ingreso;

public record DispatchEntryRequest(
    String serial,
    Integer estadoId,
    Integer palletId,
    Integer cajaDespachoId,
    Integer usuarioMovimientoId,
    Integer loteId) {
}
