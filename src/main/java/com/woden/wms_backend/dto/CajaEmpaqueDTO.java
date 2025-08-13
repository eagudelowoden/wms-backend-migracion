package com.woden.wms_backend.dto;

import lombok.Data;

@Data
public class CajaEmpaqueDTO {
    private int id;
    private String numero;
    private String pallet;
    private int cantidad;
}
