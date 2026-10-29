package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.util.ReglaCampo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Códigos SAP > Campos calculados (WmsWdGeneral.dbo.CodigoSapRegla, por cliente y código SAP): reemplaza los CASE por
 * CodigoSapId que había en pa_InsertEntry de algunas bases (MAC = serial hexadecimal + 1 en Tigo Costa Rica, Serial3 /
 * Serial4 = MAC + 1 / + 2 en Tigo Nicaragua y Global Liberty) y calcula las variables de Etiquetado.
 *   Ingreso:    MAC, SERIAL3, SERIAL4, SERIAL5 (en ese orden; cada uno puede usar el ya calculado)
 *   Etiquetado: VARIABLE1 a VARIABLE4
 * Los campos del equipo se calculan al ingresar (Momento INGRESO) o al diagnosticar (DIAGNOSTICO: lo que Tigo Costa Rica
 * tenía en pa_InsertDiagnostic, Serial3 = serial + 2 y Serial4 = sus últimos 6, o Serial3 = últimos 4 del serial).
 * Sin reglas el ingreso, el diagnóstico y el etiquetado quedan exactamente como siempre. Las reglas del cliente se
 * guardan 60 s.
 */
@Service
public class CodigoSapReglaService {

  private static final Logger logger = LoggerFactory.getLogger(CodigoSapReglaService.class);
  private static final long CACHE_MS = 60_000;
  public static final List<String> CAMPOS_INGRESO = List.of("MAC", "SERIAL3", "SERIAL4", "SERIAL5");
  public static final List<String> CAMPOS_VARIABLE = List.of("VARIABLE1", "VARIABLE2", "VARIABLE3", "VARIABLE4");
  public static final List<String> ORIGENES = List.of("SERIAL", "MAC", "SERIAL3", "SERIAL4", "SERIAL5");
  public static final List<String> MOMENTOS = List.of("INGRESO", "DIAGNOSTICO");

  public record Regla(String campo, String origen, String operacion, String parametro1, String parametro2,
      boolean activo, String momento) {}

  /** Serial, MAC y Serial3-5 de un equipo (los que entran o los que se calculan). */
  public static final class Campos {
    public String serial;
    public String mac;
    public String serial3;
    public String serial4;
    public String serial5;

    public Campos(String serial, String mac, String serial3, String serial4, String serial5) {
      this.serial = serial;
      this.mac = mac;
      this.serial3 = serial3;
      this.serial4 = serial4;
      this.serial5 = serial5;
    }

    String get(String campo) {
      return switch (campo) {
        case "SERIAL" -> serial;
        case "MAC" -> mac;
        case "SERIAL3" -> serial3;
        case "SERIAL4" -> serial4;
        case "SERIAL5" -> serial5;
        default -> "";
      };
    }

    void set(String campo, String valor) {
      switch (campo) {
        case "MAC" -> mac = valor;
        case "SERIAL3" -> serial3 = valor;
        case "SERIAL4" -> serial4 = valor;
        case "SERIAL5" -> serial5 = valor;
        default -> {
        }
      }
    }
  }

  private record Lectura(Map<Integer, List<Regla>> reglas, long vence) {}

  private static final Map<Integer, Lectura> CACHE = new ConcurrentHashMap<>();

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private DataSource dataSource;

  // ── administración ─────────────────────────────────────────────────────────

  public Map<String, Object> listar(int codigoSapId) {
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("instalado", tabla());
    out.put("campos", List.of(CAMPOS_INGRESO, CAMPOS_VARIABLE));
    out.put("origenes", ORIGENES);
    out.put("operaciones", ReglaCampo.OPERACIONES);
    out.put("momentos", MOMENTOS);
    List<Map<String, Object>> reglas = new ArrayList<>();
    if (tabla()) {
      for (Object f : em.createNativeQuery("SELECT Campo, Origen, Operacion, ISNULL(Parametro1, ''), "
          + "ISNULL(Parametro2, ''), Activo, ISNULL(Momento, 'INGRESO') FROM WmsWdGeneral.dbo.CodigoSapRegla "
          + "WHERE ClienteId = ?1 AND CodigoSapId = ?2")
          .setParameter(1, cliente()).setParameter(2, codigoSapId).getResultList()) {
        Object[] r = (Object[]) f;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("campo", r[0]);
        m.put("origen", r[1]);
        m.put("operacion", r[2]);
        m.put("parametro1", r[3]);
        m.put("parametro2", r[4]);
        m.put("activo", r[5] instanceof Boolean b ? b : r[5] instanceof Number n && n.intValue() != 0);
        m.put("momento", r[6]);
        reglas.add(m);
      }
    }
    out.put("reglas", reglas);
    return out;
  }

