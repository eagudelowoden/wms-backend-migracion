package com.woden.wms_backend.dto;

public class SmartCardDTO { // El nombre de la clase debe ser idéntico al del archivo

    private Integer serialId;
    private String serial;
    private Integer codigoSapId;
    private Integer usuarioId;

    // EL CONSTRUCTOR: Debe llamarse EXACTAMENTE igual que la clase
    public SmartCardDTO() {
    }

    // Getters y Setters
    public Integer getSerialId() {
        return serialId;
    }

    public void setSerialId(Integer serialId) {
        this.serialId = serialId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Integer getCodigoSapId() {
        return codigoSapId;
    }

    public void setCodigoSapId(Integer codigoSapId) {
        this.codigoSapId = codigoSapId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}