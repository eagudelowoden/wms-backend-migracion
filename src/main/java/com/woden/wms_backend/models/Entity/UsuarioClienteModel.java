package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuarioCliente", schema = "dbo")
public class UsuarioClienteModel {
    @Id
    @Column(name = "Id")
    private int id;
    private int usuarioId;
    private int clienteId;
}
