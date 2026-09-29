package com.woden.wms_backend.dto.clientDTO.ingreso;

import lombok.Data;

@Data
public class PalletConfigDTO {
    private String numeroPallet;
    private String lote;
    private String modelo;
    private String tipoOrigen;
    private String origen;
    private String tipologia;
    private String codigoSap;
    private boolean multimodelo;
}
