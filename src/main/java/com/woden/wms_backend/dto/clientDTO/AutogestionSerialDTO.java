package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class AutogestionSerialDTO {
    private String serial;
    /** ESTADO | CAJA | PALLET */
    private String accion;
    private Integer estadoDestinoId;
    private String palletDestino;
    private Integer usuarioId;
    private String justificacion;
}
