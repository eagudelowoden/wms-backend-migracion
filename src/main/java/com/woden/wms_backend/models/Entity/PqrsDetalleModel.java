package com.woden.wms_backend.models.Entity;

import lombok.Data;

/**
 * Detalle de PQRS para la vista de solo lectura (Diagnóstico/Reparación).
 * Reemplaza la tabla PQRS (obsoleta) — se arma buscando primero en
 * App_PQRS_Tickets y, si no hay match, en App_PQRS_Truckrolls.
 *
 * No es una entidad JPA: se llena a mano desde PqrsDetalleRepository
 * porque el cruce con las tablas maestras (App_Master_Contratista,
 * App_Master_RazonEscalamiento, App_Master_TipoEquipo) vive en WmsWdGeneral
 * y requiere JDBC directo con nombre calificado, igual que TruckrollRepository.
 */
@Data
public class PqrsDetalleModel {

  private String serial;
  private String contratista;
  private String fechaCreacion;
  private String infDano;
  private String tecnologia;
  private String obsPqrs;
  private String imagenUrl;

  /** "TICKETS" o "TRUCKROLL" — de dónde salió el detalle. */
  private String fuente;
}
