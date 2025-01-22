package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuarioClientePerfil", schema = "dbo")
public class UsuarioClientePerfil {
    @Id
    @Column(name = "Id")
    private int id;
    private int usuarioClienteId;
    private int perfilId;
}
