package com.woden.wms_backend.dto.clientDTO.clasificacion;

public class ClasificacionRequest {

    private String  serial;
    private Integer estadoId;
    private Integer nivelId;
    private Integer usuarioId;
    private String  estado;

    public String  getSerial()                   { return serial; }
    public void    setSerial(String serial)       { this.serial = serial; }

    public Integer getEstadoId()                  { return estadoId; }
    public void    setEstadoId(Integer estadoId)  { this.estadoId = estadoId; }

    public Integer getNivelId()                   { return nivelId; }
    public void    setNivelId(Integer nivelId)    { this.nivelId = nivelId; }

    public Integer getUsuarioId()                 { return usuarioId; }
    public void    setUsuarioId(Integer usuarioId){ this.usuarioId = usuarioId; }

    public String  getEstado()                    { return estado; }
    public void    setEstado(String estado)       { this.estado = estado; }
}
