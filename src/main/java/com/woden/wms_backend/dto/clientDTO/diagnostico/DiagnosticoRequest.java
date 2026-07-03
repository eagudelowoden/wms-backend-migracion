package com.woden.wms_backend.dto.clientDTO.diagnostico;

public class DiagnosticoRequest {

    private String  serial;
    private Integer estadoId;
    private Integer usuarioId;
    private Integer estadoFinalId;
    private Integer fallaId;
    private Integer estadoCalidadId;
    private Integer truckRollId;

    public String  getSerial()                    { return serial; }
    public void    setSerial(String serial)        { this.serial = serial; }

    public Integer getEstadoId()                   { return estadoId; }
    public void    setEstadoId(Integer estadoId)   { this.estadoId = estadoId; }

    public Integer getUsuarioId()                  { return usuarioId; }
    public void    setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getEstadoFinalId()                      { return estadoFinalId; }
    public void    setEstadoFinalId(Integer estadoFinalId) { this.estadoFinalId = estadoFinalId; }

    public Integer getFallaId()                  { return fallaId; }
    public void    setFallaId(Integer fallaId)   { this.fallaId = fallaId; }

    public Integer getEstadoCalidadId()                        { return estadoCalidadId; }
    public void    setEstadoCalidadId(Integer estadoCalidadId) { this.estadoCalidadId = estadoCalidadId; }

    public Integer getTruckRollId()                    { return truckRollId; }
    public void    setTruckRollId(Integer truckRollId) { this.truckRollId = truckRollId; }
}
