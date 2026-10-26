package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Interruptor INSERT_AUTOMATICO_SMARTCARD (lo que se hizo en DirecTV Ecuador modificando pa_InsertEntry y
 * pa_DeleteEntry, ahora en el backend y para cualquier cliente):
 *   - al ingresar un equipo con Serial3 (la smartcard), la smartcard queda también en Ingreso con el código SAP y el
 *     destino de WmsWdGeneral.dbo.SmartcardAutomatica (por defecto CARD0001 / SMARTCARD), PalletId 0 y el resto de
 *     datos del equipo. Si ya existe en Ingreso (por serial o MAC) no se toca. Va sin prealerta (PrealertaId 0,
 *     Cruce 0) para no contar como recibida ni como sobrante.
 *   - al borrar equipos, sus smartcards se borran en la misma transacción solo si ningún otro equipo las tiene en
 *     Serial3 y siguen como se crearon (mismo código SAP, mismo destino, sin pallet): una smartcard ya procesada
 *     (asignada, empacada...) no se borra.
 * Apagado no hace nada: el ingreso y el borrado siguen igual que siempre. Un error aquí nunca tumba el ingreso.
 */
@Service
public class SmartcardAutomaticaService {

  private static final Logger logger = LoggerFactory.getLogger(SmartcardAutomaticaService.class);
  public static final String INTERRUPTOR = "INSERT_AUTOMATICO_SMARTCARD";
  private static final String CODIGO_POR_DEFECTO = "CARD0001";
  private static final String DESTINO_POR_DEFECTO = "SMARTCARD";
  private static final long CACHE_MS = 60_000;

  /** codigoSapId y estadoId resueltos en la base del cliente */
  public record Config(int codigoSapId, int estadoId, String codigoSap, String destino) {}

  private record Cache(Config config, long vence) {}

  private static final Map<Integer, Cache> CACHE = new ConcurrentHashMap<>();

  /**
   * La smartcard con los datos del equipo recién ingresado (copiados de su fila). Va detrás de un IF: el trigger
   * Trg_InsertMovement se dispara aunque el INSERT no inserte filas y falla con SerialId NULL, así que el INSERT solo
   * se ejecuta si el equipo existe y la smartcard no (ni como serial ni como MAC).
   * Parámetros: 1 serial, 2-3 smartcard, 4-5 smartcard, 6 código SAP, 7 estado, 8 serial.
   */
  private static final String INSERTAR = "IF EXISTS (SELECT 1 FROM Ingreso WHERE Serial = ?) "
      + "AND NOT EXISTS (SELECT 1 FROM Ingreso WHERE Serial = ? OR Mac = ?) "
      + "INSERT INTO Ingreso (Serial, Mac, Serial3, Serial4, Serial5, CodigoSapId, "
      + "PalletId, PalletIdIngreso, EstadoId, TipoOrigenId, OrigenId, TipologiaId, NivelId, Tramite, Documento, Guia, "
      + "Caja, Falla, TecnicoCliente, PrealertaId, Cruce, Novedad, GarantiaFabricante, UsuarioId, Fecha, "
      + "UsuarioIdMovimiento, FechaMovimiento, Observaciones, EstadoCliente, loteId, CajaIngresoId, modeloId) "
      + "SELECT ?, ?, NULL, NULL, NULL, ?, 0, i.PalletIdIngreso, ?, i.TipoOrigenId, i.OrigenId, i.TipologiaId, "
      + "i.NivelId, i.Tramite, i.Documento, i.Guia, 0, i.Falla, i.TecnicoCliente, 0, 0, i.Novedad, "
      + "i.GarantiaFabricante, i.UsuarioId, GETDATE(), i.UsuarioId, GETDATE(), i.Observaciones, i.EstadoCliente, "
      + "i.loteId, 0, NULL FROM Ingreso i WHERE i.Serial = ?";

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ObjectMapper objectMapper;

  @PersistenceContext
  private EntityManager em;

  /** Configuración del cliente de la sesión; null si el interruptor está apagado o no se resuelve en su base. */
  public Config config() {
    if (!interruptores.activo(INTERRUPTOR)) {
      return null;
    }
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    Cache c = CACHE.get(cliente);
    if (c != null && c.vence() > System.currentTimeMillis()) {
      return c.config();
    }
    Config cfg = null;
    try (Connection conn = dataSource.getConnection()) {
      cfg = leer(conn, cliente);
    } catch (Exception e) {
      logger.warn("[Smartcard] configuración del cliente {} no se pudo leer: {}", cliente, e.getMessage());
    }
    CACHE.put(cliente, new Cache(cfg, System.currentTimeMillis() + CACHE_MS));
    return cfg;
  }

