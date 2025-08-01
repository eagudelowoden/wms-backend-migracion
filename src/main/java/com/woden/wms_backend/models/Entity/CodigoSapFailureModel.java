package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CodigoSap", schema = "dbo")
public class CodigoSapFailureModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private String nombre;
}
