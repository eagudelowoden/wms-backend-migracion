package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Tarea", schema = "dbo")
public class TareaModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String titulo;
    private String descripcion;
    private int notificacion;
    private int tiempo;
    private String fecha;
    private int usuarioId;
}
