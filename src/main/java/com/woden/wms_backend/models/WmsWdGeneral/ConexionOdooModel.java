package com.woden.wms_backend.models.WmsWdGeneral;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ConexionOdoo", schema = "dbo")
public class ConexionOdooModel {
  @Id
  @Column(name = "Id")
  private Integer id;
  private Integer idClienteWms;
  private String clienteWms;
  private Integer idOdoo;
  private String clienteOdoo;
  private String servidor;
  private String db;
  private String userOdoo;
  private String passOdoo;
  private String modelo;
  private Integer teamId;
  private Integer companyId;
  private Integer minutosRefresco;
}
