package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Cosmetica", schema = "dbo")
public class CosmeticaModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private Integer serialId;
  private String serial;
  private String mac;
  private String serial3;
  private String serial4;
  private String serial5;
  private Integer codigoSapId;
  private Integer palletId;
  private Integer nivelId;
  private Integer loteId;
  private Integer usuarioId;
  private String fecha;
  private String fechaCierre;
}
