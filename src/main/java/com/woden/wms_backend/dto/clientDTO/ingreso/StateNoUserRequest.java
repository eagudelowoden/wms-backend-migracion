package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

/**
 * DTO compartido por los endpoints:
 * - updateStateEntryNotUsuario
 * - updateStateEntryNotUsuarioRepaired
 * - updateStateEntryNotUsuarioDiagnosed
 */
public record StateNoUserRequest(
    Integer estadoId,
    Integer palletId,
    Integer fecha,
    List<String> serial) {
}
