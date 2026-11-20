package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Aprobadores de Autogestión (general.AutogestionAprobador). Es independiente de las notificaciones
 * por evento: aquí solo se guardan los correos que reciben el enlace de aprobación.
 */
@Service
public class AutogestionAprobadorService {
  private static final String TABLA = "general.AutogestionAprobador";
  private static final Pattern CORREO = Pattern.compile("^[^@\\s;,]+@[^@\\s;,]+\\.[^@\\s;,]+$");

  @PersistenceContext
  private EntityManager em;

  public boolean instalada() {
    List<?> r = em.createNativeQuery("SELECT CASE WHEN to_regclass('" + TABLA + "') IS NULL THEN 0 ELSE 1 END")
        .getResultList();
    return !r.isEmpty() && ((Number) r.get(0)).intValue() == 1;
  }

  public Map<String, Object> estado() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("instalado", instalada());
    return m;
  }

  public List<Map<String, Object>> listar() {
    List<Map<String, Object>> out = new ArrayList<>();
    if (!instalada()) {
      return out;
    }
    for (Object[] r : filas("SELECT Id, ClienteId, Correo, Nombre, Activo, Usuario, "
        + "to_char(COALESCE(FechaModifica, Fecha), 'YYYY-MM-DD HH24:MI') FROM " + TABLA
        + " WHERE ClienteId = ?1 OR ClienteId IS NULL ORDER BY Correo, Id", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", ((Number) r[0]).intValue());
      m.put("todos", r[1] == null);
      m.put("correo", r[2]);
      m.put("nombre", r[3] == null ? "" : r[3]);
      m.put("activo", r[4] instanceof Boolean b ? b : r[4] instanceof Number n && n.intValue() != 0);
      m.put("usuario", r[5] == null ? "" : r[5]);
      m.put("fecha", r[6]);
      out.add(m);
    }
    return out;
  }

  /** Correos activos del cliente de la sesión; null si la tabla no está instalada (se usa el respaldo). */
  public List<String> correosActivos() {
    if (!instalada()) {
      return null;
    }
    List<String> out = new ArrayList<>();
    for (Object[] r : filas("SELECT DISTINCT Correo FROM " + TABLA
        + " WHERE Activo = true AND (ClienteId = ?1 OR ClienteId IS NULL)", cliente())) {
      out.add(String.valueOf(r[0]).trim().toLowerCase(Locale.ROOT));
    }
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(Map<String, Object> d, String usuario) {
    exigir();
    String correo = texto(d.get("correo")).toLowerCase(Locale.ROOT);
    if (!CORREO.matcher(correo).matches()) {
      throw new BusinessRuleException("Correo no válido.");
    }
    String nombre = corto(texto(d.get("nombre")), 150);
    boolean todos = Boolean.TRUE.equals(d.get("todos"));
    boolean activo = !Boolean.FALSE.equals(d.get("activo"));
    int id = d.get("id") instanceof Number n ? n.intValue() : 0;
    int cli = todos ? 0 : cliente();

    Number repetido = (Number) first("SELECT Id FROM " + TABLA + " WHERE Correo = ?1 AND COALESCE(ClienteId, 0) = ?2 "
        + "AND Id <> ?3 LIMIT 1", correo, cli, id);
    if (repetido != null) {
      throw new BusinessRuleException("Ese correo ya está registrado con el mismo alcance.");
    }
    if (id > 0) {
      int n = em.createNativeQuery("UPDATE " + TABLA + " SET ClienteId = NULLIF(?1, 0), Correo = ?2, Nombre = NULLIF(?3, ''), "
          + "Activo = ?4, Usuario = ?5, FechaModifica = LOCALTIMESTAMP WHERE Id = ?6 AND (ClienteId = ?7 OR ClienteId IS NULL)")
          .setParameter(1, cli).setParameter(2, correo).setParameter(3, nombre).setParameter(4, activo)
          .setParameter(5, corto(usuario, 150)).setParameter(6, id).setParameter(7, cliente()).executeUpdate();
      if (n != 1) {
        throw new BusinessRuleException("El aprobador no existe.");
      }
    } else {
      id = ((Number) first("INSERT INTO " + TABLA + " (ClienteId, Correo, Nombre, Activo, Usuario, Fecha) "
          + "VALUES (NULLIF(?1, 0), ?2, NULLIF(?3, ''), ?4, ?5, LOCALTIMESTAMP) RETURNING Id", cli, correo, nombre, activo,
          corto(usuario, 150))).intValue();
    }
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("id", id);
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public void eliminar(int id) {
    exigir();
    em.createNativeQuery("DELETE FROM " + TABLA + " WHERE Id = ?1 AND (ClienteId = ?2 OR ClienteId IS NULL)")
        .setParameter(1, id).setParameter(2, cliente()).executeUpdate();
  }

  private void exigir() {
    if (!instalada()) {
      throw new BusinessRuleException(
          "Falta instalar los aprobadores de Autogestión (script db/EJECUTAR_EN_LA_DB/1_GENERAL_una_sola_vez/05_autogestion_aprobadores.sql).");
    }
  }

  private int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    return id == null ? 0 : id;
  }

  private Object first(String sql, Object... params) {
    Query q = em.createNativeQuery(sql);
    for (int i = 0; i < params.length; i++) {
      q.setParameter(i + 1, params[i]);
    }
    List<?> r = q.getResultList();
    return r.isEmpty() ? null : r.get(0);
  }

  private List<Object[]> filas(String sql, Object... params) {
    Query q = em.createNativeQuery(sql);
    for (int i = 0; i < params.length; i++) {
      q.setParameter(i + 1, params[i]);
    }
    List<Object[]> out = new ArrayList<>();
    for (Object f : q.getResultList()) {
      out.add(f instanceof Object[] arr ? arr : new Object[] { f });
    }
    return out;
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
