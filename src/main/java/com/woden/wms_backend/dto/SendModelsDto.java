package com.woden.wms_backend.dto;


import lombok.Data;

@Data
public class SendModelsDto {
    private Integer palletOrigenId;
    private Integer codigoSapId;
    private Integer destinoId;
    private Integer palletDestinoId;
    private Integer usuarioId;
    private String posicionNumero;
}