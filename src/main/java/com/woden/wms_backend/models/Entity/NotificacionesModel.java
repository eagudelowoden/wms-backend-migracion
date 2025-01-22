package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Notificaciones", schema = "dbo")
public class NotificacionesModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int seccionId;
    private String seccion;
    private int moduloId;
    private String modulo;
    private String descripcion;
    private String fecha;
    private int activo;
}
