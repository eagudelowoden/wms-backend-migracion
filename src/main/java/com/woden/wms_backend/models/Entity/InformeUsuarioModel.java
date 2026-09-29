package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "InformeUsuario", schema = "dbo")
public class InformeUsuarioModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int informeId;
    private int usuarioId;
}