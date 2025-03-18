package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class CerrarIngresoDTO {
    private Integer palletId;
    private Integer estadoId;
    private Integer tipologiaId;
    private Integer usuarioId;
    private Integer opcion; // normalmente será 0
}