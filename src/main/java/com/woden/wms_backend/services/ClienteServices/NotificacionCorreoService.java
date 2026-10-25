package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.services.WmsWdGeneral.CorreoSmtpService;

import io.pebbletemplates.pebble.PebbleEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Notificaciones por correo del WMS (como ReverseCore): cada regla = evento + cliente (o todos) + Para / CC.
 * Se envia desde la cuenta noreplyservices en segundo plano y cada envio queda en NotificacionCorreoEnvio.
 * Tablas en WmsWdGeneral (sql/2026-10-25_notificaciones_correo.sql).
 *
 * Evento NOVEDAD_ERROR_SERIAL_MAC: al terminar en Novedades > Proceso una novedad ERROR SERIAL-MAC, correo con quien la
 * hizo, fechas, serial original y regularizado, origen, prealerta, destinos y el Excel de movimientos del equipo.
 */
@Service
public class NotificacionCorreoService {

  private static final Logger logger = LoggerFactory.getLogger(NotificacionCorreoService.class);
  private static final String G = "WmsWdGeneral.dbo.";
  public static final String EVENTO_SERIAL_MAC = "NOVEDAD_ERROR_SERIAL_MAC";
  private static final Map<String, String> EVENTOS = new LinkedHashMap<>();
  static {
    EVENTOS.put(EVENTO_SERIAL_MAC, "Novedad ERROR SERIAL-MAC regularizada");
  }
  private static final Pattern CORREO = Pattern.compile("^[^@\\s;,]+@[^@\\s;,]+\\.[^@\\s;,]+$");
  private static final ExecutorService ENVIOS = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "notificaciones-correo");
    t.setDaemon(true);
    return t;
  });

  private final PebbleEngine pebble = new PebbleEngine.Builder().build();

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private CorreoSmtpService correo;

  // ── administracion de reglas ──────────────────────────────────────────────

  public List<Map<String, String>> eventos() {
    return EVENTOS.entrySet().stream().map(e -> Map.of("clave", e.getKey(), "nombre", e.getValue()))
        .collect(Collectors.toList());
  }

  public Map<String, Object> estado() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("instalado", tablas());
    m.put("smtp", correo.configurado());
    m.put("cuenta", correo.configurado() ? correo.cuenta() : "");
    return m;
  }

  public List<Map<String, Object>> reglas() {
    if (!tablas()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] r : filas("SELECT n.Id, n.Evento, n.ClienteId, n.Para, n.Cc, n.Activo, n.Usuario, "
        + "CONVERT(varchar(16), ISNULL(n.FechaModifica, n.Fecha), 120) FROM " + G + "NotificacionCorreo n "
        + "WHERE n.ClienteId = ?1 OR n.ClienteId IS NULL ORDER BY n.Evento, n.Id", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(r[0]));
      m.put("evento", r[1]);
      m.put("eventoNombre", EVENTOS.getOrDefault(texto(r[1]), texto(r[1])));
      m.put("todos", r[2] == null);
      m.put("para", texto(r[3]));
      m.put("cc", texto(r[4]));
      m.put("activo", bool(r[5]));
      m.put("usuario", texto(r[6]));
      m.put("fecha", r[7]);
      out.add(m);
    }
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardar(Map<String, Object> d, String usuario) {
    exigirTablas();
    String evento = texto(d.get("evento"));
    if (!EVENTOS.containsKey(evento)) {
      throw new BusinessRuleException("Evento no válido.");
    }
    String para = String.join("; ", validar(texto(d.get("para")), "Para", true));
    String cc = String.join("; ", validar(texto(d.get("cc")), "CC", false));
    boolean todos = Boolean.TRUE.equals(d.get("todos"));
    boolean activo = !Boolean.FALSE.equals(d.get("activo"));
    int id = num(d.get("id"));
    int cli = todos ? 0 : cliente();
    if (id > 0) {
      int n = actualizar("UPDATE " + G + "NotificacionCorreo SET Evento = ?1, ClienteId = NULLIF(?2, 0), Para = ?3, "
          + "Cc = NULLIF(?4, ''), Activo = ?5, Usuario = ?6, FechaModifica = GETDATE() "
          + "WHERE Id = ?7 AND (ClienteId = ?8 OR ClienteId IS NULL)",
          evento, cli, para, cc, activo, corto(usuario, 150), id, cliente());
      if (n != 1) {
        throw new BusinessRuleException("La notificación no existe.");
      }
    } else {
      id = num(consulta("INSERT INTO " + G + "NotificacionCorreo (Evento, ClienteId, Para, Cc, Activo, Usuario, Fecha) "
          + "OUTPUT inserted.Id VALUES (?1, NULLIF(?2, 0), ?3, NULLIF(?4, ''), ?5, ?6, GETDATE())",
          evento, cli, para, cc, activo, corto(usuario, 150)).getSingleResult());
    }
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("id", id);
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public void eliminar(int id) {
    exigirTablas();
    actualizar("DELETE FROM " + G + "NotificacionCorreo WHERE Id = ?1 AND (ClienteId = ?2 OR ClienteId IS NULL)", id, cliente());
  }

  public List<Map<String, Object>> envios() {
    if (!tablas()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] r : filas("SELECT TOP 100 e.Id, e.Evento, e.Referencia, e.Destinatarios, e.Asunto, e.Ok, e.Error, "
        + "e.Usuario, CONVERT(varchar(19), e.Fecha, 120) FROM " + G + "NotificacionCorreoEnvio e "
        + "WHERE e.ClienteId = ?1 OR e.ClienteId IS NULL ORDER BY e.Id DESC", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(r[0]));
      m.put("evento", EVENTOS.getOrDefault(texto(r[1]), texto(r[1])));
      m.put("referencia", r[2]);
      m.put("destinatarios", r[3]);
      m.put("asunto", r[4]);
      m.put("ok", bool(r[5]));
      m.put("error", r[6]);
      m.put("usuario", r[7]);
      m.put("fecha", r[8]);
      out.add(m);
    }
    return out;
  }

  /** Correo de prueba de la regla, con datos de ejemplo, a sus destinatarios. */
  public Map<String, Object> probar(int id, String usuario) {
    exigirTablas();
    List<Object[]> r = filas("SELECT Evento, Para, Cc FROM " + G + "NotificacionCorreo WHERE Id = ?1 "
        + "AND (ClienteId = ?2 OR ClienteId IS NULL)", id, cliente());
    if (r.isEmpty()) {
      throw new BusinessRuleException("La notificación no existe.");
    }
    if (!correo.configurado()) {
      throw new BusinessRuleException("El servidor no tiene configurado el SMTP (SMTP_SERVER, SMTP_USER, SMTP_PASSWORD).");
    }
    List<String> para = correos(texto(r.get(0)[1]));
    List<String> cc = correos(texto(r.get(0)[2]));
    Map<String, String> ejemplo = new LinkedHashMap<>();
    ejemplo.put("usuario", usuario);
    ejemplo.put("fechaNovedad", "2026-10-24 09:15:00");
    ejemplo.put("fechaIngreso", "2026-10-20 14:02:11");
    ejemplo.put("fechaCambio", "2026-10-25 10:30:00");
    ejemplo.put("serialId", "0");
    ejemplo.put("serialOriginal", "SERIAL-ORIGINAL");
    ejemplo.put("macOriginal", "MAC-ORIGINAL");
    ejemplo.put("serialNuevo", "SERIAL-REGULARIZADO");
    ejemplo.put("macNuevo", "MAC-REGULARIZADA");
    ejemplo.put("modelo", "Equipo de ejemplo");
    ejemplo.put("tipoOrigen", "TIPO ORIGEN");
    ejemplo.put("origen", "ORIGEN");
    ejemplo.put("prealerta", "PREALERTA");
    ejemplo.put("ultimoDestinoOriginal", "NOVEDAD · 2026-10-24 09:15");
    ejemplo.put("primerDestinoNuevo", "LIMPIEZA");
    String asunto = "[PRUEBA] " + asuntoSerialMac(ejemplo);
    String html = htmlSerialMac(asunto, ejemplo, 0);
    byte[] xlsx = excelMovimientos(List.of());
    String error = null;
    try {
      correo.enviar(para, cc, asunto, html, Map.of("movimientos-ejemplo.xlsx", xlsx));
    } catch (Exception e) {
      error = causa(e);
    }
    registrar(EVENTO_SERIAL_MAC + "_PRUEBA", cliente(), "regla " + id, para, cc, asunto, error == null, error, usuario);
    if (error != null) {
      throw new BusinessRuleException("No se pudo enviar el correo de prueba: " + error);
    }
    return Map.of("ok", true, "destinatarios", String.join("; ", para));
  }

  // ── evento: novedad ERROR SERIAL-MAC ──────────────────────────────────────

  /** Foto del equipo ANTES de cambiarle el serial (null si no se pudo tomar: la novedad sigue igual). */
  public Map<String, String> fotoSerialMac(Integer ingresoId) {
    if (ingresoId == null || !tablas()) {
      return null;
    }
    try {
      List<Object[]> r = filas("SELECT i.Id, i.Serial, ISNULL(i.Mac, ''), CONVERT(varchar(19), i.Fecha, 120), i.EstadoId, "
          + "e.Nombre, cs.Codigo, cs.Descripcion, COALESCE(NULLIF(tor.Codigo, ''), tor.Descripcion), "
          + "COALESCE(NULLIF(o.Codigo, ''), o.Descripcion), pr.Nombre "
          + "FROM Ingreso i LEFT JOIN Estado e ON e.Id = i.EstadoId LEFT JOIN CodigoSap cs ON cs.Id = i.CodigoSapId "
          + "LEFT JOIN Maestro tor ON tor.Id = i.TipoOrigenId LEFT JOIN Maestro o ON o.Id = i.OrigenId "
          + "LEFT JOIN Prealerta pr ON pr.Id = i.PrealertaId WHERE i.Id = ?1", ingresoId);
      if (r.isEmpty()) {
        return null;
      }
      Object[] f = r.get(0);
      Map<String, String> m = new LinkedHashMap<>();
      m.put("serialId", String.valueOf(num(f[0])));
      m.put("serialOriginal", texto(f[1]));
      m.put("macOriginal", texto(f[2]));
      m.put("fechaIngreso", texto(f[3]));
      m.put("modelo", (texto(f[6]) + " · " + texto(f[7])).replaceAll("^ · | · $", ""));
      m.put("tipoOrigen", texto(f[8]));
      m.put("origen", texto(f[9]));
      m.put("prealerta", texto(f[10]).isEmpty() ? "Sin prealerta" : texto(f[10]));
      // cuando entro a la novedad (ultimo movimiento hacia su estado actual)
      List<Object[]> nov = filas("SELECT TOP 1 CONVERT(varchar(19), m.Fecha, 120) FROM Movimiento m "
          + "WHERE m.SerialId = ?1 AND m.DestinoId = ?2 ORDER BY m.Id DESC", ingresoId, num(f[4]));
      m.put("fechaNovedad", nov.isEmpty() ? "" : texto(nov.get(0)[0]));
      // ultimo destino del serial original (por el serial, en cualquier vuelta)
      List<Object[]> ult = filas("SELECT TOP 1 e.Nombre, CONVERT(varchar(16), m.Fecha, 120) FROM Movimiento m "
          + "LEFT JOIN Estado e ON e.Id = m.DestinoId WHERE m.Serial = ?1 ORDER BY m.Id DESC", texto(f[1]));
      m.put("ultimoDestinoOriginal", ult.isEmpty() ? texto(f[5])
          : texto(ult.get(0)[0]) + " · " + texto(ult.get(0)[1]));
      return m;
    } catch (Exception e) {
      logger.warn("[Notificacion] no se pudo tomar la foto del serial {}: {}", ingresoId, causa(e));
      return null;
    }
  }

  /** Despues del cambio: arma el correo y lo envia en segundo plano a los destinatarios de la regla. */
  public void notificarSerialMac(Map<String, String> foto, Integer usuarioId) {
    if (foto == null) {
      return;
    }
    try {
      int cli = cliente();
      Destinos dest = destinatarios(EVENTO_SERIAL_MAC, cli);
      if (dest.para.isEmpty()) {
        return;
      }
      int serialId = num(foto.get("serialId"));
      Object[] nuevo = filas("SELECT i.Serial, ISNULL(i.Mac, ''), e.Nombre, CONVERT(varchar(19), GETDATE(), 120) "
          + "FROM Ingreso i LEFT JOIN Estado e ON e.Id = i.EstadoId WHERE i.Id = ?1", serialId).get(0);
      Map<String, String> datos = new LinkedHashMap<>(foto);
      datos.put("serialNuevo", texto(nuevo[0]));
      datos.put("macNuevo", texto(nuevo[1]));
      datos.put("primerDestinoNuevo", texto(nuevo[2]));
      datos.put("fechaCambio", texto(nuevo[3]));
      datos.put("usuario", nombreUsuario(usuarioId));

      List<Object[]> movs = filas("SELECT CONVERT(varchar(19), m.Fecha, 120), m.SerialId, m.Serial, eo.Nombre, ed.Nombre, "
          + "m.Descripcion, u.NombreUsuario FROM Movimiento m LEFT JOIN Estado eo ON eo.Id = m.OrigenId "
          + "LEFT JOIN Estado ed ON ed.Id = m.DestinoId LEFT JOIN " + G + "Usuario u ON u.Id = m.UsuarioId "
          + "WHERE m.SerialId = ?1 OR m.Serial IN (?2, ?3) ORDER BY m.Fecha, m.Id",
          serialId, datos.get("serialOriginal"), datos.get("serialNuevo"));

      String asunto = asuntoSerialMac(datos);
      String html = htmlSerialMac(asunto, datos, movs.size());
      byte[] xlsx = excelMovimientos(movs);
      String adjunto = "movimientos-" + datos.get("serialNuevo").replaceAll("[^A-Za-z0-9_-]", "") + ".xlsx";
      String referencia = datos.get("serialOriginal") + " -> " + datos.get("serialNuevo");
      String usuario = datos.get("usuario");

      // el envio y su registro van en otro hilo: se le pasa el cliente de la sesion
      String nombre = ClientDatabaseContext.getCurrentClientName();
      String db = ClientDatabaseContext.getCurrentClientDb();
      ENVIOS.submit(() -> {
        try {
          ClientDatabaseContext.setCurrentClient(nombre, db, cli);
          String error = null;
          try {
            correo.enviar(dest.para, dest.cc, asunto, html, Map.of(adjunto, xlsx));
          } catch (Exception e) {
            error = causa(e);
            logger.warn("[Notificacion] {} no se envio: {}", referencia, error);
          }
          registrar(EVENTO_SERIAL_MAC, cli, referencia, dest.para, dest.cc, asunto, error == null, error, usuario);
        } catch (Exception e) {
          logger.warn("[Notificacion] {} no se registro: {}", referencia, causa(e));
        } finally {
          ClientDatabaseContext.clear();
        }
      });
    } catch (Exception e) {
      logger.warn("[Notificacion] no se pudo armar la notificacion ERROR SERIAL-MAC: {}", causa(e));
    }
  }

  private String asuntoSerialMac(Map<String, String> d) {
    String cli = texto(ClientDatabaseContext.getCurrentClientName());
    return "WMS " + (cli.isEmpty() ? "" : cli + " ") + "· Novedad ERROR SERIAL-MAC: " + d.get("serialOriginal") + " → "
        + d.get("serialNuevo");
  }

  private String htmlSerialMac(String asunto, Map<String, String> d, int movimientos) {
    List<Map<String, Object>> filas = new ArrayList<>();
    fila(filas, "Usuario", d.get("usuario"), false);
    fila(filas, "Fecha de la novedad", d.get("fechaNovedad"), false);
    fila(filas, "Fecha de ingreso del equipo", d.get("fechaIngreso"), false);
    fila(filas, "Fecha del cambio", d.get("fechaCambio"), false);
    fila(filas, "SerialId", d.get("serialId"), false);
    fila(filas, "Serial original", d.get("serialOriginal"), false);
    fila(filas, "MAC original", d.get("macOriginal"), false);
    fila(filas, "Serial regularizado", d.get("serialNuevo"), true);
    fila(filas, "MAC regularizada", d.get("macNuevo"), true);
    fila(filas, "Código SAP / modelo", d.get("modelo"), false);
    fila(filas, "Tipo de origen", d.get("tipoOrigen"), false);
    fila(filas, "Origen", d.get("origen"), false);
    fila(filas, "Prealerta", d.get("prealerta"), false);
    fila(filas, "Último destino del serial original", d.get("ultimoDestinoOriginal"), false);
    fila(filas, "Primer destino del serial regularizado", d.get("primerDestinoNuevo"), true);
    Map<String, Object> ctx = new LinkedHashMap<>();
    ctx.put("asunto", asunto);
    ctx.put("titulo", EVENTOS.get(EVENTO_SERIAL_MAC));
    ctx.put("cliente", texto(ClientDatabaseContext.getCurrentClientName()));
    ctx.put("filas", filas);
    ctx.put("movimientos", movimientos);
    try {
      StringWriter w = new StringWriter();
      pebble.getTemplate("mail/novedad-serial-mac.html").evaluate(w, ctx);
      return w.toString();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar la plantilla del correo: " + causa(e), e);
    }
  }

  private static void fila(List<Map<String, Object>> filas, String etiqueta, String valor, boolean resaltar) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("etiqueta", etiqueta);
    m.put("valor", valor == null || valor.isBlank() ? "—" : valor);
    m.put("resaltar", resaltar);
    filas.add(m);
  }

  private byte[] excelMovimientos(List<Object[]> movs) {
    try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sh = wb.createSheet("Movimientos");
      CellStyle cab = wb.createCellStyle();
      Font f = wb.createFont();
      f.setBold(true);
      f.setColor(IndexedColors.WHITE.getIndex());
      cab.setFont(f);
      cab.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
      cab.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      String[] titulos = { "Fecha", "SerialId", "Serial", "Origen", "Destino", "Descripción", "Usuario" };
      Row r0 = sh.createRow(0);
      for (int i = 0; i < titulos.length; i++) {
        r0.createCell(i).setCellValue(titulos[i]);
        r0.getCell(i).setCellStyle(cab);
      }
      int n = 1;
      for (Object[] m : movs) {
        Row r = sh.createRow(n++);
        r.createCell(0).setCellValue(texto(m[0]));
        r.createCell(1).setCellValue(num(m[1]));
        for (int i = 2; i < 7; i++) {
          r.createCell(i).setCellValue(texto(m[i]));
        }
      }
      sh.createFreezePane(0, 1);
      sh.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(0, n - 1), 0, titulos.length - 1));
      int[] anchos = { 20, 10, 22, 22, 22, 70, 18 };
      for (int i = 0; i < anchos.length; i++) {
        sh.setColumnWidth(i, anchos[i] * 256);
      }
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar el Excel de movimientos: " + causa(e), e);
    }
  }

  // ── destinatarios y registro ──────────────────────────────────────────────

  private record Destinos(List<String> para, List<String> cc) {}

  private Destinos destinatarios(String evento, int cli) {
    LinkedHashSet<String> para = new LinkedHashSet<>();
    LinkedHashSet<String> cc = new LinkedHashSet<>();
    for (Object[] r : filas("SELECT Para, Cc FROM " + G + "NotificacionCorreo WHERE Evento = ?1 AND Activo = 1 "
        + "AND (ClienteId = ?2 OR ClienteId IS NULL)", evento, cli)) {
      para.addAll(correos(texto(r[0])));
      cc.addAll(correos(texto(r[1])));
    }
    cc.removeAll(para);
    return new Destinos(new ArrayList<>(para), new ArrayList<>(cc));
  }

  private void registrar(String evento, int cli, String referencia, List<String> para, List<String> cc, String asunto,
      boolean ok, String error, String usuario) {
    String dest = String.join("; ", para) + (cc.isEmpty() ? "" : " | CC: " + String.join("; ", cc));
    new TransactionTemplate(transactionManager).executeWithoutResult(s -> actualizar("INSERT INTO " + G
        + "NotificacionCorreoEnvio (Evento, ClienteId, Referencia, Destinatarios, Asunto, Ok, Error, Usuario, Fecha) "
        + "VALUES (?1, NULLIF(?2, 0), ?3, ?4, ?5, ?6, NULLIF(?7, ''), ?8, GETDATE())",
        evento, cli, corto(referencia, 100), dest, corto(asunto, 300), ok, corto(error == null ? "" : error, 1000),
        corto(usuario, 150)));
  }

  private String nombreUsuario(Integer usuarioId) {
    if (usuarioId == null || usuarioId <= 0) {
      return "";
    }
    List<Object[]> r = filas("SELECT LTRIM(RTRIM(CONCAT(Nombres, ' ', Apellidos))), NombreUsuario FROM " + G
        + "Usuario WHERE Id = ?1", usuarioId);
    if (r.isEmpty()) {
      return String.valueOf(usuarioId);
    }
    String nombre = texto(r.get(0)[0]);
    String login = texto(r.get(0)[1]);
    return nombre.isEmpty() ? login : nombre + (login.isEmpty() ? "" : " (" + login + ")");
  }

  private static List<String> correos(String texto) {
    return Arrays.stream(texto.split("[;,\\s]+")).map(String::trim).filter(s -> !s.isEmpty())
        .map(s -> s.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
  }

  private static List<String> validar(String texto, String campo, boolean requerido) {
    List<String> lista = correos(texto);
    if (requerido && lista.isEmpty()) {
      throw new BusinessRuleException("Indique al menos un correo en " + campo + ".");
    }
    List<String> malos = lista.stream().filter(c -> !CORREO.matcher(c).matches()).collect(Collectors.toList());
    if (!malos.isEmpty()) {
      throw new BusinessRuleException("Correo no válido en " + campo + ": " + String.join(", ", malos));
    }
    return lista;
  }

  // ── utilidades ────────────────────────────────────────────────────────────

  private boolean tablas() {
    List<Object[]> r = filas("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.NotificacionCorreo', 'U') IS NULL "
        + "OR OBJECT_ID('WmsWdGeneral.dbo.NotificacionCorreoEnvio', 'U') IS NULL THEN 0 ELSE 1 END");
    return !r.isEmpty() && num(r.get(0)[0]) == 1;
  }

  private void exigirTablas() {
    if (!tablas()) {
      throw new BusinessRuleException("Las notificaciones aún no están instaladas en la base (falta el script "
          + "2026-10-25_notificaciones_correo.sql).");
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

  private static String causa(Throwable e) {
    Throwable t = e;
    while (t.getCause() != null && t.getCause() != t) {
      t = t.getCause();
    }
    return t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
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

  private static boolean bool(Object o) {
    return o instanceof Boolean b ? b : o instanceof Number n && n.intValue() != 0;
  }

  private static String texto(Object o) {
    return o == null ? "" : String.valueOf(o).trim();
  }

  private static String corto(String s, int max) {
    String v = s == null ? "" : s.trim();
    return v.length() > max ? v.substring(0, max) : v;
  }
}
