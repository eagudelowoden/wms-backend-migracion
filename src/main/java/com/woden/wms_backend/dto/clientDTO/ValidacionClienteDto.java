package com.woden.wms_backend.dto.clientDTO;

public class ValidacionClienteDto {

    private String codigo;
    private boolean activo;

    public ValidacionClienteDto() {}

    public ValidacionClienteDto(String codigo, boolean activo) {
        this.codigo = codigo;
        this.activo = activo;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
