package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "WorkFlow", schema = "dbo")
public class WorkFlowModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int moduloId;
    private int origenId;
    private int opcionId;
    private int tipologiaId;
    private int nivelId;
}
