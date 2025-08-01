package com.woden.wms_backend.models.WmsWdGeneral;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuarioTipoOrigen", schema = "dbo")
public class UsuarioTipoOrigenModel {
  @Id
  @Column(name = "Id")
  public Integer id;
  public Integer usuarioClienteId;
  public Integer tipoOrigenId;
}
