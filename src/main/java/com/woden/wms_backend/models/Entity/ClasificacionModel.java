package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "clasificacion")
public class ClasificacionModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private String serialId;
  private String serial;
  private String mac;
  private String serial3;
  private String serial4;
  private String serial5;
  private Integer codigoSapId;
  private Integer nivelId;
  private Integer loteId;
  private Integer estadoEnviado;
  private Integer usuarioId;
  private String fecha;
  private Integer usuarioAsignadoId;
  private String fechaCierre;
}
