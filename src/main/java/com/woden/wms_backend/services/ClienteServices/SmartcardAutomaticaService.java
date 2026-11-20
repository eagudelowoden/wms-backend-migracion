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

@Service
public class SmartcardAutomaticaService {

  private static final Logger logger = LoggerFactory.getLogger(SmartcardAutomaticaService.class);
  public static final String INTERRUPTOR = "INSERT_AUTOMATICO_SMARTCARD";
  private static final String CODIGO_POR_DEFECTO = "CARD0001";
  private static final String DESTINO_POR_DEFECTO = "SMARTCARD";
  public static final String MAESTRO = "SmartCard Ingreso";
  private static final long CACHE_MS = 60_000;

  public record Config(int codigoSapId, int estadoId, String codigoSap, String destino) {}

  private record Cache(Config config, long vence) {}

  private static final Map<Integer, Cache> CACHE = new ConcurrentHashMap<>();

  // Parametros (en este orden): serial del equipo, tarjeta, codigoSapId, estadoId, usuarioId.
  // La tarjeta se crea solo si el equipo existe y la tarjeta no esta usada como serial ni como MAC.
  public static final String INSERTAR = "WITH p(serial, tarjeta, codigosap, estado, usuario) AS ("
      + "SELECT CAST(? AS character varying), CAST(? AS character varying), CAST(? AS integer), CAST(? AS integer), "
      + "CAST(? AS integer)) "
      + "INSERT INTO Ingreso (Serial, Mac, Serial3, Serial4, Serial5, CodigoSapId, "
      + "PalletId, PalletIdIngreso, EstadoId, TipoOrigenId, OrigenId, TipologiaId, NivelId, Tramite, Documento, Guia, "
      + "Caja, Falla, TecnicoCliente, PrealertaId, Cruce, Novedad, GarantiaFabricante, UsuarioId, Fecha, "
      + "UsuarioIdMovimiento, FechaMovimiento, Observaciones, EstadoCliente, loteId, CajaIngresoId, modeloId) "
      + "SELECT p.tarjeta, p.tarjeta, NULL, NULL, NULL, p.codigosap, 0, i.PalletIdIngreso, p.estado, i.TipoOrigenId, "
      + "i.OrigenId, i.TipologiaId, i.NivelId, i.Tramite, i.Documento, i.Guia, 0, i.Falla, i.TecnicoCliente, 0, false, "
      + "i.Novedad, i.GarantiaFabricante, COALESCE(NULLIF(p.usuario, 0), i.UsuarioId), LOCALTIMESTAMP, "
      + "COALESCE(NULLIF(p.usuario, 0), i.UsuarioId), LOCALTIMESTAMP, i.Observaciones, i.EstadoCliente, i.loteId, 0, NULL "
      + "FROM Ingreso i, p WHERE i.Serial = p.serial "
      + "AND NOT EXISTS (SELECT 1 FROM Ingreso WHERE Serial = p.tarjeta) "
      + "AND NOT EXISTS (SELECT 1 FROM Ingreso WHERE Mac = p.tarjeta)";

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private MaestroParametroService maestros;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ObjectMapper objectMapper;

