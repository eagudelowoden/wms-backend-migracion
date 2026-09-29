package com.woden.wms_backend.dto;

public class PackingTransactionDTO {
    // 📦 Campos para el INSERT (Antes estaban dentro de empaque)
    private Integer serialId;
    private String serial;
    private String mac;
    private Integer codigoSapId;
    private Integer palletId;
    private Integer cajaEmpaqueId;
    private Integer nivelId;
    private Integer usuarioId;
    private Integer loteId;
    private Integer smartCardId;
    private String smartCard;

    // ⚡ Campos para los UPDATES (Los datos de control)
    private Integer estadoId;       // estadoId de la SmartCard (ej: 99 - EMPAQUE APROBADO)
    private Integer estadoSerialId; // estadoId del Serial       (ej: 66 - EMPACADO)
    private String smartCardCode;

    // --- GETTERS Y SETTERS ---
    public Integer getSerialId() { return serialId; }
    public void setSerialId(Integer serialId) { this.serialId = serialId; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }

    public Integer getCodigoSapId() { return codigoSapId; }
    public void setCodigoSapId(Integer codigoSapId) { this.codigoSapId = codigoSapId; }

    public Integer getPalletId() { return palletId; }
    public void setPalletId(Integer palletId) { this.palletId = palletId; }

    public Integer getCajaEmpaqueId() { return cajaEmpaqueId; }
    public void setCajaEmpaqueId(Integer cajaEmpaqueId) { this.cajaEmpaqueId = cajaEmpaqueId; }

    public Integer getNivelId() { return nivelId; }
    public void setNivelId(Integer nivelId) { this.nivelId = nivelId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getLoteId() { return loteId; }
    public void setLoteId(Integer loteId) { this.loteId = loteId; }

    public Integer getSmartCardId() { return smartCardId; }
    public void setSmartCardId(Integer smartCardId) { this.smartCardId = smartCardId; }

    public String getSmartCard() { return smartCard; }
    public void setSmartCard(String smartCard) { this.smartCard = smartCard; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Integer getEstadoSerialId() { return estadoSerialId; }
    public void setEstadoSerialId(Integer estadoSerialId) { this.estadoSerialId = estadoSerialId; }

    public String getSmartCardCode() { return smartCardCode; }
    public void setSmartCardCode(String smartCardCode) { this.smartCardCode = smartCardCode; }
}