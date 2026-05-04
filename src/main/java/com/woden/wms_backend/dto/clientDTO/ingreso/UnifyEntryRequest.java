package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record UnifyEntryRequest(
    Integer palletIdDestino,
    Integer tipologiaId,
    Integer usuarioId,
    List<Integer> palletIds) {
}
