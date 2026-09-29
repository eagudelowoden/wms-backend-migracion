package com.woden.wms_backend.dto;

import java.util.List;
import com.woden.wms_backend.models.Entity.IngresoModel; // Ajusta según tu paquete

public class ProcesarSmartCardDTO {

    private SmartCardDTO smartCard;
    private Integer estadoFinalId;
    private Integer fallaId;
    private String serial;
    private List<IngresoModel> ingresos;

    public ProcesarSmartCardDTO() {
    }

    // Getters y Setters
    public SmartCardDTO getSmartCard() { return smartCard; }
    public void setSmartCard(SmartCardDTO smartCard) { this.smartCard = smartCard; }

    public Integer getEstadoFinalId() { return estadoFinalId; }
    public void setEstadoFinalId(Integer estadoFinalId) { this.estadoFinalId = estadoFinalId; }

    public Integer getFallaId() { return fallaId; }
    public void setFallaId(Integer fallaId) { this.fallaId = fallaId; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public List<IngresoModel> getIngresos() { return ingresos; }
    public void setIngresos(List<IngresoModel> ingresos) { this.ingresos = ingresos; }
}