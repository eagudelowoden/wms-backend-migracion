package com.woden.wms_backend.dto.clientDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClienteValidacionTipoDto {

    private Integer id;
    private String codigo;
    private String descripcion;

    public ClienteValidacionTipoDto() {}

    public ClienteValidacionTipoDto(Integer id, String codigo, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @JsonProperty("codigo")
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @JsonProperty("descripcion")
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
