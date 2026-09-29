package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ServicioWeb", schema = "dbo")
public class ServicioWebModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private String descripcion;
    private String endPoint;
    private int clienteId;
    private int usuarioId;
    private String fecha;
    private int activo;
}
