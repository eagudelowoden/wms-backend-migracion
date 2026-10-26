package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.util.PlantillaTexto;

import io.pebbletemplates.pebble.PebbleEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Cierre de prealertas con resultado:
 *   - automático: cuando Ingreso completa el 100 % y el usuario la finaliza (PUT /updatePrealerta, igual que siempre);
 *     después de ese cierre se registra y se avisa por correo (si algo falla aquí el cierre ya quedó hecho).
 *   - manual: botón "Cerrar prealerta" de Ingreso, solo con el interruptor PREALERTA_CIERRE_MANUAL del cliente.
 * Resultado: esperados (seriales de la prealerta), recibidos, faltantes (de la prealerta sin ingreso), sobrantes
 * (ingresados con la prealerta pero que no estaban en ella: Cruce = 0, como los marca el trigger de Ingreso) y series
 * que siguen en novedad. Sobrantes también se marcan desde Ingreso sin prealerta (interruptor PREALERTA_SOBRANTES):
 * se elige la prealerta de la que vino el equipo y se guarda con su Id y Cruce = 0.
 * Correo con la plantilla de Administración > Documentos > Correo de prealerta y el Excel con los listados.
 */
@Service
public class PrealertaCierreService {

  private static final Logger logger = LoggerFactory.getLogger(PrealertaCierreService.class);
  private static final String G = "WmsWdGeneral.dbo.";
  public static final String CIERRE_MANUAL = "PREALERTA_CIERRE_MANUAL";
  public static final String SOBRANTES = "PREALERTA_SOBRANTES";
  private static final String COMPLETADA = "COMPLETADA";
  /** filas de cada listado que devuelve el resumen para el aviso de Ingreso */
  private static final int FILAS_RESUMEN = 200;

  /** logo = el de la plantilla (data URI) o vacío para el de Woden */
  public record Correo(String asunto, String html, Map<String, byte[]> adjuntos, String logo) {}

  /** una fila de los listados: serial, código SAP, descripción, detalle, fecha */
  private record Linea(String serial, String codigoSap, String descripcion, String detalle, String fecha) {}

  private static class Resultado {
    int id;
    String nombre = "";
    String guia = "";
    String estado = "";
    String creada = "";
    String tipoOrigen = "";
    String origen = "";
    int esperados;
    List<Linea> faltantes = new ArrayList<>();
    List<Linea> sobrantes = new ArrayList<>();
    List<Linea> novedades = new ArrayList<>();

    int recibidos() {
      return Math.max(0, esperados - faltantes.size());
    }

    String porcentaje() {
      double p = esperados == 0 ? 100 : recibidos() * 100.0 / esperados;
      return String.format(Locale.US, "%.1f", p).replace(".0", "");
    }
  }

  private final PebbleEngine pebble = new PebbleEngine.Builder().build();

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private NotificacionCorreoService notificaciones;

  @Autowired
  private DocumentoPlantillaService plantillas;

  // ── consultas para Ingreso ────────────────────────────────────────────────

  public boolean interruptor(String codigo) {
    Integer n = entero("SELECT COUNT(*) FROM " + G + "ClienteValidacion cv JOIN " + G + "ClienteValidacionTipo t "
        + "ON t.Id = cv.ValidacionTipoId WHERE cv.ClienteId = ?1 AND t.Codigo = ?2 AND cv.Activo = 1 AND t.Activo = 1",
        cliente(), codigo);
    return n != null && n > 0;
  }

  /** Resultado actual de la prealerta (para confirmar el cierre manual). */
  public Map<String, Object> resumen(int id) {
    Resultado r = calcular(id, null);
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("id", r.id);
    out.put("nombre", r.nombre);
    out.put("estado", r.estado);
    out.put("esperados", r.esperados);
    out.put("recibidos", r.recibidos());
    out.put("faltantes", r.faltantes.size());
    out.put("sobrantes", r.sobrantes.size());
    out.put("novedades", r.novedades.size());
    out.put("porcentaje", r.porcentaje());
    out.put("listaFaltantes", r.faltantes.stream().limit(FILAS_RESUMEN).map(PrealertaCierreService::fila)
        .collect(Collectors.toList()));
    return out;
  }

