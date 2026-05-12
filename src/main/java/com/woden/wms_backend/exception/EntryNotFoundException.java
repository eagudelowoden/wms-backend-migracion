package com.woden.wms_backend.exception;

public class EntryNotFoundException extends RuntimeException {

  public EntryNotFoundException(String message) {
    super(message);
  }

  public static EntryNotFoundException forSerial(String serial) {
    return new EntryNotFoundException("Ingreso no encontrado para serial: " + serial);
  }
}