  /** Reemplaza las reglas del código SAP por las enviadas (una por campo). */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(int codigoSapId, List<Map<String, Object>> reglas, String usuario) {
    if (!tabla()) {
      throw new BusinessRuleException("Falta el script 2026-10-29_parametros_wms.sql en WmsWdGeneral.");
    }
    int cliente = cliente();
    Map<String, Regla> porCampo = new LinkedHashMap<>();
    for (Map<String, Object> m : reglas == null ? List.<Map<String, Object>>of() : reglas) {
      String campo = texto(m.get("campo")).toUpperCase();
      String origen = texto(m.get("origen")).toUpperCase();
      String operacion = texto(m.get("operacion")).toUpperCase();
      if (!CAMPOS_INGRESO.contains(campo) && !CAMPOS_VARIABLE.contains(campo)) {
        throw new BusinessRuleException("Campo no válido: " + campo);
      }
      if (!ORIGENES.contains(origen)) {
        throw new BusinessRuleException("Origen no válido: " + origen);
      }
      if (!ReglaCampo.OPERACIONES.contains(operacion)) {
        throw new BusinessRuleException("Operación no válida: " + operacion);
      }
      if (campo.equals(origen)) {
        throw new BusinessRuleException("El campo " + campo + " no puede calcularse de sí mismo.");
      }
      if (porCampo.containsKey(campo)) {
        throw new BusinessRuleException("El campo " + campo + " está dos veces.");
      }
      String momento = texto(m.get("momento")).toUpperCase();
      if (CAMPOS_VARIABLE.contains(campo)) {
        momento = "ETIQUETADO";
      } else if (momento.isEmpty()) {
        momento = "INGRESO";
      } else if (!MOMENTOS.contains(momento)) {
        throw new BusinessRuleException("Momento no válido: " + momento);
      }
      porCampo.put(campo, new Regla(campo, origen, operacion, corto(texto(m.get("parametro1")), 100),
          corto(texto(m.get("parametro2")), 100), !Boolean.FALSE.equals(m.get("activo")), momento));
    }
    em.createNativeQuery("DELETE FROM WmsWdGeneral.dbo.CodigoSapRegla WHERE ClienteId = ?1 AND CodigoSapId = ?2")
        .setParameter(1, cliente).setParameter(2, codigoSapId).executeUpdate();
    for (Regla r : porCampo.values()) {
      em.createNativeQuery("INSERT INTO WmsWdGeneral.dbo.CodigoSapRegla (ClienteId, CodigoSapId, Campo, Origen, "
          + "Operacion, Parametro1, Parametro2, Activo, Momento, Usuario, Fecha) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, "
          + "?9, ?10, GETDATE())")
          .setParameter(1, cliente).setParameter(2, codigoSapId).setParameter(3, r.campo()).setParameter(4, r.origen())
          .setParameter(5, r.operacion()).setParameter(6, r.parametro1()).setParameter(7, r.parametro2())
          .setParameter(8, r.activo()).setParameter(9, r.momento()).setParameter(10, corto(usuario, 150))
          .executeUpdate();
    }
    CACHE.remove(cliente);
    return listar(codigoSapId);
  }

  /** Vista previa en el editor: los campos calculados con un serial / MAC de ejemplo. */
  public Map<String, String> probar(List<Map<String, Object>> reglas, String serial, String mac) {
    List<Regla> lista = new ArrayList<>();
    for (Map<String, Object> m : reglas == null ? List.<Map<String, Object>>of() : reglas) {
      lista.add(new Regla(texto(m.get("campo")).toUpperCase(), texto(m.get("origen")).toUpperCase(),
          texto(m.get("operacion")).toUpperCase(), texto(m.get("parametro1")), texto(m.get("parametro2")), true,
          texto(m.get("momento")).toUpperCase()));
    }
    Campos c = new Campos(serial, mac, "", "", "");
    Map<String, String> out = new LinkedHashMap<>();
    for (String campo : CAMPOS_INGRESO) {
      for (Regla r : lista) {
        if (r.campo().equals(campo)) {
          c.set(campo, ReglaCampo.aplicar(r.operacion(), c.get(r.origen()), r.parametro1(), r.parametro2()));
          out.put(campo, c.get(campo));
        }
      }
    }
    for (Regla r : lista) {
      if (CAMPOS_VARIABLE.contains(r.campo())) {
        out.put(r.campo(), ReglaCampo.aplicar(r.operacion(), c.get(r.origen()), r.parametro1(), r.parametro2()));
      }
    }
    return out;
  }

  // ── uso en Ingreso y Etiquetado ─────────────────────────────────────────────

  /** Calcula MAC y Serial3-5 del ingreso según las reglas de su código SAP; sin reglas no cambia nada. */
  public void aplicarIngreso(Integer codigoSapId, Campos c) {
    if (codigoSapId == null) {
      return;
    }
    List<Regla> reglas = activas().get(codigoSapId);
    if (reglas == null || reglas.isEmpty()) {
      return;
    }
    for (String campo : CAMPOS_INGRESO) {
      for (Regla r : reglas) {
        if (r.campo().equals(campo) && !"DIAGNOSTICO".equals(r.momento())) {
          c.set(campo, ReglaCampo.aplicar(r.operacion(), c.get(r.origen()), r.parametro1(), r.parametro2()));
        }
      }
    }
  }

