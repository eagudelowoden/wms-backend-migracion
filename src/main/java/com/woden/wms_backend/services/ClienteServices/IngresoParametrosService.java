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

@Service
public class IngresoParametrosService {

  private static final Logger logger = LoggerFactory.getLogger(IngresoParametrosService.class);
  public static final String LIMPIEZA = "LIMPIEZA_CARACTERES_INGRESO";
  public static final String PQRS = "APP_PQRS_INGRESO";
  public static final String SERIAL_DISTINTO_MAC = "SERIAL_DISTINTO_MAC";
  public static final String MODELOID_CODIGOSAP = "MODELOID_DESDE_CODIGOSAP";
  public static final String SCRAP_MULTIMODELO = "SCRAP_MULTIMODELO_ENVIO";
  public static final String MAESTRO_LIMPIEZA = "Limpieza Caracteres";

  // una sola ida a la base: smartcard repetida, pa_InsertEntry y smartcard nueva. Columnas: código y mensaje del
  // SP, dónde está repetida (0 no, 1 serial, 2 MAC, 3 Serial3), el serial relacionado y el error de la smartcard
  public static final String INGRESO_CON_SMARTCARD = "SET NOCOUNT ON; " + SmartcardAutomaticaService.DECLARAR
      + "DECLARE @campo INT = 0, @relacionado VARCHAR(50) = NULL, @codigo INT = 0, @mensaje NVARCHAR(4000) = N'', "
      + "@errorTarjeta NVARCHAR(4000) = NULL; "
      + "SELECT TOP 1 @relacionado = x.Serial, @campo = x.Campo FROM ("
      + "SELECT TOP 1 Serial, 1 AS Campo FROM Ingreso WHERE Serial = @scTarjeta "
      + "UNION ALL SELECT TOP 1 Serial, 2 FROM Ingreso WHERE Mac = @scTarjeta "
      + "UNION ALL SELECT TOP 1 Serial, 3 FROM Ingreso WHERE Serial3 = @scTarjeta AND Serial <> @scSerial) x "
      + "ORDER BY CASE x.Campo WHEN 3 THEN 0 WHEN 2 THEN 1 ELSE 2 END; "
      + "IF @campo = 0 BEGIN "
      + "EXEC pa_InsertEntry " + String.join(", ", java.util.Collections.nCopies(28, "?"))
      + ", @codigo OUTPUT, @mensaje OUTPUT; "
      + "IF @codigo = 0 BEGIN BEGIN TRY " + SmartcardAutomaticaService.CREAR
      + "END TRY BEGIN CATCH SET @errorTarjeta = ERROR_MESSAGE(); END CATCH END "
      + "END "
      + "SELECT @codigo AS Codigo, @mensaje AS Mensaje, @campo AS Campo, @relacionado AS Relacionado, "
      + "@errorTarjeta AS ErrorTarjeta;";

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

  public static String avisoSmartcard(int campo, String tarjeta, String relacionado) {
    return switch (campo) {
      case 1 -> "La smartcard " + tarjeta + " ya está en Ingreso como serial.";
      case 2 -> "La smartcard " + tarjeta + " ya está en Ingreso como MAC del equipo " + relacionado + ".";
      default -> "La smartcard " + tarjeta + " ya está en Ingreso como Serial3 del equipo " + relacionado + ".";
    };
  }

  public String serialIgualMac(String serial, String mac, Integer codigoSapId) {
    if (serial == null || mac == null || serial.isBlank() || !serial.trim().equalsIgnoreCase(mac.trim())
        || !interruptores.activo(SERIAL_DISTINTO_MAC)) {
      return null;
    }
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT CASE WHEN t.Codigo = 'SMARTCARD' OR c.Codigo = 'SMARTSAP' "
            + "THEN 1 ELSE 0 END FROM CodigoSap cs LEFT JOIN Maestro t ON t.Id = cs.TipoId "
            + "LEFT JOIN Maestro c ON c.Id = cs.ClasificacionId WHERE cs.Id = ?")) {
      ps.setInt(1, codigoSapId == null ? 0 : codigoSapId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next() && rs.getInt(1) == 1) {
          return null;
        }
      }
    } catch (SQLException e) {
      logger.warn("[Ingreso] tipo del código SAP {}: {}", codigoSapId, e.getMessage());
    }
    return "El serial no puede ser igual a la MAC.";
  }

  public Integer modeloPorDefecto(Integer modeloId, Integer codigoSapId) {
    if ((modeloId != null && modeloId != 0) || codigoSapId == null || !interruptores.activo(MODELOID_CODIGOSAP)) {
      return modeloId;
    }
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT ISNULL(modeloId, 0) FROM CodigoSap WHERE Id = ?")) {
      ps.setInt(1, codigoSapId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next() && rs.getInt(1) > 0) {
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      logger.warn("[Ingreso] ModeloId del código SAP {}: {}", codigoSapId, e.getMessage());
    }
    return modeloId;
  }

  public Integer tipologiaEnvio(Integer palletId, Integer tipologiaId) {
    if (palletId == null || !interruptores.activo(SCRAP_MULTIMODELO)) {
      return tipologiaId;
    }
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT p.TipologiaId FROM Pallet p "
            + "INNER JOIN CodigoSap cs ON cs.Id = p.CodigoSapId INNER JOIN Maestro m ON m.Id = p.TipologiaId "
            + "WHERE p.Id = ? AND cs.Codigo = 'MULTIMODELO' AND m.Codigo = 'SCRAP'")) {
      ps.setInt(1, palletId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      logger.warn("[Envio] tipología del pallet {}: {}", palletId, e.getMessage());
    }
    return tipologiaId;
  }

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