  /** Prealertas que se pueden elegir como origen de un sobrante (abiertas o cerradas hace poco). */
  public List<Map<String, Object>> paraSobrante() {
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] f : filas("SELECT TOP 300 p.Id, p.Nombre, ISNULL(p.Guia, ''), ISNULL(p.Estado, ''), "
        + "CONVERT(varchar(10), p.Fecha, 23) FROM Prealerta p WHERE p.Estado IN ('CARGADO', 'RECOGIDO') "
        + "OR (p.Estado = 'COMPLETADA' AND p.Fecha >= DATEADD(day, -60, GETDATE())) ORDER BY p.Id DESC")) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(f[0]));
      m.put("nombre", texto(f[1]));
      m.put("guia", texto(f[2]));
      m.put("estado", texto(f[3]));
      m.put("fecha", texto(f[4]));
      out.add(m);
    }
    return out;
  }

  /** Guía, documento (el pedido más común de la prealerta), tipo de origen y origen para llenar Ingreso. */
  public Map<String, Object> datosSobrante(int id) {
    List<Object[]> p = filas("SELECT p.Id, p.Nombre, ISNULL(p.Guia, ''), ISNULL(m.Codigo, ''), ISNULL(ma.Codigo, ''), "
        + "ISNULL(p.tipologiaId, 0) FROM Prealerta p LEFT JOIN Maestro m ON m.Id = p.TipoOrigenId "
        + "LEFT JOIN Maestro ma ON ma.Id = p.OrigenId WHERE p.Id = ?1", id);
    if (p.isEmpty()) {
      throw new BusinessRuleException("La prealerta no existe.");
    }
    List<Object[]> doc = filas("SELECT TOP 1 LTRIM(RTRIM(Pedido)) FROM PrealertaSerial WHERE PrealertaId = ?1 "
        + "AND LTRIM(RTRIM(ISNULL(Pedido, ''))) NOT IN ('', '0', '_') GROUP BY LTRIM(RTRIM(Pedido)) "
        + "ORDER BY COUNT(*) DESC", id);
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("id", num(p.get(0)[0]));
    out.put("nombre", texto(p.get(0)[1]));
    out.put("guia", texto(p.get(0)[2]));
    out.put("tipoOrigen", texto(p.get(0)[3]));
    out.put("origen", texto(p.get(0)[4]));
    out.put("tipologiaId", num(p.get(0)[5]));
    out.put("documento", doc.isEmpty() ? "" : texto(doc.get(0)[0]));
    return out;
  }

  // ── cierres ───────────────────────────────────────────────────────────────

  /** Cierre manual desde Ingreso (interruptor PREALERTA_CIERRE_MANUAL). */
  public Map<String, Object> cerrarManual(int id, String usuario, String observacion) {
    if (!interruptor(CIERRE_MANUAL)) {
      throw new BusinessRuleException("El cliente no tiene activo el cierre manual de prealertas (" + CIERRE_MANUAL + ").");
    }
    String quien = usuario == null || usuario.isBlank() ? usuarioSesion() : usuario.trim();
    String obs = observacion == null ? "" : observacion.trim();
    Resultado r = calcular(id, null);
    if (COMPLETADA.equalsIgnoreCase(r.estado)) {
      throw new BusinessRuleException("La prealerta " + r.nombre + " ya estaba cerrada.");
    }
    new TransactionTemplate(transactionManager).executeWithoutResult(s -> {
      int n = actualizar("UPDATE Prealerta SET Estado = 'COMPLETADA' WHERE Id = ?1 AND ISNULL(Estado, '') <> 'COMPLETADA'", id);
      if (n != 1) {
        throw new BusinessRuleException("La prealerta " + r.nombre + " ya estaba cerrada.");
      }
      registrar(r, "MANUAL", quien, obs);
    });
    avisar(r, true, quien, obs);
    Map<String, Object> out = resumen(id);
    out.put("cerrada", true);
    return out;
  }

  /** Estado antes del cierre automático (para saber si de verdad la cerró este pedido). */
  public String estadoActual(int id) {
    try {
      List<Object[]> r = filas("SELECT ISNULL(Estado, '') FROM Prealerta WHERE Id = ?1", id);
      return r.isEmpty() ? "" : texto(r.get(0)[0]);
    } catch (Exception e) {
      return "";
    }
  }

  /** Después del cierre automático de siempre: registro y correo. Nunca lanza error (el cierre ya quedó hecho). */
  public void cierreAutomatico(int id, String estadoAnterior) {
    try {
      if (COMPLETADA.equalsIgnoreCase(estadoAnterior) || !COMPLETADA.equalsIgnoreCase(estadoActual(id))) {
        return;
      }
      Resultado r = calcular(id, estadoAnterior);
      String quien = usuarioSesion();
      new TransactionTemplate(transactionManager).executeWithoutResult(s -> registrar(r, "AUTO", quien, ""));
      avisar(r, false, quien, "");
    } catch (Exception e) {
      logger.warn("[Prealerta] cierre automatico {}: no se registro / aviso: {}", id, e.getMessage());
    }
  }

  private void registrar(Resultado r, String modo, String usuario, String observacion) {
    if (!tablaCierre()) {
      return;
    }
    actualizar("INSERT INTO " + G + "PrealertaCierre (ClienteId, PrealertaId, Prealerta, Modo, Esperados, Recibidos, "
        + "Faltantes, Sobrantes, Novedades, Observacion, Usuario, Fecha) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, "
        + "NULLIF(?10, ''), ?11, GETDATE())", cliente(), r.id, corto(r.nombre, 50), modo, r.esperados, r.recibidos(),
        r.faltantes.size(), r.sobrantes.size(), r.novedades.size(), corto(observacion, 500), corto(usuario, 150));
  }

  private void avisar(Resultado r, boolean manual, String usuario, String observacion) {
    String evento = manual ? NotificacionCorreoService.EVENTO_PREALERTA_MANUAL : NotificacionCorreoService.EVENTO_PREALERTA_AUTO;
    try {
      if (!notificaciones.hayDestinatarios(evento)) {
        return;
      }
      String cerrada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      Correo c = correo(r, manual, usuario, observacion, cerrada, plantillas.config(DocumentoPlantillaService.CORREO_PREALERTA));
      notificaciones.enviarEvento(evento, corto(r.nombre, 100), c.asunto(), c.html(), c.adjuntos(), usuario, c.logo());
    } catch (Exception e) {
      logger.warn("[Prealerta] {} no se pudo armar el correo: {}", r.nombre, e.getMessage());
    }
  }

  // ── resultado de la prealerta ─────────────────────────────────────────────

  /** estadoFiltro = estado con el que se cuentan los esperados (RECOGIDO cuenta solo los recogidos). */
  private Resultado calcular(int id, String estadoFiltro) {
    List<Object[]> p = filas("SELECT p.Id, p.Nombre, ISNULL(p.Guia, ''), ISNULL(p.Estado, ''), "
        + "CONVERT(varchar(16), p.Fecha, 120), ISNULL(tor.Codigo, ''), ISNULL(o.Codigo, '') FROM Prealerta p "
        + "LEFT JOIN Maestro tor ON tor.Id = p.TipoOrigenId LEFT JOIN Maestro o ON o.Id = p.OrigenId WHERE p.Id = ?1", id);
    if (p.isEmpty()) {
      throw new BusinessRuleException("La prealerta no existe.");
    }
    Resultado r = new Resultado();
    Object[] f = p.get(0);
    r.id = num(f[0]);
    r.nombre = texto(f[1]);
    r.guia = texto(f[2]);
    r.estado = texto(f[3]);
    r.creada = texto(f[4]);
    r.tipoOrigen = texto(f[5]);
    r.origen = texto(f[6]);
    boolean recogida = "RECOGIDO".equalsIgnoreCase(estadoFiltro == null ? r.estado : estadoFiltro);

    // esperados: seriales (serializables) de la prealerta, como el contador de Ingreso
    List<Object[]> esperados = filas("SELECT UPPER(LTRIM(RTRIM(s.Serial))), UPPER(LTRIM(RTRIM(ISNULL(s.Mac, '')))), "
        + "ISNULL(s.CodigoSap, ''), ISNULL(s.Descripcion, ''), ISNULL(s.Pedido, ''), ISNULL(s.Novedad, '') "
        + "FROM PrealertaSerial s WHERE s.PrealertaId = ?1 AND ISNULL(s.tipo, 'Serializable') = 'Serializable'"
        + (recogida ? " AND s.recogida = 1" : "") + " ORDER BY s.Id", id);
    r.esperados = esperados.size();

    // ingresados con la prealerta (Cruce = 1 estaba en ella; Cruce = 0 no estaba: sobrante)
    Set<String> cruzados = new HashSet<>();
    for (Object[] i : filas("SELECT UPPER(LTRIM(RTRIM(i.Serial))), UPPER(LTRIM(RTRIM(ISNULL(i.Mac, '')))), "
        + "CAST(ISNULL(i.Cruce, 0) AS INT), ISNULL(e.Nombre, ''), ISNULL(i.Novedad, ''), ISNULL(cs.Codigo, ''), "
        + "ISNULL(cs.Descripcion, ''), CONVERT(varchar(16), i.Fecha, 120) FROM Ingreso i "
        + "LEFT JOIN Estado e ON e.Id = i.EstadoId LEFT JOIN CodigoSap cs ON cs.Id = i.CodigoSapId "
        + "WHERE i.PrealertaId = ?1 ORDER BY i.Fecha", id)) {
      String serial = texto(i[0]);
      String estado = texto(i[3]);
      String novedad = texto(i[4]);
      if (num(i[2]) == 1) {
        cruzados.add(serial);
        if (!texto(i[1]).isEmpty()) {
          cruzados.add(texto(i[1]));
        }
      } else {
        r.sobrantes.add(new Linea(serial, texto(i[5]), texto(i[6]), estado, texto(i[7])));
      }
      if (estado.equals("NOVEDAD") || estado.equals("P-NOVEDAD")) {
        String detalle = novedad.isEmpty() || novedad.equals("0") ? estado : novedad;
        r.novedades.add(new Linea(serial, texto(i[5]), texto(i[6]), detalle, texto(i[7])));
      }
    }

    List<Object[]> pendientes = new ArrayList<>();
    for (Object[] s : esperados) {
      String serial = texto(s[0]);
      String mac = texto(s[1]);
      if (!cruzados.contains(serial) && (mac.isEmpty() || !cruzados.contains(mac))) {
        pendientes.add(s);
      }
    }
    // los que ya salieron de Ingreso (despachados) cuentan como recibidos: se buscan por serial en Despacho
    Set<String> despachados = despachados(id, pendientes);
    for (Object[] s : pendientes) {
      if (!despachados.contains(texto(s[0]))) {
        String novedad = texto(s[5]);
        r.faltantes.add(new Linea(texto(s[0]), texto(s[2]), texto(s[3]), texto(s[4]),
            novedad.isEmpty() ? "" : novedad));
      }
    }
    return r;
  }

  private Set<String> despachados(int id, List<Object[]> pendientes) {
    Set<String> out = new HashSet<>();
    if (pendientes.isEmpty()) {
      return out;
    }
    try {
      Integer cols = entero("SELECT CASE WHEN COL_LENGTH('Despacho', 'PrealertaId') IS NULL "
          + "OR COL_LENGTH('Despacho', 'Cruce') IS NULL THEN 0 ELSE 1 END");
      if (cols == null || cols == 0) {
        return out;
      }
      List<String> seriales = pendientes.stream().map(s -> texto(s[0])).filter(s -> !s.isEmpty()).distinct()
          .collect(Collectors.toList());
      for (int i = 0; i < seriales.size(); i += 500) {
        List<String> bloque = seriales.subList(i, Math.min(seriales.size(), i + 500));
        StringBuilder in = new StringBuilder();
        for (int k = 0; k < bloque.size(); k++) {
          in.append(k == 0 ? "" : ",").append("?").append(k + 2);
        }
        Object[] params = new Object[bloque.size() + 1];
        params[0] = id;
        for (int k = 0; k < bloque.size(); k++) {
          params[k + 1] = bloque.get(k);
        }
        for (Object[] d : filas("SELECT UPPER(LTRIM(RTRIM(Serial))) FROM Despacho WHERE Serial IN (" + in
            + ") AND PrealertaId = ?1 AND Cruce = 1", params)) {
          out.add(texto(d[0]));
        }
      }
    } catch (Exception e) {
      logger.warn("[Prealerta] {}: no se pudo revisar Despacho: {}", id, e.getMessage());
    }
    return out;
  }

  // ── correo ────────────────────────────────────────────────────────────────

  /** Correo de ejemplo (prueba de la regla en Notificaciones y vista previa de Documentos). */
  public Correo ejemplo(boolean manual, String usuario) {
    return ejemplo(manual, usuario, plantillas.config(DocumentoPlantillaService.CORREO_PREALERTA));
  }

  public Correo ejemplo(boolean manual, String usuario, Map<String, Object> cfg) {
    Resultado r = new Resultado();
    r.id = 0;
    r.nombre = "PREALERTA-EJEMPLO";
    r.guia = "GUIA-123456";
    r.estado = "CARGADO";
    r.creada = "2026-10-24 08:30";
    r.tipoOrigen = "TIPO ORIGEN";
    r.origen = "ORIGEN";
    r.esperados = 120;
    for (int i = 1; i <= (manual ? 6 : 0); i++) {
      r.faltantes.add(new Linea("FALTA00" + i, "HME0004010", "EMTA DOCSIS3.0 TG2482", "PED-778" + i, "SIN NOVEDAD"));
    }
    r.sobrantes.add(new Linea("SOBRA001", "TMS0003135", "DECODIFICADOR HD DCX525", "NOVEDAD", "2026-10-25 09:12"));
    r.sobrantes.add(new Linea("SOBRA002", "HME0004010", "EMTA DOCSIS3.0 TG2482", "ALMACENAMIENTO", "2026-10-25 09:40"));
    r.novedades.add(new Linea("SOBRA001", "TMS0003135", "DECODIFICADOR HD DCX525", "NO EXISTE PREALERTA", "2026-10-25 09:12"));
    return correo(r, manual, usuario == null ? "" : usuario, manual ? "Faltan equipos por recoger" : "",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), cfg);
  }

  @SuppressWarnings("unchecked")
  private Correo correo(Resultado r, boolean manual, String usuario, String observacion, String cerrada,
      Map<String, Object> cfg) {
    Map<String, String> vars = new HashMap<>();
    vars.put("cliente", texto(ClientDatabaseContext.getCurrentClientName()));
    vars.put("prealerta", r.nombre);
    vars.put("guia", r.guia);
    vars.put("tipo_origen", r.tipoOrigen);
    vars.put("origen", r.origen);
    vars.put("creada", r.creada);
    vars.put("cerrada", cerrada);
    vars.put("usuario", usuario);
    vars.put("modo", manual ? "manualmente" : "automáticamente");
    vars.put("tipo_cierre", manual ? "Manual" : "Automático");
    vars.put("esperados", String.valueOf(r.esperados));
    vars.put("recibidos", String.valueOf(r.recibidos()));
    vars.put("faltantes", String.valueOf(r.faltantes.size()));
    vars.put("sobrantes", String.valueOf(r.sobrantes.size()));
    vars.put("novedades", String.valueOf(r.novedades.size()));
    vars.put("porcentaje", r.porcentaje());
    vars.put("observacion", observacion);

    Map<String, Object> col = cfg.get("colores") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
    Map<String, String> c = new HashMap<>();
    c.put("encabezado", PlantillaTexto.color(col.get("encabezado"), "#f97316"));
    c.put("encabezadoFin", PlantillaTexto.color(col.get("encabezadoFin"), "#ea580c"));
    c.put("acento", PlantillaTexto.color(col.get("acento"), "#ea580c"));
    c.put("suave", PlantillaTexto.color(col.get("suave"), "#fff7ed"));
    c.put("bordeSuave", PlantillaTexto.color(col.get("bordeSuave"), "#fed7aa"));
    c.put("texto", PlantillaTexto.color(col.get("texto"), "#111827"));
    c.put("fondo", PlantillaTexto.color(col.get("fondo"), "#f5f5f5"));

    String asunto = PlantillaTexto.texto(texto(cfg.get("asunto")), vars).replaceAll("\\s{2,}", " ").trim();
    if (asunto.isEmpty()) {
      asunto = "WMS · Prealerta " + r.nombre + " cerrada";
    }
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("c", c);
    ctx.put("asunto", PlantillaTexto.escapar(asunto));
    ctx.put("titulo", PlantillaTexto.html(texto(cfg.get("titulo")), vars));
    ctx.put("subtitulo", PlantillaTexto.html(texto(cfg.get("subtitulo")), vars));
    ctx.put("marca", PlantillaTexto.html(texto(cfg.get("marca")), vars));
    ctx.put("usar_logo", Boolean.TRUE.equals(cfg.get("usarLogo")));
    ctx.put("prealerta", PlantillaTexto.escapar(r.nombre));
    ctx.put("intro", PlantillaTexto.html(texto(cfg.get(manual ? "introManual" : "introAuto")), vars));

    List<Map<String, String>> datos = new ArrayList<>();
    datos.add(dato("Guía", r.guia));
    datos.add(dato("Cierre", (manual ? "Manual" : "Automático") + " · " + cerrada));
    datos.add(dato("Tipo origen", r.tipoOrigen));
    datos.add(dato("Origen", r.origen));
    datos.add(dato("Creada", r.creada));
    datos.add(dato("Cerrada por", usuario.isBlank() ? "—" : usuario));
    if (!observacion.isBlank()) {
      datos.add(dato("Observación", observacion));
    }
    List<List<Map<String, Object>>> info = new ArrayList<>();
    for (int i = 0; i < datos.size(); i += 2) {
      List<Map<String, Object>> par = new ArrayList<>();
      boolean completa = i + 1 >= datos.size();
      for (int k = i; k < Math.min(datos.size(), i + 2); k++) {
        Map<String, Object> d = new HashMap<>(datos.get(k));
        d.put("suave", i == 0);
        d.put("completa", completa);
        par.add(d);
      }
      info.add(par);
    }
    ctx.put("info", info);

    Map<String, Object> sec = cfg.get("secciones") instanceof Map<?, ?> m2 ? (Map<String, Object>) m2 : Map.of();
    List<Map<String, String>> resultado = new ArrayList<>();
    if (!Boolean.FALSE.equals(sec.get("resultado"))) {
      resultado.add(kpi("Esperados", String.valueOf(r.esperados), c.get("texto")));
      resultado.add(kpi("Recibidos", String.valueOf(r.recibidos()), "#15803d"));
      resultado.add(kpi("Faltantes", String.valueOf(r.faltantes.size()), r.faltantes.isEmpty() ? c.get("texto") : "#b91c1c"));
      resultado.add(kpi("Sobrantes", String.valueOf(r.sobrantes.size()), r.sobrantes.isEmpty() ? c.get("texto") : c.get("acento")));
      resultado.add(kpi("Con novedad", String.valueOf(r.novedades.size()), r.novedades.isEmpty() ? c.get("texto") : c.get("acento")));
      resultado.add(kpi("% Recibido", r.porcentaje() + "%", c.get("acento")));
    }
    ctx.put("resultado", resultado);

    int max = (int) PlantillaTexto.numero(cfg.get("maxFilas"), 50, 0, 500);
    List<Map<String, Object>> secciones = new ArrayList<>();
    if (!Boolean.FALSE.equals(sec.get("faltantes"))) {
      secciones.add(seccion("Faltantes", "Todos los equipos de la prealerta fueron recibidos.",
          List.of("Serial", "Código SAP", "Descripción", "Pedido", "Novedad prealerta"), r.faltantes, max));
    }
    if (!Boolean.FALSE.equals(sec.get("sobrantes"))) {
      secciones.add(seccion("Sobrantes", "Sin equipos sobrantes.",
          List.of("Serial", "Código SAP", "Descripción", "Estado actual", "Ingreso"), r.sobrantes, max));
    }
    if (!Boolean.FALSE.equals(sec.get("novedades"))) {
      secciones.add(seccion("Series con novedad de ingreso", "Ninguna serie quedó en novedad.",
          List.of("Serial", "Código SAP", "Descripción", "Novedad", "Ingreso"), r.novedades, max));
    }
    ctx.put("secciones", secciones);

    List<String> pie = new ArrayList<>();
    if (cfg.get("pie") instanceof List<?> lineas) {
      for (Object l : lineas) {
        if (!PlantillaTexto.vacia(texto(l), vars)) {
          pie.add(PlantillaTexto.html(texto(l), vars));
        }
      }
    }
    ctx.put("pie", pie);

    String html;
    try {
      StringWriter w = new StringWriter();
      pebble.getTemplate("mail/prealerta-cierre.html").evaluate(w, ctx);
      html = w.toString();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar el correo de la prealerta: " + e.getMessage(), e);
    }
    Map<String, byte[]> adjuntos = new LinkedHashMap<>();
    if (!Boolean.FALSE.equals(cfg.get("adjuntarExcel"))) {
      String nombre = r.nombre.replaceAll("[^A-Za-z0-9_-]", "");
      adjuntos.put("prealerta-" + (nombre.isEmpty() ? r.id : nombre) + ".xlsx", excel(r, manual, usuario, cerrada, observacion));
    }
    return new Correo(asunto, html, adjuntos, texto(cfg.get("logo")));
  }

  /** HTML del correo con datos de ejemplo y una plantilla dada (vista previa del editor; el logo va embebido). */
  public String vistaPrevia(Map<String, Object> cfg, boolean manual, String usuario) {
    String logo = texto(cfg.get("logo"));
    if (!logo.startsWith("data:image/")) {
      try (java.io.InputStream in = getClass().getResourceAsStream("/mail/logo-woden.png")) {
        logo = in == null ? "" : "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(in.readAllBytes());
      } catch (Exception e) {
        logo = "";
      }
    }
    return ejemplo(manual, usuario, cfg).html().replace("cid:logo", logo);
  }

  private static Map<String, String> dato(String etiqueta, String valor) {
    return Map.of("etiqueta", PlantillaTexto.escapar(etiqueta),
        "valor", PlantillaTexto.escapar(valor == null || valor.isBlank() ? "—" : valor));
  }

  private static Map<String, String> kpi(String etiqueta, String valor, String color) {
    return Map.of("etiqueta", etiqueta, "valor", valor, "color", color);
  }

  private static Map<String, Object> seccion(String titulo, String vacio, List<String> columnas, List<Linea> lineas,
      int max) {
    Map<String, Object> s = new HashMap<>();
    s.put("titulo", titulo);
    s.put("vacio", vacio);
    s.put("columnas", columnas);
    s.put("colspan", columnas.size() + 1);
    s.put("total", lineas.size());
    s.put("filas", lineas.stream().limit(max).map(PrealertaCierreService::fila).collect(Collectors.toList()));
    s.put("restantes", Math.max(0, lineas.size() - max));
    return s;
  }

  private static List<String> fila(Linea l) {
    return List.of(guion(l.serial()), guion(l.codigoSap()), guion(l.descripcion()), guion(l.detalle()), guion(l.fecha()));
  }

  private static String guion(String s) {
    return s == null || s.isBlank() ? "—" : s;
  }

  private byte[] excel(Resultado r, boolean manual, String usuario, String cerrada, String observacion) {
    try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle cab = wb.createCellStyle();
      Font f = wb.createFont();
      f.setBold(true);
      f.setColor(IndexedColors.WHITE.getIndex());
      cab.setFont(f);
      ((XSSFCellStyle) cab).setFillForegroundColor(new XSSFColor(new byte[] { (byte) 0xE4, (byte) 0x7D, (byte) 0x06 }, null));
      cab.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      Sheet res = wb.createSheet("Resumen");
      String[][] datos = {
          { "Prealerta", r.nombre }, { "Guía", r.guia }, { "Tipo origen", r.tipoOrigen }, { "Origen", r.origen },
          { "Creada", r.creada }, { "Cierre", (manual ? "Manual" : "Automático") + " · " + cerrada },
          { "Cerrada por", usuario }, { "Observación", observacion }, { "Esperados", String.valueOf(r.esperados) },
          { "Recibidos", String.valueOf(r.recibidos()) }, { "Faltantes", String.valueOf(r.faltantes.size()) },
          { "Sobrantes", String.valueOf(r.sobrantes.size()) }, { "Con novedad", String.valueOf(r.novedades.size()) },
          { "% Recibido", r.porcentaje() } };
      for (int i = 0; i < datos.length; i++) {
        Row row = res.createRow(i);
        row.createCell(0).setCellValue(datos[i][0]);
        row.getCell(0).setCellStyle(cab);
        row.createCell(1).setCellValue(datos[i][1] == null ? "" : datos[i][1]);
      }
      res.setColumnWidth(0, 20 * 256);
      res.setColumnWidth(1, 50 * 256);

      hoja(wb, cab, "Faltantes", new String[] { "Serial", "Código SAP", "Descripción", "Pedido", "Novedad prealerta" }, r.faltantes);
      hoja(wb, cab, "Sobrantes", new String[] { "Serial", "Código SAP", "Descripción", "Estado actual", "Ingreso" }, r.sobrantes);
      hoja(wb, cab, "Con novedad", new String[] { "Serial", "Código SAP", "Descripción", "Novedad", "Ingreso" }, r.novedades);
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar el Excel de la prealerta: " + e.getMessage(), e);
    }
  }

  private static void hoja(XSSFWorkbook wb, CellStyle cab, String nombre, String[] titulos, List<Linea> lineas) {
    Sheet sh = wb.createSheet(nombre);
    Row r0 = sh.createRow(0);
    for (int i = 0; i < titulos.length; i++) {
      r0.createCell(i).setCellValue(titulos[i]);
      r0.getCell(i).setCellStyle(cab);
    }
    int n = 1;
    for (Linea l : lineas) {
      Row row = sh.createRow(n++);
      row.createCell(0).setCellValue(texto(l.serial()));
      row.createCell(1).setCellValue(texto(l.codigoSap()));
      row.createCell(2).setCellValue(texto(l.descripcion()));
      row.createCell(3).setCellValue(texto(l.detalle()));
      row.createCell(4).setCellValue(texto(l.fecha()));
    }
    if (n == 1) {
      sh.createRow(1).createCell(0).setCellValue("Sin registros");
    }
    sh.createFreezePane(0, 1);
    int[] anchos = { 24, 16, 40, 26, 20 };
    for (int i = 0; i < anchos.length; i++) {
      sh.setColumnWidth(i, anchos[i] * 256);
    }
  }

  // ── utilidades ────────────────────────────────────────────────────────────

  private boolean tablaCierre() {
    Integer n = entero("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.PrealertaCierre', 'U') IS NULL THEN 0 ELSE 1 END");
    return n != null && n == 1;
  }

  private static String usuarioSesion() {
    try {
      Authentication a = SecurityContextHolder.getContext().getAuthentication();
      return a == null || a.getName() == null ? "" : a.getName();
    } catch (Exception e) {
      return "";
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
    return rs.isEmpty() || rs.get(0) == null ? null : num(rs.get(0));
  }

  private static int num(Object o) {
    if (o == null) {
      return 0;
    }
    if (o instanceof Number n) {
      return n.intValue();
    }
    try {
      return Integer.parseInt(String.valueOf(o).trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
