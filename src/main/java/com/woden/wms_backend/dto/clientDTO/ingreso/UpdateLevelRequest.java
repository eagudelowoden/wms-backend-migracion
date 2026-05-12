package com.woden.wms_backend.dto.clientDTO.ingreso;

/**
 * DTO compartido por updateLevel y updateLevelWeb.
 */
public record UpdateLevelRequest(
    Integer levelId,
    Integer palletId) {
}
