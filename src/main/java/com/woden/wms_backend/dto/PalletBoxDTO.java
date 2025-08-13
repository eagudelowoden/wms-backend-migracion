package com.woden.wms_backend.dto;

public class PalletBoxDTO {
    private Long id;
    private String numero;
    private String cajas;
    private String tipologia;

    public PalletBoxDTO(Long id, String numero, String cajas, String tipologia) {
        this.id = id;
        this.numero = numero;
        this.cajas = cajas;
        this.tipologia = tipologia;
    }

    // Getters y setters
}
