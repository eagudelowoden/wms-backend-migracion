package com.woden.wms_backend.dto.response;

import java.time.Instant;

/**
 * Respuesta estándar para errores HTTP.
 *
 * @param code    Código legible por máquina (ej: DUPLICATE_ENTRY, NOT_FOUND)
 * @param message Mensaje descriptivo para el usuario
 * @param detail  Detalle técnico del error (excepción, causa)
 * @param path    Ruta del endpoint que generó el error
 * @param timestamp Momento en que ocurrió el error
 */
public record ApiError(
    String code,
    String message,
    String detail,
    String path,
    Instant timestamp) {

  public static ApiError of(String code, String message, String detail, String path) {
    return new ApiError(code, message, detail, path, Instant.now());
  }
}
