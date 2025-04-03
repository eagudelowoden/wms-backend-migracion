package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuarioSys", schema = "dbo")
public class UsuarioSysModel {
  @Id
  @Column(name = "Id")
  private Integer id;

  @Column(name = "Nombres") // Nombre exacto de columna en BD
  private String nombres;

  @Column(name = "Nombre_Usuario") // Nombre exacto de columna en BD
  private String nombreUsuario; // Cambiado a camelCase en Java

  @Column(name = "Perfil_Id")
  private Integer perfilId;

  // @Column(name = "Identificacion")
  // private Long Identificacion;
}
