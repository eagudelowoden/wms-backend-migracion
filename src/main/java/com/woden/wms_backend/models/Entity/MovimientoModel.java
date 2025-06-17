package com.woden.wms_backend.models.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Movimiento", schema = "dbo")
public class MovimientoModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private Integer Id;  
  @Column(name = "SerialId")
  private Integer SerialId;
  @Column(name = "Serial")
  private String Serial;
  @Column(name = "OrigenId")
  private Integer OrigenId;
  @Column(name = "DestinoId")
  private Integer DestinoId;
  @Column(name = "Descripcion")
  private String Descripcion;
  @Column(name = "UsuarioId")
  private Integer UsuarioId;
  @Column(name = "Fecha")
  private String Fecha;
}
