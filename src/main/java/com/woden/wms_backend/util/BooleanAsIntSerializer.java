package com.woden.wms_backend.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Las columnas son boolean en PostgreSQL, pero el frontend sigue comparando estos flags con
 * 1/0 (por ejemplo loteEmpaqueON === 1). Este serializador mantiene el JSON numérico.
 * La lectura no necesita nada especial: Jackson ya acepta 0/1 y true/false para Boolean.
 */
public class BooleanAsIntSerializer extends JsonSerializer<Boolean> {

  @Override
  public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeNumber(value ? 1 : 0);
    }
  }
}
