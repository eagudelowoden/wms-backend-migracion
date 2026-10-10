package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Clinico")
public class CiclicoModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private int usuarioId;
    private String estado;
}