  @PersistenceContext
  private EntityManager em;

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
    List<MaestroParametroService.Fila> filas = maestros.activos(MAESTRO);
    if (!filas.isEmpty()) {
      codigo = texto(filas.get(0).codigo(), CODIGO_POR_DEFECTO);
      destino = texto(filas.get(0).detalle(), DESTINO_POR_DEFECTO);
    }
    int codigoSapId = entero(conn, "SELECT Id FROM CodigoSap WHERE Codigo = ? LIMIT 1", codigo);
    int estadoId = entero(conn, "SELECT Id FROM general.Estado WHERE LOWER(Nombre) = LOWER(?) LIMIT 1", destino);
    if (codigoSapId <= 0 || estadoId <= 0) {
      logger.warn("[Smartcard] cliente {}: no existe el código SAP {} o el destino {} en su base; no se crean smartcards",
          cliente, codigo, destino);
      return null;
    }
    return new Config(codigoSapId, estadoId, codigo, destino);
  }

  public int crearSiAplica(List<String[]> equipos, Integer usuarioId) {
    try {
      Config cfg = config();
      if (cfg == null || equipos == null || equipos.isEmpty()) {
        return 0;
      }
      int creadas = 0;
      try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERTAR)) {
        for (String[] e : equipos) {
          String serial = limpio(e[0]);
          String tarjeta = tarjeta(serial, e.length > 1 ? e[1] : null);
          if (serial.isEmpty() || tarjeta == null) {
            continue;
          }
          try {
            parametros(ps, 1, serial, tarjeta, cfg, usuarioId);
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

  public List<String> tarjetasDe(List<String> seriales) {
    if (seriales == null || seriales.isEmpty() || config() == null) {
      return List.of();
    }
    List<?> rs = em.createNativeQuery("SELECT DISTINCT UPPER(TRIM(i.Serial3)) FROM Ingreso i "
        + "INNER JOIN jsonb_array_elements_text(CAST(?1 AS jsonb)) s(serial) ON i.Serial = s.serial "
        + "WHERE LENGTH(TRIM(COALESCE(i.Serial3, ''))) > 1").setParameter(1, json(seriales)).getResultList();
    List<String> out = new ArrayList<>();
    for (Object o : rs) {
      if (o != null) {
        out.add(String.valueOf(o));
      }
    }
    return out;
  }

  public int borrarSinUso(List<String> tarjetas) {
    Config cfg = config();
    if (cfg == null || tarjetas == null || tarjetas.isEmpty()) {
      return 0;
    }
    String filtro = "sc.CodigoSapId = ?2 AND sc.EstadoId = ?3 AND COALESCE(sc.PalletId, 0) = 0 "
        + "AND NOT EXISTS (SELECT 1 FROM Ingreso d WHERE d.Serial3 = sc.Serial)";
    String lista = json(tarjetas);
    // ejecuto el DELETE solo si hay algo que borrar (los triggers de Ingreso no siempre aguantan sentencias vacias)
    Object cuantas = em.createNativeQuery("SELECT COUNT(*) FROM Ingreso sc "
        + "INNER JOIN jsonb_array_elements_text(CAST(?1 AS jsonb)) t(serial) ON sc.Serial = t.serial WHERE " + filtro)
        .setParameter(1, lista).setParameter(2, cfg.codigoSapId()).setParameter(3, cfg.estadoId()).getSingleResult();
    if (!(cuantas instanceof Number c) || c.intValue() == 0) {
      return 0;
    }
    int n = em.createNativeQuery("DELETE FROM Ingreso sc USING jsonb_array_elements_text(CAST(?1 AS jsonb)) t(serial) "
        + "WHERE sc.Serial = t.serial AND " + filtro).setParameter(1, lista)
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

  // leo todos los resultados: los triggers de Ingreso devuelven conteos extra
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

  public static String tarjeta(String serial, String serial3) {
    String t = limpio(serial3);
    return t.length() <= 1 || t.equals(limpio(serial)) ? null : t;
  }

  public static void parametros(PreparedStatement ps, int desde, String serial, String tarjeta, Config cfg,
      Integer usuarioId) throws SQLException {
    ps.setString(desde, limpio(serial));
    ps.setString(desde + 1, tarjeta);
    ps.setInt(desde + 2, cfg.codigoSapId());
    ps.setInt(desde + 3, cfg.estadoId());
    ps.setInt(desde + 4, usuarioId == null ? 0 : usuarioId);
  }

  private static String limpio(String s) {
    return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
  }

  private static String texto(String s, String porDefecto) {
    return s == null || s.isBlank() ? porDefecto : s.trim();
  }
}
