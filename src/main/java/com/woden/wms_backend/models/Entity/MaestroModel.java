package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Maestro", schema = "dbo")
public class MaestroModel {

    @Id
    @Column(name = "Id")
    private int id;
    private int tipoMaestroId;
    private String codigo;
    private String descripcion;
    private int activo;
}
