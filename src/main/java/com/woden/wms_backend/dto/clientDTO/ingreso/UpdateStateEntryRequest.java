package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record UpdateStateEntryRequest(
    Integer estadoId,
    Integer palletId,
    Integer usuarioId,
    Integer fecha,
    List<String> seriales) {
}
