package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

@Service
public class IngresoParametrosService {

  private static final Logger logger = LoggerFactory.getLogger(IngresoParametrosService.class);
  public static final String LIMPIEZA = "LIMPIEZA_CARACTERES_INGRESO";
  public static final String PQRS = "APP_PQRS_INGRESO";
  public static final String SERIAL_DISTINTO_MAC = "SERIAL_DISTINTO_MAC";
  public static final String MODELOID_CODIGOSAP = "MODELOID_DESDE_CODIGOSAP";
  public static final String SCRAP_MULTIMODELO = "SCRAP_MULTIMODELO_ENVIO";
  public static final String MAESTRO_LIMPIEZA = "Limpieza Caracteres";

  // Vincula el ingreso al ticket del app PQRS (si existen las tablas). Parametros, en orden: serial, serial (ticket),
  // usuarioId, serial (comentario).
  private static final String VINCULAR_PQRS = "WITH ing AS (SELECT Id FROM Ingreso WHERE Serial = CAST(? AS character varying) "
      + "ORDER BY Id DESC LIMIT 1), "
      + "tk AS (SELECT t.id, t.id_etapa FROM App_PQRS_Tickets t WHERE UPPER(t.mac) = UPPER(CAST(? AS character varying)) "
      + "AND t.id_etapa IN (1, 2) AND (t.id_ingreso IS NULL OR t.id_ingreso = 0) ORDER BY t.id DESC LIMIT 1), "
      + "upd AS (UPDATE App_PQRS_Tickets t SET id_ingreso = (SELECT Id FROM ing), id_etapa = 3, "
      + "updated_at = LOCALTIMESTAMP - interval '5 hours' FROM tk WHERE t.id = tk.id AND EXISTS (SELECT 1 FROM ing) "
      + "RETURNING t.id) "
      + "INSERT INTO App_PQRS_StatusLog (id_ticket, id_etapa_anterior, id_etapa_nueva, id_usuario, comentario, auto_change) "
      + "SELECT tk.id, tk.id_etapa, 3, CAST(? AS integer), 'Ingreso WMS detectado - Serial: ' "
      + "|| UPPER(CAST(? AS character varying)), true FROM tk JOIN upd ON upd.id = tk.id";

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
    Integer modelo = modelosPorCodigo().get(codigoSapId);
    return modelo != null ? modelo : modeloId;
  }

  private record Modelos(Map<Integer, Integer> porCodigo, long vence) {}

  private static final Map<Integer, Modelos> MODELOS = new ConcurrentHashMap<>();

  // leo los modelos de todos los códigos SAP una vez por minuto para no sumarle una ida a la base a cada ingreso
  private Map<Integer, Integer> modelosPorCodigo() {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    Modelos l = cliente == null ? null : MODELOS.get(cliente);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.porCodigo();
    }
    Map<Integer, Integer> out = new HashMap<>();
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT Id, modeloId FROM CodigoSap WHERE modeloId > 0");
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.put(rs.getInt(1), rs.getInt(2));
      }
    } catch (SQLException e) {
      logger.warn("[Ingreso] ModeloId de los códigos SAP: {}", e.getMessage());
    }
    if (cliente != null) {
      MODELOS.put(cliente, new Modelos(out, System.currentTimeMillis() + 60_000));
    }
    return out;
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
      try (PreparedStatement chk = conn.prepareStatement("SELECT to_regclass('App_PQRS_Tickets') IS NOT NULL "
          + "AND to_regclass('App_PQRS_StatusLog') IS NOT NULL");
          ResultSet rs = chk.executeQuery()) {
        if (!rs.next() || !rs.getBoolean(1)) {
          return;
        }
      }
      for (String serial : seriales) {
        if (serial == null || serial.isBlank()) {
          continue;
        }
        try {
          String serialLimpio = serial.trim().toUpperCase(Locale.ROOT);
          ps.setString(1, serialLimpio);
          ps.setString(2, serialLimpio);
          ps.setInt(3, usuarioId == null ? 0 : usuarioId);
          ps.setString(4, serialLimpio);
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
