package com.woden.wms_backend.models.WmsWdGeneral;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Key de API para integraciones externas (sistema a sistema, sin JWT/usuario).
 * Genérica — no es específica de Hoja de Vida: cuando se agregue la integración
 * con Odoo, reutiliza esta misma tabla con una fila nueva (Nombre distinto).
 */
@Data
@Entity
@Table(name = "IntegracionApiKey", schema = "dbo")
public class IntegracionApiKeyModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private Integer id;

  @Column(name = "Nombre")
  private String nombre;

  // Se guarda el HASH (SHA-256, hex) de la key — nunca el valor en texto plano.
  @Column(name = "ApiKeyHash")
  private String apiKeyHash;

  @Column(name = "Activo")
  private Boolean activo;

  @Column(name = "FechaCreacion")
  private LocalDateTime fechaCreacion;
}
