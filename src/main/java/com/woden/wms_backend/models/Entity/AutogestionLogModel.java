package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "AutogestionLog", schema = "dbo")
public class AutogestionLogModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private String fecha;
  private Integer usuarioId;
  private String accion;
  private Integer serialId;
  private String serial;
  private Integer estadoOrigenId;
  private Integer estadoDestinoId;
  private Integer palletOrigenId;
  private Integer palletDestinoId;
  private Integer cajaOrigenId;
  private Integer cajaDestinoId;
  private String justificacion;
}
