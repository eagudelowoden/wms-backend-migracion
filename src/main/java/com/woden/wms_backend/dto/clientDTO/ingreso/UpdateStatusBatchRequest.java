package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record UpdateStatusBatchRequest(
    Integer estadoId,
    Integer palletId,
    Integer usuarioIdMovimiento,
    Integer fecha,
    List<String> seriales,
    Integer loteId) {
}
