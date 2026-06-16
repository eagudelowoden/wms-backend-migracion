package com.woden.wms_backend.dto.clientDTO.ingreso;

import java.util.List;

public record PackingAllRequest(
    Integer estadoId,
    Integer usuarioIdMovimiento,
    List<String> seriales) {
}
