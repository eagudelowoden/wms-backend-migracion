package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record UpdatePalletTypologyRequest(
    Integer palletId,
    Integer tipologiaId,
    Integer usuarioIdMovimiento,
    Integer estadoId,
    List<String> seriales) {
}
