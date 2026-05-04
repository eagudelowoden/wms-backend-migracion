package com.woden.wms_backend.dto.clientDTO.pallet;

public record UpdatePalletDataRequest(
    Integer palletId,
    Integer codigoSapId,
    Integer tipologiaId,
    Integer posicionId,
    Integer loteId,
    Integer usuarioId) {}
