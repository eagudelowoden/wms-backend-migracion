package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Menu", schema = "dbo")
public class MenuModel {
  @Id
  @Column(name = "Id")
  private String id;
  private String descripcion;
  private String tipo;
  private int orden;
  private String accion;
  private String estado;
  private String icono;
  private String id_padre;
  private String view_name;
  private String controller_name;

}
