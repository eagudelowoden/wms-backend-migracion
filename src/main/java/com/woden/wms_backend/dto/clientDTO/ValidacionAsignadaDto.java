package com.woden.wms_backend.dto.clientDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidacionAsignadaDto {

    private Integer id;
    private Integer validacionTipoId;
    private String codigo;
    private String descripcion;
    private Boolean activo;

    public ValidacionAsignadaDto() {}

    public ValidacionAsignadaDto(Integer id, Integer validacionTipoId, String codigo, String descripcion, Boolean activo) {
        this.id = id;
        this.validacionTipoId = validacionTipoId;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    @JsonProperty("id")
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @JsonProperty("validacionTipoId")
    public Integer getValidacionTipoId() { return validacionTipoId; }
    public void setValidacionTipoId(Integer validacionTipoId) { this.validacionTipoId = validacionTipoId; }

    @JsonProperty("codigo")
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @JsonProperty("descripcion")
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @JsonProperty("activo")
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
