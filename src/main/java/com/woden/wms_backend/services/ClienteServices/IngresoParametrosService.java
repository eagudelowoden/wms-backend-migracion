package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reglas de Ingreso por cliente que estaban dentro de pa_InsertEntry de algunas bases, ahora en el backend para que el
 * SP sea igual en todas:
 *   LIMPIEZA_CARACTERES_INGRESO  quita en Serial, MAC y Serial3 los textos del maestro "Limpieza Caracteres" (Código =
 *                                texto a quitar; Detalle = campos SERIAL, MAC, SERIAL3 separados por coma, vacío =
 *                                los tres). Era el REPLACE de la MAC de Claro Ecuador.
 *   Campos calculados            MAC y Serial3-5 por código SAP (CodigoSapReglaService).
 *   INSERT_AUTOMATICO_SMARTCARD  la smartcard (Serial3) no puede estar ya en Ingreso como serial, MAC o Serial3 de otro
 *                                equipo: se avisa con el serial relacionado, en la misma conexión del ingreso.
 *   APP_PQRS_INGRESO             después del ingreso, el ticket PQRS pendiente del serial pasa a En proceso (etapa 3)
 *                                con su registro en App_PQRS_StatusLog. Era el bloque "VÍNCULO AUTOMÁTICO APP PQRS".
 * Con los interruptores apagados y sin reglas el ingreso no cambia en nada.
 */
@Service
public class IngresoParametrosService {

  private static final Logger logger = LoggerFactory.getLogger(IngresoParametrosService.class);
  public static final String LIMPIEZA = "LIMPIEZA_CARACTERES_INGRESO";
  public static final String PQRS = "APP_PQRS_INGRESO";
  public static final String MAESTRO_LIMPIEZA = "Limpieza Caracteres";

  private static final String SMARTCARD_EXISTE = "SELECT TOP 1 x.Serial, x.Campo FROM ("
      + "SELECT TOP 1 Serial, 1 AS Campo FROM Ingreso WHERE Serial = ? "
      + "UNION ALL SELECT TOP 1 Serial, 2 FROM Ingreso WHERE Mac = ? "
      + "UNION ALL SELECT TOP 1 Serial, 3 FROM Ingreso WHERE Serial3 = ? AND Serial <> ?) x "
      + "ORDER BY CASE x.Campo WHEN 3 THEN 0 WHEN 2 THEN 1 ELSE 2 END";

  private static final String VINCULAR_PQRS = "IF OBJECT_ID(N'dbo.App_PQRS_Tickets', N'U') IS NOT NULL "
      + "AND OBJECT_ID(N'dbo.App_PQRS_StatusLog', N'U') IS NOT NULL "
      + "BEGIN "
      + "DECLARE @serial VARCHAR(50) = ?, @usuarioId INT = ?; "
      + "DECLARE @ingresoId INT = (SELECT TOP 1 Id FROM Ingreso WHERE Serial = @serial ORDER BY Id DESC); "
      + "DECLARE @ticketId INT = NULL, @etapaAnterior INT = NULL; "
      + "SELECT TOP 1 @ticketId = id, @etapaAnterior = id_etapa FROM App_PQRS_Tickets "
      + "WHERE UPPER(mac) = UPPER(@serial) AND id_etapa IN (1, 2) AND (id_ingreso IS NULL OR id_ingreso = 0) "
      + "ORDER BY id DESC; "
      + "IF @ticketId IS NOT NULL AND @ingresoId IS NOT NULL "
      + "BEGIN "
      + "UPDATE App_PQRS_Tickets SET id_ingreso = @ingresoId, id_etapa = 3, updated_at = DATEADD(HOUR, -5, GETDATE()) "
      + "WHERE id = @ticketId; "
      + "INSERT INTO App_PQRS_StatusLog (id_ticket, id_etapa_anterior, id_etapa_nueva, id_usuario, comentario, "
      + "auto_change) VALUES (@ticketId, @etapaAnterior, 3, @usuarioId, 'Ingreso WMS detectado - Serial: ' "
      + "+ UPPER(@serial), 1); "
      + "END "
      + "END";

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private MaestroParametroService maestros;

  @Autowired
  private CodigoSapReglaService reglas;

