package com.woden.wms_backend.dto.response;

import java.time.Instant;

/**
 * Respuesta estándar para operaciones de escritura exitosas.
 *
 * @param success   Siempre {@code true} — indica que la operación fue exitosa
 * @param message   Mensaje descriptivo de la operación realizada
 * @param timestamp Momento en que se completó la operación
 */
public record ApiSuccess(
    boolean success,
    String message,
    Instant timestamp) {

  /** Factory method principal — success siempre es true */
  public static ApiSuccess of(String message) {
    return new ApiSuccess(true, message, Instant.now());
  }
}
