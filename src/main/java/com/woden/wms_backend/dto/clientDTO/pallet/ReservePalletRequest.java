package com.woden.wms_backend.dto.clientDTO.pallet;

public record ReservePalletRequest(
    Integer origenId,
    Integer destinoId,
    Integer usuarioId,
    String zonaHoraria) {}
