package com.woden.wms_backend.dto;


import lombok.Data;

@Data
public class ModeloPalletDto {
    private String codigoSap;
    private Integer cantidad;
    private String posicion;
}