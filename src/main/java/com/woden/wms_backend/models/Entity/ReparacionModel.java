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
    private Integer id;
    private Integer serialId;
    private String serial;
    private String mac;
    private Integer codigoSapId;
    private String codigoSap;
    private String descripcion;
    private Integer estadoFinalId;
    private String estadoFinal;
    private Integer fallaDxId;
    private Integer falla1Id;
    private String falla1;
    private Integer falla2Id;
    private Integer falla3Id;
    private Integer falla4Id;
    private String partesCambiadas;
    private Integer motivoScrapId;
    private Integer tecnicoAsignacionId;
    private Integer tecnicoReparacionId;
    private String tecnicoReparacion;
    private String fechaAsignacion;
    private String fechaReparacion;
    private Integer estadoCalidadId;
    private Integer usuarioId;
}
