package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "Perfil", schema = "dbo")
public class PerfilModel {
    @Id
    @Column(name = "Id")
    private int id;
    private String nombre;
    private int clienteId;
    @Transient
    private int tipoPerfilId;
}
