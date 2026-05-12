package com.woden.wms_backend.dto.clientDTO.ingreso;

public record UpdateNoveltyItemRequest(
    Integer id,
    String serial,
    String mac,
    Integer codigoSapId,
    String guia,
    String documento,
    Integer tipoOrigenId,
    Integer origenId,
    Integer tipologiaId,
    Integer estadoId,
    String tipoNovedad,
    Integer usuarioId) {
}
