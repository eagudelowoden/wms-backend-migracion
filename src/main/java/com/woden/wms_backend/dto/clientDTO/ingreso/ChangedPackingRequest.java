package com.woden.wms_backend.dto.clientDTO.ingreso;

public record ChangedPackingRequest(
    Integer estadoId,
    Integer palletId,
    Integer cajaEmpaqueId,
    String serial,
    Integer usuarioIdMovimiento,
    Integer tipologiaId) {
}
