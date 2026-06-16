package com.woden.wms_backend.dto.clientDTO.ingreso;

public record InsertSerialInventoryRequest(
    String serial,
    String codigoSap,
    String palletNumero,
    Integer usuarioId) {
}