  @Autowired
  private DataSource dataSource;

  /** Limpieza de caracteres y campos calculados: cambia los valores de c. */
  public void preparar(Integer codigoSapId, CodigoSapReglaService.Campos c) {
    if (interruptores.activo(LIMPIEZA)) {
      for (MaestroParametroService.Fila f : maestros.activos(MAESTRO_LIMPIEZA)) {
        String texto = f.codigo();
        if (texto == null || texto.isEmpty()) {
          continue;
        }
        String campos = f.detalle() == null ? "" : f.detalle().toUpperCase(Locale.ROOT);
        boolean todos = campos.isBlank();
        if (todos || campos.matches(".*\\bSERIAL\\b.*")) {
          c.serial = quitar(c.serial, texto);
        }
        if (todos || campos.matches(".*\\bMAC\\b.*")) {
          c.mac = quitar(c.mac, texto);
        }
        if (todos || campos.matches(".*\\bSERIAL3\\b.*")) {
          c.serial3 = quitar(c.serial3, texto);
        }
      }
    }
    reglas.aplicarIngreso(codigoSapId, c);
  }

  /**
   * Con INSERT_AUTOMATICO_SMARTCARD activo: null si la smartcard se puede ingresar; si no, el aviso con el serial del
   * equipo que ya la tiene (primero el equipo que la tiene en Serial3, después en MAC y por último la smartcard sola).
   */
  public String smartcardRepetida(String serial, String serial3, boolean smartcardActiva) {
    String tarjeta = serial3 == null ? "" : serial3.trim().toUpperCase(Locale.ROOT);
    if (!smartcardActiva || tarjeta.length() <= 1 || tarjeta.equalsIgnoreCase(serial == null ? "" : serial.trim())) {
      return null;
    }
    try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(SMARTCARD_EXISTE)) {
      ps.setString(1, tarjeta);
      ps.setString(2, tarjeta);
      ps.setString(3, tarjeta);
      ps.setString(4, serial == null ? "" : serial.trim().toUpperCase(Locale.ROOT));
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        String relacionado = rs.getString(1);
        return switch (rs.getInt(2)) {
          case 1 -> "La smartcard " + tarjeta + " ya está en Ingreso como serial.";
          case 2 -> "La smartcard " + tarjeta + " ya está en Ingreso como MAC del equipo " + relacionado + ".";
          default -> "La smartcard " + tarjeta + " ya está en Ingreso como Serial3 del equipo " + relacionado + ".";
        };
      }
    } catch (SQLException e) {
      logger.warn("[Ingreso] no se pudo validar la smartcard {}: {}", tarjeta, e.getMessage());
      return null;
    }
  }

  /** Después de un ingreso correcto. Nunca lanza error: el equipo ya quedó ingresado. */
  public void vincularPqrs(List<String> seriales, Integer usuarioId) {
    if (seriales == null || seriales.isEmpty() || !interruptores.activo(PQRS)) {
      return;
    }
    try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(VINCULAR_PQRS)) {
      for (String serial : seriales) {
        if (serial == null || serial.isBlank()) {
          continue;
        }
        try {
          ps.setString(1, serial.trim().toUpperCase(Locale.ROOT));
          ps.setInt(2, usuarioId == null ? 0 : usuarioId);
          boolean hay = ps.execute();
          while (true) {
            if (hay) {
              ps.getResultSet().close();
            } else if (ps.getUpdateCount() == -1) {
              break;
            }
            hay = ps.getMoreResults();
          }
        } catch (SQLException e) {
          logger.warn("[PQRS] {} no se pudo vincular: {}", serial, e.getMessage());
        }
      }
    } catch (Exception e) {
      logger.warn("[PQRS] vínculo de tickets no disponible: {}", e.getMessage());
    }
  }

  public void vincularPqrs(String serial, Integer usuarioId) {
    List<String> uno = new ArrayList<>();
    uno.add(serial);
    vincularPqrs(uno, usuarioId);
  }

  private static String quitar(String valor, String texto) {
    if (valor == null || valor.isEmpty()) {
      return valor;
    }
    return Pattern.compile(Pattern.quote(texto), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(valor)
        .replaceAll(Matcher.quoteReplacement(""));
  }
}
