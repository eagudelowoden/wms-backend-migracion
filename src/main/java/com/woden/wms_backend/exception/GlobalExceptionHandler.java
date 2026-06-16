package com.woden.wms_backend.exception;

import com.woden.wms_backend.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador global de excepciones para todos los controladores REST.
 * Captura excepciones del dominio y las convierte en respuestas HTTP
 * estructuradas, eliminando la necesidad de try-catch en los controladores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ── Excepciones de dominio ───────────────────────────────────────────────────

  @ExceptionHandler(DuplicateEntryException.class)
  public ResponseEntity<ApiError> handleDuplicateEntry(
      DuplicateEntryException ex, HttpServletRequest request) {
    logger.warn("[DUPLICATE_ENTRY] path={} message={}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiError.of("DUPLICATE_ENTRY", ex.getMessage(), null, request.getRequestURI()));
  }

  @ExceptionHandler(EntryNotFoundException.class)
  public ResponseEntity<ApiError> handleEntryNotFound(
      EntryNotFoundException ex, HttpServletRequest request) {
    logger.warn("[NOT_FOUND] path={} message={}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiError.of("NOT_FOUND", ex.getMessage(), null, request.getRequestURI()));
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ApiError> handleBusinessRule(
      BusinessRuleException ex, HttpServletRequest request) {
    logger.warn("[BUSINESS_RULE] path={} message={}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiError.of("BUSINESS_RULE", ex.getMessage(), null, request.getRequestURI()));
  }

  // ── Errores de solicitud HTTP ────────────────────────────────────────────────

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiError> handleMissingParam(
      MissingServletRequestParameterException ex, HttpServletRequest request) {
    String detail = "Parámetro requerido ausente: '" + ex.getParameterName() + "' (tipo: " + ex.getParameterType() + ")";
    logger.warn("[BAD_REQUEST] path={} detail={}", request.getRequestURI(), detail);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiError.of("MISSING_PARAMETER", "Parámetro requerido no enviado.", detail, request.getRequestURI()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String detail = "El parámetro '" + ex.getName() + "' recibió el valor '" + ex.getValue()
        + "' que no puede convertirse a " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo desconocido");
    logger.warn("[TYPE_MISMATCH] path={} detail={}", request.getRequestURI(), detail);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiError.of("TYPE_MISMATCH", "Tipo de dato inválido en la solicitud.", detail, request.getRequestURI()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    String detail = ex.getMostSpecificCause().getMessage();
    logger.warn("[BAD_REQUEST] path={} detail={}", request.getRequestURI(), detail);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiError.of("INVALID_BODY", "El cuerpo de la solicitud no es válido o está mal formado.", detail, request.getRequestURI()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> handleNoResourceFound(
      NoResourceFoundException ex, HttpServletRequest request) {
    logger.warn("[NOT_FOUND] path={} message={}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiError.of(
            "ENDPOINT_NOT_FOUND",
            "El endpoint solicitado no existe: " + request.getRequestURI(),
            ex.getMessage(),
            request.getRequestURI()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    logger.warn("[BAD_REQUEST] path={} message={}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiError.of("INVALID_ARGUMENT", ex.getMessage(), null, request.getRequestURI()));
  }

  // ── Fallback general ─────────────────────────────────────────────────────────

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(
      Exception ex, HttpServletRequest request) {
    String detail = ex.getCause() != null
        ? ex.getMessage() + " | Causa: " + ex.getCause().getMessage()
        : ex.getMessage();
    logger.error("[INTERNAL_ERROR] path={} error={}", request.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiError.of(
            "INTERNAL_ERROR",
            "Ocurrió un error interno. Por favor, intenta de nuevo.",
            detail,
            request.getRequestURI()));
  }
}
