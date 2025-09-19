package com.woden.wms_backend.dto.clientDTO;

public class UpdatePackingRequestDTO {
    private Integer estadoId;
    private Integer palletId;
    private Integer cajaEmpaqueId;
    private Integer usuarioId;
    private String serial;
    private Integer loteId;
    private Integer SmardCardId;
    private String SmartCard;

    // ✅ Getters y setters
    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Integer getPalletId() { return palletId; }
    public void setPalletId(Integer palletId) { this.palletId = palletId; }

    public Integer getCajaEmpaqueId() { return cajaEmpaqueId; }
    public void setCajaEmpaqueId(Integer cajaEmpaqueId) { this.cajaEmpaqueId = cajaEmpaqueId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public Integer getLoteId() { return loteId; }
    public void setLoteId(Integer loteId) { this.loteId = loteId; }

    public Integer getSmardCardId() { return SmardCardId; }
    public void setSmardCardId(Integer smardCardId) { SmardCardId = smardCardId; }

    public String getSmartCard() { return SmartCard; }
    public void setSmartCard(String smartCard) { SmartCard = smartCard; }
}