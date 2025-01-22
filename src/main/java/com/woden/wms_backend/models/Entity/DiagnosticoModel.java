package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Diagnostico", schema = "dbo")
public class DiagnosticoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int serialId;
    private String serial;
    private String mac;
    private int codigoSapId;
    private int estadoFinalId;
    private int fallaId;
    private int usuarioId;
    private String fecha;
    private int estadoCalidadId;
}
