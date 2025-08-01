package com.woden.wms_backend.models.WmsWdGeneral;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuarioClientePerfil", schema = "dbo")
public class UsuarioClientePerfilModel {
  @Id
  @Column(name = "Id")
  private int id;
  private int usuarioClienteId;
  private int perfilId;
}
