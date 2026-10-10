package com.woden.wms_backend.config.DataSource;

import java.util.Locale;

/**
 * En PostgreSQL todos los clientes viven en una misma base (WmsWoden), cada uno en su esquema.
 * El nombre lógico del cliente (columna Cliente.dbase, p. ej. "WmsWdClaro") se traduce al esquema
 * en minúsculas ("wmswdclaro"); la base general ("WmsWdGeneral") es el esquema "general".
 */
public final class ClientSchema {

  public static final String GENERAL_DB = "WmsWdGeneral";
  public static final String GENERAL_SCHEMA = "general";

  private ClientSchema() {
  }

  public static String schemaFor(String dbName) {
    if (dbName == null || GENERAL_DB.equalsIgnoreCase(dbName)) {
      return GENERAL_SCHEMA;
    }
    return dbName.toLowerCase(Locale.ROOT);
  }

  /** Agrega (o reemplaza) currentSchema en la URL JDBC base. */
  public static String jdbcUrlFor(String baseUrl, String dbName) {
    String url = baseUrl.replaceAll("[?&]currentSchema=[^&]*", "");
    url = url.replaceFirst("\\?&", "?");
    url = url + (url.contains("?") ? "&" : "?") + "currentSchema=" + schemaFor(dbName);
    // stringtype=unspecified: los String se envian sin tipo y PostgreSQL los convierte al de la columna
    // (varchar -> timestamp/boolean/int). SQL Server lo hacia solo; sin esto fallan los INSERT/UPDATE
    // de entidades con fechas o flags declarados como String.
    if (!url.contains("stringtype=")) {
      url = url + "&stringtype=unspecified";
    }
    return url;
  }
}
