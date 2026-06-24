package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "menu_perfil", schema = "dbo")
public class MenuPerfilModel {

    @Id
    @Column(name = "Id")
    private int id;

    @Column(name = "IdMenu")
    private String idMenu;

    @Column(name = "IdPerfil")
    private int idPerfil;
}