  /**
   * Después de crear el diagnóstico: calcula los campos con Momento DIAGNOSTICO sobre el equipo en Ingreso y los
   * actualiza. Nunca lanza error: el diagnóstico ya quedó creado.
   */
  public void aplicarDiagnostico(Integer codigoSapId, String serial) {
    List<Regla> reglas = codigoSapId == null || serial == null ? null : activas().get(codigoSapId);
    if (reglas == null || reglas.stream().noneMatch(r -> "DIAGNOSTICO".equals(r.momento()))) {
      return;
    }
    try (Connection conn = dataSource.getConnection()) {
      Campos c;
      try (PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 Serial, Mac, Serial3, Serial4, Serial5 FROM Ingreso "
          + "WHERE Serial = CAST(? AS VARCHAR(50)) ORDER BY Id DESC")) {
        ps.setString(1, serial.trim().toUpperCase());
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) {
            return;
          }
          c = new Campos(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
        }
      }
      List<String> sets = new ArrayList<>();
      List<String> valores = new ArrayList<>();
      for (String campo : CAMPOS_INGRESO) {
        for (Regla r : reglas) {
          if (r.campo().equals(campo) && "DIAGNOSTICO".equals(r.momento())) {
            String v = ReglaCampo.aplicar(r.operacion(), c.get(r.origen()), r.parametro1(), r.parametro2()).toUpperCase();
            c.set(campo, v);
            sets.add((campo.equals("MAC") ? "Mac" : "Serial" + campo.substring(6)) + " = ?");
            valores.add(v);
          }
        }
      }
      try (PreparedStatement ps = conn.prepareStatement("UPDATE Ingreso SET " + String.join(", ", sets)
          + " WHERE Serial = ?")) {
        for (int i = 0; i < valores.size(); i++) {
          ps.setString(i + 1, valores.get(i));
        }
        ps.setString(valores.size() + 1, serial.trim().toUpperCase());
        ps.executeUpdate();
      }
    } catch (Exception e) {
      logger.warn("[CamposCalculados] diagnóstico de {}: {}", serial, e.getMessage());
    }
  }

  /** Variable1-4 calculadas para el código SAP (solo las que tienen regla). */
  public Map<String, String> variables(Integer codigoSapId, Campos c) {
    Map<String, String> out = new HashMap<>();
    List<Regla> reglas = codigoSapId == null ? null : activas().get(codigoSapId);
    if (reglas == null) {
      return out;
    }
    for (Regla r : reglas) {
      if (CAMPOS_VARIABLE.contains(r.campo())) {
        try {
          out.put(r.campo().toLowerCase(), ReglaCampo.aplicar(r.operacion(), c.get(r.origen()), r.parametro1(),
              r.parametro2()));
        } catch (BusinessRuleException e) {
          logger.warn("[CamposCalculados] {} de {}: {}", r.campo(), c.serial, e.getMessage());
        }
      }
    }
    return out;
  }

  /** Si algún código SAP del cliente calcula Variable1-4 (sin reglas, Etiquetado no consulta nada más). */
  public boolean hayVariables() {
    return activas().values().stream().flatMap(List::stream).anyMatch(r -> CAMPOS_VARIABLE.contains(r.campo()));
  }

  public boolean tieneVariables(Integer codigoSapId) {
    List<Regla> reglas = codigoSapId == null ? null : activas().get(codigoSapId);
    return reglas != null && reglas.stream().anyMatch(r -> CAMPOS_VARIABLE.contains(r.campo()));
  }

  private Map<Integer, List<Regla>> activas() {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    if (cliente == null) {
      return Map.of();
    }
    Lectura l = CACHE.get(cliente);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.reglas();
    }
    Map<Integer, List<Regla>> out = new HashMap<>();
    try {
      if (tabla()) {
        for (Object f : em.createNativeQuery("SELECT CodigoSapId, Campo, Origen, Operacion, ISNULL(Parametro1, ''), "
            + "ISNULL(Parametro2, ''), ISNULL(Momento, 'INGRESO') FROM WmsWdGeneral.dbo.CodigoSapRegla "
            + "WHERE ClienteId = ?1 AND Activo = 1")
            .setParameter(1, cliente).getResultList()) {
          Object[] r = (Object[]) f;
          out.computeIfAbsent(((Number) r[0]).intValue(), k -> new ArrayList<>()).add(new Regla(texto(r[1]),
              texto(r[2]), texto(r[3]), texto(r[4]), texto(r[5]), true, texto(r[6])));
        }
      }
    } catch (Exception e) {
      logger.warn("[CamposCalculados] reglas del cliente {} no se pudieron leer: {}", cliente, e.getMessage());
    }
    CACHE.put(cliente, new Lectura(out, System.currentTimeMillis() + CACHE_MS));
    return out;
  }

  private boolean tabla() {
    Object r = em.createNativeQuery("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.CodigoSapRegla', 'U') IS NULL THEN 0 "
        + "ELSE 1 END").getSingleResult();
    return r instanceof Number n && n.intValue() == 1;
  }

  private static int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    if (id == null || id <= 0) {
      throw new BusinessRuleException("No se identificó el cliente de la sesión.");
    }
    return id;
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
