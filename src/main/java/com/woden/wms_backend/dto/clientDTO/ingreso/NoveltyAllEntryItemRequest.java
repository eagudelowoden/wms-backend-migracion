package com.woden.wms_backend.dto.clientDTO.ingreso;

public record NoveltyAllEntryItemRequest(
    String serial,
    String observaciones,
    Integer fallaCosmeticaId,
    Integer fallaFuncionalId) {
}
