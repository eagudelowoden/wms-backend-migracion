package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// en DirecTV Ecuador también actualizo las columnas viejas de Forecast para que su pa_InsertEtiquetado dé lo mismo
@Service
public class EtiquetadoForecastService {

  private static final Logger logger = LoggerFactory.getLogger(EtiquetadoForecastService.class);
  public static final String INTERRUPTOR = "ETIQUETADO_FORECAST";
  private static final String CLAVE = "ETIQUETADO_FORECAST";
  public static final String MAESTRO_TIPO_EMPAQUE = "Tipo Empaque";
  private static final long CACHE_MS = 300_000;

  private record Columnas(boolean tipoEmpaque, boolean lote, boolean anio, boolean fechaLote, boolean forecastViejo,
      long vence) {}

  private static final Map<Integer, Columnas> COLUMNAS = new ConcurrentHashMap<>();

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private ClienteConfigService config;

  @Autowired
  private MaestroParametroService maestros;

  @Autowired
  private ObjectMapper json;

  @Autowired
  private DataSource dataSource;

  @PersistenceContext
  private EntityManager em;

  public boolean activo() {
    return interruptores.activo(INTERRUPTOR);
  }

  public Map<String, Object> estado() {
    Map<String, Object> out = new LinkedHashMap<>();
    boolean activo = activo();
    out.put("activo", activo);
    if (!activo) {
      return out;
    }
    Columnas col = columnas();
    out.put("columnas", col.tipoEmpaque() || col.lote() || col.anio() || col.fechaLote());
    out.putAll(valores(col));
    List<Map<String, Object>> lotes = new ArrayList<>();
    for (Object f : em.createNativeQuery("SELECT m.Id, m.Codigo FROM Maestro m INNER JOIN TipoMaestro tm "
        + "ON tm.Id = m.TipoMaestroId WHERE UPPER(tm.Nombre) = 'FAMILIAS' AND m.Activo = true ORDER BY m.Id DESC LIMIT 80").getResultList()) {
      Object[] r = (Object[]) f;
      lotes.add(Map.of("id", ((Number) r[0]).intValue(), "nombre", String.valueOf(r[1])));
    }
    out.put("lotes", lotes);
    List<String> tipos = new ArrayList<>();
    for (MaestroParametroService.Fila f : maestros.activos(MAESTRO_TIPO_EMPAQUE)) {
      if (!f.codigo().isBlank()) {
        tipos.add(f.codigo().trim());
      }
    }
    if (tipos.isEmpty() && col.tipoEmpaque()) {
      for (Object t : em.createNativeQuery("SELECT TipoEmpaque FROM Etiquetado WHERE COALESCE(TipoEmpaque, '') <> '' "
          + "GROUP BY TipoEmpaque ORDER BY MAX(Id) DESC LIMIT 20").getResultList()) {
        tipos.add(String.valueOf(t));
      }
    }
    out.put("tipos", tipos);
    int anio = java.time.Year.now().getValue();
    List<String> anios = new ArrayList<>(List.of(String.valueOf(anio - 1), String.valueOf(anio), String.valueOf(anio + 1)));
    String fijado = texto(out.get("anio"));
    if (!fijado.isEmpty() && !anios.contains(fijado)) {
      anios.add(0, fijado);
    }
    out.put("anios", anios);
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(Map<String, Object> valores, String usuario) {
    if (!activo()) {
      throw new BusinessRuleException("El cliente no tiene activo ETIQUETADO_FORECAST.");
    }
    Map<String, Object> v = new LinkedHashMap<>();
    v.put("tipoEmpaque", corto(texto(valores.get("tipoEmpaque")), 50));
    v.put("loteId", entero(valores.get("loteId")));
    v.put("anio", corto(texto(valores.get("anio")), 20));
    v.put("fecha", corto(texto(valores.get("fecha")), 10));
    try {
      config.set(CLAVE, json.writeValueAsString(v), usuario);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new BusinessRuleException("No se pudo guardar: " + e.getMessage());
    }
    if (columnas().forecastViejo()) {
      em.createNativeQuery("UPDATE Forecast SET forecast = NULLIF(?1, ''), forecastIngreso = NULLIF(?2, ''), "
          + "familiaId = NULLIF(?3, 0) WHERE id = (SELECT id FROM Forecast ORDER BY id DESC LIMIT 1)")
          .setParameter(1, v.get("tipoEmpaque")).setParameter(2, v.get("anio")).setParameter(3, v.get("loteId"))
          .executeUpdate();
    }
    return estado();
  }

  public void aplicar(String serial) {
    try {
      if (serial == null || !activo()) {
        return;
      }
      Columnas col = columnas();
      String cfg = config.get(CLAVE, null);
      if (cfg == null || !(col.tipoEmpaque() || col.lote() || col.anio() || col.fechaLote())) {
        return;
      }
      Map<String, Object> v = json.readValue(cfg, new TypeReference<LinkedHashMap<String, Object>>() {
      });
      List<String> sets = new ArrayList<>();
      List<Object> params = new ArrayList<>();
      if (col.tipoEmpaque()) {
        sets.add("TipoEmpaque = NULLIF(?, '')");
        params.add(texto(v.get("tipoEmpaque")));
      }
      if (col.lote()) {
        sets.add("Lote = NULLIF(?, 0)");
        params.add(entero(v.get("loteId")));
      }
      if (col.anio()) {
        sets.add("[Año] = NULLIF(?, '')");
        params.add(texto(v.get("anio")));
      }
      if (col.fechaLote()) {
        sets.add("FechaLote = CAST(NULLIF(?, '') AS timestamp)");
        params.add(texto(v.get("fecha")));
      }
      params.add(serial);
      try (Connection conn = dataSource.getConnection();
          PreparedStatement ps = conn.prepareStatement("UPDATE Etiquetado SET " + String.join(", ", sets)
              + " WHERE Id = (SELECT MAX(Id) FROM Etiquetado WHERE Serial = CAST(? AS VARCHAR(50)))")) {
        for (int i = 0; i < params.size(); i++) {
          ps.setObject(i + 1, params.get(i));
        }
        ps.executeUpdate();
      }
    } catch (Exception e) {
      logger.warn("[EtiquetadoForecast] {} no se actualizó: {}", serial, e.getMessage());
    }
  }

  private Map<String, Object> valores(Columnas col) {
    Map<String, Object> v = new LinkedHashMap<>();
    String cfg = config.get(CLAVE, null);
    if (cfg != null) {
      try {
        v.putAll(json.readValue(cfg, new TypeReference<LinkedHashMap<String, Object>>() {
        }));
        return v;
      } catch (Exception e) {
        logger.warn("[EtiquetadoForecast] configuración no válida: {}", e.getMessage());
      }
    }
    if (col.forecastViejo()) {
      List<?> rs = em.createNativeQuery("SELECT COALESCE(forecast, ''), COALESCE(familiaId, 0), "
          + "COALESCE(forecastIngreso, '') FROM Forecast ORDER BY id DESC LIMIT 1").getResultList();
      if (!rs.isEmpty()) {
        Object[] r = (Object[]) rs.get(0);
        v.put("tipoEmpaque", texto(r[0]));
        v.put("loteId", entero(r[1]));
        v.put("anio", texto(r[2]));
      }
    }
    v.putIfAbsent("tipoEmpaque", "");
    v.putIfAbsent("loteId", 0);
    v.putIfAbsent("anio", "");
    v.putIfAbsent("fecha", "");
    return v;
  }

  private Columnas columnas() {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    Columnas c = cliente == null ? null : COLUMNAS.get(cliente);
    if (c != null && c.vence() > System.currentTimeMillis()) {
      return c;
    }
    Object[] r = (Object[]) em.createNativeQuery("SELECT CASE WHEN general.col_length('Etiquetado', 'TipoEmpaque') IS NULL "
        + "THEN 0 ELSE 1 END, CASE WHEN general.col_length('Etiquetado', 'Lote') IS NULL THEN 0 ELSE 1 END, "
        + "CASE WHEN general.col_length('Etiquetado', 'Año') IS NULL THEN 0 ELSE 1 END, "
        + "CASE WHEN general.col_length('Etiquetado', 'FechaLote') IS NULL THEN 0 ELSE 1 END, "
        + "CASE WHEN general.col_length('Forecast', 'forecastIngreso') IS NULL THEN 0 ELSE 1 END").getSingleResult();
    c = new Columnas(uno(r[0]), uno(r[1]), uno(r[2]), uno(r[3]), uno(r[4]), System.currentTimeMillis() + CACHE_MS);
    if (cliente != null) {
      COLUMNAS.put(cliente, c);
    }
    return c;
  }

  private static boolean uno(Object o) {
    return o instanceof Number n && n.intValue() == 1;
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static int entero(Object o) {
    if (o instanceof Number n) {
      return n.intValue();
    }
    try {
      return o == null || String.valueOf(o).isBlank() ? 0 : Integer.parseInt(String.valueOf(o).trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static String corto(String s, int max) {
    return s.length() > max ? s.substring(0, max) : s;
  }
}
