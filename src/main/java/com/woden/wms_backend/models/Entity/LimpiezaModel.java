package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Limpieza", schema = "dbo")
public class LimpiezaModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String serial3;
    private String mac;
    private int codigoSapId;
    private Boolean asignacionConfirmada;
    private int usuarioId;
    private int usuarioIdAsignado;
    private String fecha;
}
