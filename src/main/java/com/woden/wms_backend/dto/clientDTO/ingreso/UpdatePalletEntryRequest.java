package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record UpdatePalletEntryRequest(
    Integer palletId,
    Integer usuarioIdMovimiento,
    List<String> seriales) {
}
