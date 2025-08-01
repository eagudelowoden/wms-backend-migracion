package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Reparacion", schema = "dbo")
public class ReparacionModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String mac;
    private int codigoSapId;
    private String codigoSap;
    private String descripcion;
    private int estadoFinalId;
    private String estadoFinal;
    private int fallaDxId;
    private int falla1Id;
    private String falla1;
    private int falla2Id;
    private int falla3Id;
    private int falla4Id;
    private String partesCambiadas;
    private int motivoScrapId;
    private int tecnicoAsignacionId;
    private int tecnicoReparacionId;
    private String tecnicoReparacion;
    private String fechaAsignacion;
    private String fechaReparacion;
    private int estadoCalidadId;
    private int usuarioId;
}
