package com.woden.wms_backend.dto.clientDTO;

import lombok.Data;

@Data
public class AutogestionPalletDTO {
    private String pallet;
    /** MOVER | CAJA_VACIAR | CAJA_ELIMINAR | CAJA_PALLET */
    private String accion;
    private Integer estadoDestinoId;
    private String cajaNumero;
    private String palletDestino;
    private Integer usuarioId;
    private String justificacion;
}
