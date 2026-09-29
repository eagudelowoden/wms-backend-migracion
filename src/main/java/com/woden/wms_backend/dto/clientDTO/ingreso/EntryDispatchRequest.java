package com.woden.wms_backend.dto.clientDTO.ingreso;

public record EntryDispatchRequest(
    Integer estadoId,
    Integer palletId,
    Integer usuarioIdMovimiento,
    String serial,
    Integer loteId) {
}
