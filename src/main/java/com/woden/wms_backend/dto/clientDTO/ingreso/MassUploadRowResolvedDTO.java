package com.woden.wms_backend.dto.clientDTO.ingreso;

import lombok.Data;

@Data
public class MassUploadRowResolvedDTO {
    private String serial;
    private String mac;
    private String serial3;
    private String serial4;
    private String serial5;
    private String smartCard;
    private Integer codigoSapId;
    private Integer palletId;
    private Integer estadoId;
    private Integer tipoOrigenId;
    private Integer origenId;
    private Integer tipologiaId;
    private Integer nivelId;
    private String tramite;
    private String documento;
    private String guia;
    private String causa;
    private Integer usuarioId;
    private String fecha;
    private String estadoCliente;
    private Integer loteId;
    private Integer modeloId;
}