  private Config leer(Connection conn, int cliente) throws SQLException {
    String codigo = CODIGO_POR_DEFECTO;
    String destino = DESTINO_POR_DEFECTO;
    if (entero(conn, "SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.SmartcardAutomatica', 'U') IS NULL THEN 0 ELSE 1 END") == 1) {
      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT CodigoSap, Destino FROM WmsWdGeneral.dbo.SmartcardAutomatica WHERE ClienteId = ?")) {
        ps.setInt(1, cliente);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            codigo = texto(rs.getString(1), CODIGO_POR_DEFECTO);
            destino = texto(rs.getString(2), DESTINO_POR_DEFECTO);
          }
        }
      }
    }
    int codigoSapId = entero(conn, "SELECT TOP 1 Id FROM CodigoSap WHERE Codigo = ?", codigo);
    int estadoId = entero(conn, "SELECT TOP 1 Id FROM Estado WHERE Nombre = ?", destino);
    if (codigoSapId <= 0 || estadoId <= 0) {
      logger.warn("[Smartcard] cliente {}: no existe el código SAP {} o el destino {} en su base; no se crean smartcards",
          cliente, codigo, destino);
      return null;
    }
    return new Config(codigoSapId, estadoId, codigo, destino);
  }

  /** Después de un ingreso correcto. Nunca lanza error: el equipo ya quedó ingresado. */
  public void crearSiAplica(String serial, String serial3) {
    List<String[]> uno = new ArrayList<>();
    uno.add(new String[] { serial, serial3 });
    crearSiAplica(uno);
  }

  /** equipos = [serial, serial3]; devuelve cuántas smartcards se crearon. */
  public int crearSiAplica(List<String[]> equipos) {
    try {
      Config cfg = config();
      if (cfg == null || equipos == null || equipos.isEmpty()) {
        return 0;
      }
      int creadas = 0;
      try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERTAR)) {
        for (String[] e : equipos) {
          String serial = limpio(e[0]);
          String tarjeta = limpio(e.length > 1 ? e[1] : null);
          if (serial.isEmpty() || tarjeta.length() <= 1 || tarjeta.equals(serial)) {
            continue;
          }
          try {
            ps.setString(1, serial);
            ps.setString(2, tarjeta);
            ps.setString(3, tarjeta);
            ps.setString(4, tarjeta);
            ps.setString(5, tarjeta);
            ps.setInt(6, cfg.codigoSapId());
            ps.setInt(7, cfg.estadoId());
            ps.setString(8, serial);
            creadas += ejecutar(ps);
          } catch (SQLException ex) {
            logger.warn("[Smartcard] {} (equipo {}) no se creó: {}", tarjeta, serial, ex.getMessage());
          }
        }
      }
      return creadas;
    } catch (Exception e) {
      logger.warn("[Smartcard] no se pudieron crear las smartcards: {}", e.getMessage());
      return 0;
    }
  }

  /** Antes del borrado (misma transacción): las smartcards (Serial3) de los equipos que se van a borrar. */
  public List<String> tarjetasDe(List<String> seriales) {
    if (seriales == null || seriales.isEmpty() || config() == null) {
      return List.of();
    }
    List<?> rs = em.createNativeQuery("SELECT DISTINCT UPPER(LTRIM(RTRIM(i.Serial3))) FROM Ingreso i "
        + "INNER JOIN OPENJSON(?1) WITH (Serial VARCHAR(50) '$') s ON i.Serial = s.Serial "
        + "WHERE LEN(LTRIM(RTRIM(ISNULL(i.Serial3, '')))) > 1").setParameter(1, json(seriales)).getResultList();
    List<String> out = new ArrayList<>();
    for (Object o : rs) {
      if (o != null) {
        out.add(String.valueOf(o));
      }
    }
    return out;
  }

  /** Después del borrado (misma transacción): borra las smartcards que nadie más usa y siguen sin procesar. */
  public int borrarSinUso(List<String> tarjetas) {
    Config cfg = config();
    if (cfg == null || tarjetas == null || tarjetas.isEmpty()) {
      return 0;
    }
    String desde = "FROM Ingreso sc INNER JOIN OPENJSON(?1) WITH (Serial VARCHAR(50) '$') t ON sc.Serial = t.Serial "
        + "WHERE sc.CodigoSapId = ?2 AND sc.EstadoId = ?3 AND ISNULL(sc.PalletId, 0) = 0 "
        + "AND NOT EXISTS (SELECT 1 FROM Ingreso d WHERE d.Serial3 = sc.Serial)";
    String lista = json(tarjetas);
    // solo se ejecuta el DELETE si hay algo que borrar (los triggers de Ingreso no siempre aguantan sentencias vacías)
    Object cuantas = em.createNativeQuery("SELECT COUNT(*) " + desde).setParameter(1, lista)
        .setParameter(2, cfg.codigoSapId()).setParameter(3, cfg.estadoId()).getSingleResult();
    if (!(cuantas instanceof Number c) || c.intValue() == 0) {
      return 0;
    }
    int n = em.createNativeQuery("DELETE sc " + desde).setParameter(1, lista)
        .setParameter(2, cfg.codigoSapId()).setParameter(3, cfg.estadoId()).executeUpdate();
    if (n > 0) {
      logger.info("[Smartcard] {} smartcard(s) borradas con sus equipos", n);
    }
    return n;
  }

  private String json(List<String> valores) {
    try {
      return objectMapper.writeValueAsString(valores.stream().map(SmartcardAutomaticaService::limpio).toList());
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar la lista de seriales", e);
    }
  }

  /** Ejecuta el IF ... INSERT leyendo todos los resultados (los triggers de Ingreso devuelven conteos extra). */
  private static int ejecutar(PreparedStatement ps) throws SQLException {
    boolean hayResultado = ps.execute();
    int filas = 0;
    while (true) {
      if (hayResultado) {
        ps.getResultSet().close();
      } else {
        int n = ps.getUpdateCount();
        if (n == -1) {
          break;
        }
        filas = n;
      }
      hayResultado = ps.getMoreResults();
    }
    return filas;
  }

  private static int entero(Connection conn, String sql, Object... params) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    }
  }

  private static String limpio(String s) {
    return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
  }

  private static String texto(String s, String porDefecto) {
    return s == null || s.isBlank() ? porDefecto : s.trim();
  }
}
