package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class AutogestionSolicitudDTO {
    /** SERIAL | PALLET */
    private String ambito;
    /** SERIAL: ESTADO | CAJA | PALLET. PALLET: MOVER | CAJA_VACIAR | CAJA_ELIMINAR | CAJA_PALLET */
    private String accion;
    /** Serial o número de pallet sobre el que se actúa. */
    private String objetivo;
    private Integer estadoDestinoId;
    private String cajaNumero;
    private String palletDestino;
    private Integer usuarioId;
    private String justificacion;
}
