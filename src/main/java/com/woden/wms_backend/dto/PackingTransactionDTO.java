package com.woden.wms_backend.dto;

import com.woden.wms_backend.models.Entity.EmpaqueModel;

public class PackingTransactionDTO {
    // Datos del Empaque nuevo (para el Insert)
    private EmpaqueModel empaque;

    // Datos para la actualización del SmartCard (lo que tenías suelto)
    private Integer estadoId;
    private Integer palletId;
    private Integer cajaEmpaqueId;
    private Integer usuarioId;
    private String serial; // El serial principal
    // El loteId y smartCardId pueden venir dentro de 'empaque',
    // pero si son distintos, déjalos aquí.
    private String smartCardCode;

    public EmpaqueModel getEmpaque() {
        return empaque;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public Integer getPalletId() {
        return palletId;
    }

    public Integer getCajaEmpaqueId() {
        return cajaEmpaqueId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getSerial() {
        return serial;
    }

    public String getSmartCardCode() {
        return smartCardCode;
    }

    public void setEmpaque(EmpaqueModel empaque) {
        this.empaque = empaque;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public void setPalletId(Integer palletId) {
        this.palletId = palletId;
    }

    public void setCajaEmpaqueId(Integer cajaEmpaqueId) {
        this.cajaEmpaqueId = cajaEmpaqueId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setSmartCardCode(String smartCardCode) {
        this.smartCardCode = smartCardCode;
    }
    // Getters y Setters...
}