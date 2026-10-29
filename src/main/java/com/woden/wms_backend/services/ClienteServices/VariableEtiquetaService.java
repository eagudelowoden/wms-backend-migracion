package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.util.ReglaCampo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class VariableEtiquetaService {

  private static final Logger logger = LoggerFactory.getLogger(VariableEtiquetaService.class);
  private static final long CACHE_MS = 60_000;
  public static final List<String> APLICA = List.of("ETIQUETADO", "EMPAQUE", "AMBOS");
  public static final List<String> ORIGENES = List.of("SERIAL", "MAC", "SERIAL3", "SERIAL4", "SERIAL5", "CODIGOSAP",
      "DESCRIPCION", "LOTE", "MODELO", "TIPOLOGIA", "CONSULTA");
  private static final Pattern NOMBRE = Pattern.compile("^[A-Za-z][A-Za-z0-9]{2,39}$");
  private static final Pattern PROHIBIDO = Pattern.compile("\\b(insert|update|delete|merge|drop|alter|create|truncate|"
      + "exec|execute|grant|revoke|deny|into|backup|restore|shutdown|dbcc|waitfor|openrowset|opendatasource|openquery|"
      + "sp_\\w*|xp_\\w*)\\b", Pattern.CASE_INSENSITIVE);

  private static final List<Map<String, String>> INTEGRADAS = new ArrayList<>();

  static {
    integrada("<campo>N", "ETIQUETADO", "Campos de la etiqueta (Administración > Etiquetas): nombre + posición; valor serial, mac, unitserial3-5, variable1-4 o el índice viejo 0 serial, 1 mac, 2-5 variable1-4");
    integrada("fechaN", "ETIQUETADO", "Fecha de la etiqueta");
    integrada("codeinModelN", "ETIQUETADO", "Últimos 4 caracteres de la MAC");
    integrada("unitSerial3N · unitSerial4N · unitSerial5N", "ETIQUETADO", "Serial3, Serial4 y Serial5 del equipo");
    integrada("variable1_N … variable4_N", "ETIQUETADO", "Lo escaneado en Variable 1-4, o lo que calcula Códigos SAP > Campos calculados");
    integrada("familiaN · descripcionN · codigosapN · usuarioN · tipologiaN · modeloN · loteN", "ETIQUETADO", "Solo con Lectura variable: datos del maestro del código SAP y del equipo (descripción hasta 35 caracteres)");
    integrada("codProveedorN · proveedorN · passModelN · modelCodigoN · modelDescripcionN · modelDetalleN", "ETIQUETADO", "Solo con Lectura variable: proveedor y modelo (maestro de modelos)");
    integrada("<campo>N", "EMPAQUE", "Campos de la etiqueta: propiedad del equipo (serial, mac, smartCard, serial3, serial4, codigoSap, descripcion, nivel, lote, modelo, tipologia…) o índice 0-10; virtual = Serial3 o la smartcard");
    integrada("serialN · macN · smartCardN · serial3N · serial4N", "EMPAQUE", "Datos de cada equipo de la caja por posición");
    integrada("codigosapN · descripcionN · nivelN · loteN · modeloN · tipologiaN", "EMPAQUE", "Datos de cada equipo de la caja por posición");
    integrada("familia · descripcion · codigosap · usuario · tipologia · modelo · lote · fecha", "EMPAQUE", "Datos generales de la caja (sin posición)");
    integrada("pallet · caja · pNumberBox", "EMPAQUE", "Pallet, \"Caja: N\" y número de caja");
    integrada("smartCardSerial · smartCardCodigoSap · smartCardDescripcion", "EMPAQUE", "Smartcard de la caja");
    integrada("codProveedor · proveedor · modelCodigo · modelDescripcion · modelDetalle", "EMPAQUE", "Solo en etiqueta unitaria");
    integrada("Box", "EMPAQUE", "Número de caja con el interruptor REEMPLAZAR_VARIABLE_BOX_PRN");
  }

  private static void integrada(String nombre, String aplica, String origen) {
    Map<String, String> m = new LinkedHashMap<>();
    m.put("nombre", nombre);
    m.put("aplica", aplica);
    m.put("origen", origen);
    INTEGRADAS.add(m);
  }

  public record Variable(int id, String nombre, String descripcion, String aplica, String origen, String operacion,
      String parametro1, String parametro2, String consulta, boolean activo) {}

  private record Lectura(List<Variable> variables, long vence) {}

  private static final Map<Integer, Lectura> CACHE = new ConcurrentHashMap<>();

  @Autowired
  private DataSource dataSource;

  @PersistenceContext
  private EntityManager em;


  public Map<String, Object> listar() {
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("instalado", tabla());
    out.put("integradas", INTEGRADAS);
    out.put("aplica", APLICA);
    out.put("origenes", ORIGENES);
    out.put("operaciones", ReglaCampo.OPERACIONES);
    List<Map<String, Object>> propias = new ArrayList<>();
    if (tabla()) {
      for (Variable v : leer(cliente(), false)) {
        propias.add(mapa(v));
      }
    }
    out.put("propias", propias);
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(Map<String, Object> m, String usuario) {
    if (!tabla()) {
      throw new BusinessRuleException("Falta el script 2026-10-29_parametros_wms.sql en WmsWdGeneral.");
    }
    Variable v = validar(m);
    int cliente = cliente();
    if (v.id() > 0) {
      em.createNativeQuery("UPDATE WmsWdGeneral.dbo.VariableEtiqueta SET Nombre = ?1, Descripcion = ?2, Aplica = ?3, "
          + "Origen = ?4, Operacion = ?5, Parametro1 = ?6, Parametro2 = ?7, Consulta = ?8, Activo = ?9, Usuario = ?10, "
          + "Fecha = GETDATE() WHERE Id = ?11 AND ClienteId = ?12")
          .setParameter(1, v.nombre()).setParameter(2, v.descripcion()).setParameter(3, v.aplica())
          .setParameter(4, v.origen()).setParameter(5, v.operacion()).setParameter(6, v.parametro1())
          .setParameter(7, v.parametro2()).setParameter(8, v.consulta()).setParameter(9, v.activo())
          .setParameter(10, corto(usuario, 150)).setParameter(11, v.id()).setParameter(12, cliente).executeUpdate();
    } else {
      Object existe = em.createNativeQuery("SELECT COUNT(*) FROM WmsWdGeneral.dbo.VariableEtiqueta WHERE ClienteId = ?1 "
          + "AND Nombre = ?2").setParameter(1, cliente).setParameter(2, v.nombre()).getSingleResult();
      if (existe instanceof Number n && n.intValue() > 0) {
        throw new BusinessRuleException("Ya existe la variable " + v.nombre() + ".");
      }
      em.createNativeQuery("INSERT INTO WmsWdGeneral.dbo.VariableEtiqueta (ClienteId, Nombre, Descripcion, Aplica, Origen, "
          + "Operacion, Parametro1, Parametro2, Consulta, Activo, Usuario, Fecha) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, "
          + "?9, ?10, ?11, GETDATE())")
          .setParameter(1, cliente).setParameter(2, v.nombre()).setParameter(3, v.descripcion())
          .setParameter(4, v.aplica()).setParameter(5, v.origen()).setParameter(6, v.operacion())
          .setParameter(7, v.parametro1()).setParameter(8, v.parametro2()).setParameter(9, v.consulta())
          .setParameter(10, v.activo()).setParameter(11, corto(usuario, 150)).executeUpdate();
    }
    CACHE.remove(cliente);
    return listar();
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> eliminar(int id) {
    int cliente = cliente();
    em.createNativeQuery("DELETE FROM WmsWdGeneral.dbo.VariableEtiqueta WHERE Id = ?1 AND ClienteId = ?2")
        .setParameter(1, id).setParameter(2, cliente).executeUpdate();
    CACHE.remove(cliente);
    return listar();
  }

  public Map<String, Object> probar(Map<String, Object> m, String serial) {
    Variable v = validar(m);
    IngresoModel equipo = equipo(serial);
    if (equipo == null) {
      throw new BusinessRuleException("El serial " + serial + " no está en Ingreso.");
    }
    Map<String, Object> out = new LinkedHashMap<>();
    long inicio = System.nanoTime();
    String origen = origen(v, equipo);
    out.put("ms", (System.nanoTime() - inicio) / 1_000_000);
    out.put("origen", nulo(origen));
    out.put("valor", ReglaCampo.aplicar(v.operacion(), origen, v.parametro1(), v.parametro2()));
    return out;
  }


  public String reemplazar(String zpl, String tipo, String sufijo, IngresoModel equipo) {
    if (zpl == null || equipo == null) {
      return zpl;
    }
    List<Variable> vars = activas();
    if (vars.isEmpty()) {
      return zpl;
    }
    for (Variable v : vars) {
      if (!v.aplica().equals("AMBOS") && !v.aplica().equals(tipo)) {
        continue;
      }
      String marca = v.nombre() + sufijo;
      if (!zpl.contains(marca)) {
        continue;
      }
      try {
        zpl = zpl.replace(marca, valor(v, equipo));
      } catch (Exception e) {
        logger.warn("[VariablesEtiqueta] {} de {}: {}", v.nombre(), equipo.getSerial(), e.getMessage());
        zpl = zpl.replace(marca, "");
      }
    }
    return zpl;
  }

  private String valor(Variable v, IngresoModel e) {
    return ReglaCampo.aplicar(v.operacion(), origen(v, e), v.parametro1(), v.parametro2());
  }

  private String origen(Variable v, IngresoModel e) {
    return switch (v.origen()) {
      case "SERIAL" -> e.getSerial();
      case "MAC" -> e.getMac();
      case "SERIAL3" -> e.getSerial3();
      case "SERIAL4" -> e.getSerial4();
      case "SERIAL5" -> e.getSerial5();
      case "CODIGOSAP" -> e.getCodigoSap();
      case "DESCRIPCION" -> e.getDescripcion();
      case "LOTE" -> e.getLote();
      case "MODELO" -> e.getModelo();
      case "TIPOLOGIA" -> e.getTipologia();
      case "CONSULTA" -> consulta(v.consulta(), e);
      default -> "";
    };
  }

  private String consulta(String sql, IngresoModel e) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("EXEC sys.sp_executesql ?, N'@serial VARCHAR(100), "
            + "@mac VARCHAR(100), @codigoSap VARCHAR(100)', ?, ?, ?")) {
      ps.setQueryTimeout(5);
      ps.setString(1, sql);
      ps.setString(2, nulo(e.getSerial()));
      ps.setString(3, nulo(e.getMac()));
      ps.setString(4, nulo(e.getCodigoSap()));
      boolean hay = ps.execute();
      while (!hay && ps.getUpdateCount() != -1) {
        hay = ps.getMoreResults();
      }
      if (!hay) {
        return "";
      }
      try (ResultSet rs = ps.getResultSet()) {
        return rs.next() ? nulo(rs.getString(1)) : "";
      }
    } catch (Exception ex) {
      throw new BusinessRuleException("La consulta no se pudo ejecutar: " + ex.getMessage());
    }
  }

  private IngresoModel equipo(String serial) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 i.Serial, i.Mac, i.Serial3, i.Serial4, i.Serial5, "
            + "cs.Codigo, cs.Descripcion, l.nombre, t.Codigo FROM Ingreso i LEFT JOIN CodigoSap cs ON cs.Id = i.CodigoSapId "
            + "LEFT JOIN Lote l ON l.id = i.loteId LEFT JOIN Maestro t ON t.Id = i.TipologiaId "
            + "WHERE i.Serial = CAST(? AS VARCHAR(50)) ORDER BY i.Id DESC")) {
      ps.setString(1, serial);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        IngresoModel m = new IngresoModel();
        m.setSerial(rs.getString(1));
        m.setMac(rs.getString(2));
        m.setSerial3(rs.getString(3));
        m.setSerial4(rs.getString(4));
        m.setSerial5(rs.getString(5));
        m.setCodigoSap(rs.getString(6));
        m.setDescripcion(rs.getString(7));
        m.setLote(rs.getString(8));
        m.setTipologia(rs.getString(9));
        return m;
      }
    } catch (Exception e) {
      throw new BusinessRuleException("No se pudo leer el serial: " + e.getMessage());
    }
  }

  private List<Variable> activas() {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    if (cliente == null) {
      return List.of();
    }
    Lectura l = CACHE.get(cliente);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.variables();
    }
    List<Variable> vars = List.of();
    try {
      vars = tabla() ? leer(cliente, true) : List.of();
    } catch (Exception e) {
      logger.warn("[VariablesEtiqueta] del cliente {} no se pudieron leer: {}", cliente, e.getMessage());
    }
    CACHE.put(cliente, new Lectura(vars, System.currentTimeMillis() + CACHE_MS));
    return vars;
  }

  private List<Variable> leer(int cliente, boolean soloActivas) {
    List<Variable> out = new ArrayList<>();
    for (Object f : em.createNativeQuery("SELECT Id, Nombre, ISNULL(Descripcion, ''), Aplica, Origen, Operacion, "
        + "ISNULL(Parametro1, ''), ISNULL(Parametro2, ''), ISNULL(Consulta, ''), Activo FROM WmsWdGeneral.dbo.VariableEtiqueta "
        + "WHERE ClienteId = ?1" + (soloActivas ? " AND Activo = 1" : "") + " ORDER BY LEN(Nombre) DESC, Nombre")
        .setParameter(1, cliente).getResultList()) {
      Object[] r = (Object[]) f;
      out.add(new Variable(((Number) r[0]).intValue(), texto(r[1]), texto(r[2]), texto(r[3]), texto(r[4]), texto(r[5]),
          texto(r[6]), texto(r[7]), texto(r[8]), r[9] instanceof Boolean b ? b : r[9] instanceof Number n && n.intValue() != 0));
    }
    return out;
  }

  private Variable validar(Map<String, Object> m) {
    String nombre = texto(m.get("nombre"));
    if (!NOMBRE.matcher(nombre).matches()) {
      throw new BusinessRuleException("El nombre debe empezar con una letra y llevar solo letras y números (3 a 40).");
    }
    String aplica = texto(m.get("aplica")).toUpperCase();
    String origen = texto(m.get("origen")).toUpperCase();
    String operacion = texto(m.get("operacion")).toUpperCase();
    if (operacion.isEmpty()) {
      operacion = "COPIAR";
    }
    if (!APLICA.contains(aplica)) {
      throw new BusinessRuleException("Aplica no válido: " + aplica);
    }
    if (!ORIGENES.contains(origen)) {
      throw new BusinessRuleException("Origen no válido: " + origen);
    }
    if (!ReglaCampo.OPERACIONES.contains(operacion)) {
      throw new BusinessRuleException("Operación no válida: " + operacion);
    }
    String consulta = texto(m.get("consulta"));
    if (origen.equals("CONSULTA")) {
      String c = consulta.replaceAll("\\s+", " ").trim();
      if (!c.toLowerCase().startsWith("select ")) {
        throw new BusinessRuleException("La consulta debe empezar con SELECT.");
      }
      if (c.contains(";") || c.contains("--") || c.contains("/*") || PROHIBIDO.matcher(c).find()) {
        throw new BusinessRuleException("La consulta solo puede leer datos (un SELECT, sin ; ni comentarios).");
      }
    } else {
      consulta = "";
    }
    int id = m.get("id") instanceof Number n ? n.intValue() : 0;
    return new Variable(id, nombre, corto(texto(m.get("descripcion")), 200), aplica, origen, operacion,
        corto(texto(m.get("parametro1")), 100), corto(texto(m.get("parametro2")), 100), consulta,
        !Boolean.FALSE.equals(m.get("activo")));
  }

  private static Map<String, Object> mapa(Variable v) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", v.id());
    m.put("nombre", v.nombre());
    m.put("descripcion", v.descripcion());
    m.put("aplica", v.aplica());
    m.put("origen", v.origen());
    m.put("operacion", v.operacion());
    m.put("parametro1", v.parametro1());
    m.put("parametro2", v.parametro2());
    m.put("consulta", v.consulta());
    m.put("activo", v.activo());
    return m;
  }

  private boolean tabla() {
    Object r = em.createNativeQuery("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.VariableEtiqueta', 'U') IS NULL THEN 0 "
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

  private static String nulo(String s) {
    return s == null ? "" : s;
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
