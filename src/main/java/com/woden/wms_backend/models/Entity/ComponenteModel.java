package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "SerialComponente", schema = "dbo")
public class ComponenteModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private String serialId;
  private String componente;
  private String nivel;
}
