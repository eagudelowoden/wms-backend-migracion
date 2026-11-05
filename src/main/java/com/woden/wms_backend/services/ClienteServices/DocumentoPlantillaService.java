package com.woden.wms_backend.services.ClienteServices;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class DocumentoPlantillaService {

  private static final Logger logger = LoggerFactory.getLogger(DocumentoPlantillaService.class);
  private static final String G = "general.";
  public static final String BOLETA = "BOLETA_ENTREGA";
  public static final String PALLET = "PALLET";
  public static final String CORREO_PREALERTA = "CORREO_PREALERTA";
  public static final String CORREO_SERIAL_MAC = "CORREO_SERIAL_MAC";
  private static final Map<String, String> TIPOS = new LinkedHashMap<>();
  private static final Map<String, List<Map<String, String>>> VARIABLES = new LinkedHashMap<>();
  private static final int MAX_IMAGEN = 600_000;
  private static final int MAX_CONFIG = 2_000_000;

  static {
    TIPOS.put(BOLETA, "Boleta de entrega");
    TIPOS.put(PALLET, "Boleta de pallet");
    TIPOS.put(CORREO_PREALERTA, "Correo de prealerta");
    TIPOS.put(CORREO_SERIAL_MAC, "Correo ERROR SERIAL-MAC");

    VARIABLES.put(BOLETA, vars(
        "documento", "Documento / nota de remisión", "fecha_entrega", "Fecha de entrega", "origen", "Área origen",
        "destino", "Área destino", "usuario", "Quien entrega", "observaciones", "Observaciones",
        "cliente", "Cliente", "empresa_nombre", "Empresa", "empresa_ruc", "RUC", "empresa_telefono", "Teléfono",
        "empresa_email", "Correo", "empresa_web", "Sitio web", "total_unidades", "Total unidades",
        "total_equipos", "Total equipos", "total_accesorios", "Total accesorios", "total_smartcards", "Total smartcards",
        "total_pallets", "Total pallets", "total_cajas", "Total cajas", "lotes", "Lotes de los pallets",
        "responsable_op", "Responsable de operación (Maestros > Responsable Op)",
        "fecha_impresion", "Fecha de impresión", "pagina", "Página", "paginas", "Total de páginas",
        "n", "Fila #", "no_pallet", "No. pallet", "codigo_sap", "Código SAP", "descripcion", "Descripción",
        "cantidad", "Cantidad (equipos; si no hay, accesorios; si no, smartcards)", "equipos", "Equipos",
        "accesorios", "Accesorios", "smartcards", "Smartcards", "cajas", "Cajas", "tipologia", "Tipología",
        "posicion", "Posición", "lote", "Lote", "fecha_pallet", "Fecha del pallet"));
    VARIABLES.put(PALLET, vars(
        "cliente", "Cliente", "pallet", "No. pallet", "posicion", "Posición", "fecha", "Fecha creación",
        "origen", "Origen", "destino", "Destino", "tipologia", "Tipología", "codigo_sap", "Código SAP",
        "modelo", "Modelo", "usuario", "Usuario creación", "nombre_usuario", "NombreUsuario",
        "lote", "Lote", "cantidad", "Cantidad"));
    VARIABLES.put(CORREO_PREALERTA, vars(
        "cliente", "Cliente", "prealerta", "Prealerta", "guia", "Guía", "tipo_origen", "Tipo origen",
        "origen", "Origen", "creada", "Fecha de creación", "cerrada", "Fecha de cierre", "usuario", "Quien cierra",
        "modo", "automáticamente / manualmente", "tipo_cierre", "Automático / Manual", "esperados", "Esperados",
        "recibidos", "Recibidos", "faltantes", "Faltantes", "sobrantes", "Sobrantes", "novedades", "Con novedad",
        "porcentaje", "% recibido", "observacion", "Observación del cierre"));
    VARIABLES.put(CORREO_SERIAL_MAC, vars(
        "cliente", "Cliente", "usuario", "Quien regularizó", "fecha_novedad", "Fecha novedad",
        "fecha_ingreso", "Fecha ingreso", "fecha_cambio", "Fecha cambio", "serial_id", "SerialId",
        "serial_original", "Serial original", "mac_original", "MAC original", "serial_nuevo", "Serial regularizado",
        "mac_nuevo", "MAC regularizada", "modelo", "Equipo", "tipo_origen", "Tipo origen", "origen", "Origen",
        "prealerta", "Prealerta", "ultimo_destino", "Último destino del original",
        "ultimo_destino_fecha", "Fecha del último destino", "primer_destino", "Primer destino del regularizado",
        "movimientos", "Movimientos del SerialId (Excel)"));
  }

  private final ObjectMapper json = new ObjectMapper();

  @PersistenceContext
  private EntityManager em;

  private static List<Map<String, String>> vars(String... pares) {
    List<Map<String, String>> out = new ArrayList<>();
    for (int i = 0; i + 1 < pares.length; i += 2) {
      out.add(Map.of("clave", pares[i], "nombre", pares[i + 1]));
    }
    return out;
  }

  public List<Map<String, String>> tipos() {
    List<Map<String, String>> out = new ArrayList<>();
    TIPOS.forEach((k, v) -> out.add(Map.of("tipo", k, "nombre", v)));
    return out;
  }

  public Map<String, Object> estado(String tipo) {
    exigirTipo(tipo);
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("tipo", tipo);
    out.put("nombre", TIPOS.get(tipo));
    out.put("instalado", tabla());
    out.put("porDefecto", porDefecto(tipo));
    out.put("variables", VARIABLES.get(tipo));
    List<Object[]> r = tabla() ? filas("SELECT Config, Usuario, to_char(Fecha, 'YYYY-MM-DD HH24:MI') FROM " + G
        + "DocumentoPlantilla WHERE ClienteId = ?1 AND Tipo = ?2", cliente(), tipo) : List.of();
    out.put("personalizado", !r.isEmpty());
    out.put("usuario", r.isEmpty() ? "" : texto(r.get(0)[1]));
    out.put("fecha", r.isEmpty() ? "" : texto(r.get(0)[2]));
    out.put("config", r.isEmpty() ? porDefecto(tipo) : mezclar(porDefecto(tipo), leer(texto(r.get(0)[0]))));
    return out;
  }

  public Map<String, Object> config(String tipo) {
    String clave = cliente() + "|" + tipo;
    Cache c = CACHE.get(clave);
    if (c != null && c.vence > System.currentTimeMillis()) {
      return c.config;
    }
    Map<String, Object> base = porDefecto(tipo);
    Map<String, Object> out = base;
    try {
      if (tabla()) {
        List<Object[]> r = filas("SELECT Config FROM " + G + "DocumentoPlantilla WHERE ClienteId = ?1 AND Tipo = ?2 "
            + "AND Activo = true", cliente(), tipo);
        out = r.isEmpty() ? base : mezclar(base, leer(texto(r.get(0)[0])));
      }
    } catch (Exception e) {
      // si la plantilla está dañada imprimo con la de fábrica
      logger.warn("[Documentos] plantilla {} del cliente {} no se pudo leer: {}", tipo, cliente(), e.getMessage());
    }
    CACHE.put(clave, new Cache(out, System.currentTimeMillis() + CACHE_MS));
    return out;
  }

  private record Cache(Map<String, Object> config, long vence) {}

  private static final long CACHE_MS = 30_000;
  private static final Map<String, Cache> CACHE = new java.util.concurrent.ConcurrentHashMap<>();

  public Map<String, Object> conCambios(String tipo, Map<String, Object> cambios) {
    exigirTipo(tipo);
    return cambios == null ? porDefecto(tipo) : mezclar(porDefecto(tipo), cambios);
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(String tipo, Map<String, Object> config, String usuario) {
    exigirTipo(tipo);
    exigirTabla();
    if (config == null) {
      throw new BusinessRuleException("Sin configuración para guardar.");
    }
    validarImagen(config.get("logo"), "logo");
    validarImagen(config.get("icono"), "icono");
    String valor;
    try {
      valor = json.writeValueAsString(config);
    } catch (Exception e) {
      throw new BusinessRuleException("La configuración no es válida.");
    }
    if (valor.length() > MAX_CONFIG) {
      throw new BusinessRuleException("La plantilla es demasiado grande (revise el tamaño de las imágenes).");
    }
    actualizar("INSERT INTO " + G + "DocumentoPlantilla (ClienteId, Tipo, Config, Activo, Usuario, Fecha) "
        + "VALUES (?1, ?2, ?3, true, ?4, LOCALTIMESTAMP) "
        + "ON CONFLICT (ClienteId, Tipo) DO UPDATE SET Config = EXCLUDED.Config, Activo = true, "
        + "Usuario = EXCLUDED.Usuario, Fecha = LOCALTIMESTAMP", cliente(), tipo, valor, corto(usuario, 150));
    CACHE.remove(cliente() + "|" + tipo);
    return estado(tipo);
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> restablecer(String tipo) {
    exigirTipo(tipo);
    exigirTabla();
    actualizar("DELETE FROM " + G + "DocumentoPlantilla WHERE ClienteId = ?1 AND Tipo = ?2", cliente(), tipo);
    CACHE.remove(cliente() + "|" + tipo);
    return estado(tipo);
  }

  public Map<String, Object> porDefecto(String tipo) {
    exigirTipo(tipo);
    try (InputStream in = getClass().getResourceAsStream("/documentos/" + tipo + ".json")) {
      if (in == null) {
        throw new IllegalStateException("Falta resources/documentos/" + tipo + ".json");
      }
      return json.readValue(in, new TypeReference<LinkedHashMap<String, Object>>() {
      });
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo leer la plantilla de fábrica " + tipo + ": " + e.getMessage(), e);
    }
  }


  @SuppressWarnings("unchecked")
  static Map<String, Object> mezclar(Map<String, Object> base, Map<String, Object> cambios) {
    Map<String, Object> out = new LinkedHashMap<>(base);
    for (Map.Entry<String, Object> e : cambios.entrySet()) {
      Object actual = out.get(e.getKey());
      if (actual instanceof Map<?, ?> a && e.getValue() instanceof Map<?, ?> b) {
        out.put(e.getKey(), mezclar((Map<String, Object>) a, (Map<String, Object>) b));
      } else if (e.getValue() != null) {
        out.put(e.getKey(), e.getValue());
      }
    }
    return out;
  }

  private Map<String, Object> leer(String valor) {
    try {
      return json.readValue(valor, new TypeReference<LinkedHashMap<String, Object>>() {
      });
    } catch (Exception e) {
      throw new IllegalStateException("Plantilla guardada no válida: " + e.getMessage(), e);
    }
  }

  private static void validarImagen(Object valor, String campo) {
    String v = valor == null ? "" : String.valueOf(valor);
    if (v.isEmpty()) {
      return;
    }
    if (!v.matches("^data:image/(png|jpeg);base64,[A-Za-z0-9+/=]+$")) {
      throw new BusinessRuleException("El " + campo + " debe ser una imagen PNG o JPG.");
    }
    if (v.length() > MAX_IMAGEN) {
      throw new BusinessRuleException("El " + campo + " pesa demasiado (máximo unos 450 KB).");
    }
  }

  private static void exigirTipo(String tipo) {
    if (!TIPOS.containsKey(tipo)) {
      throw new BusinessRuleException("Documento no válido: " + tipo);
    }
  }

  private boolean tabla() {
    Integer n = entero("SELECT CASE WHEN to_regclass('general.DocumentoPlantilla') IS NULL THEN 0 ELSE 1 END");
    return n != null && n == 1;
  }

  private void exigirTabla() {
    if (!tabla()) {
      throw new BusinessRuleException("Documentos aún no está instalado (falta el script 2026-10-26_documentos_prealertas.sql).");
    }
  }

  private int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    return id == null ? 0 : id;
  }

  private Query consulta(String sql, Object... params) {
    Query q = em.createNativeQuery(sql);
    for (int i = 0; i < params.length; i++) {
      q.setParameter(i + 1, params[i]);
    }
    return q;
  }

  private int actualizar(String sql, Object... params) {
    return consulta(sql, params).executeUpdate();
  }

  private List<Object[]> filas(String sql, Object... params) {
    List<?> rs = consulta(sql, params).getResultList();
    List<Object[]> out = new ArrayList<>(rs.size());
    for (Object f : rs) {
      out.add(f instanceof Object[] arr ? arr : new Object[] { f });
    }
    return out;
  }

  private Integer entero(String sql, Object... params) {
    List<?> rs = consulta(sql, params).getResultList();
    if (rs.isEmpty() || rs.get(0) == null) {
      return null;
    }
    Object o = rs.get(0);
    return o instanceof Number n ? n.intValue() : Integer.valueOf(String.valueOf(o).trim());
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
