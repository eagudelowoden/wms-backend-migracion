package com.woden.wms_backend.dto.clientDTO;

public record ReemplazoSerialRequest(
    String serialAnterior,
    String serialNuevo,
    Integer destinoId,
    Integer usuarioId,
    String modulo,
    String smartCardModo,
    String smartCardNuevo,
    Boolean simular) {
}
