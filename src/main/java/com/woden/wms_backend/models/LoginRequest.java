package com.woden.wms_backend.models;

import lombok.Data;

@Data
public class LoginRequest {
    private String nombreUsuario;
    private String clave;
}
