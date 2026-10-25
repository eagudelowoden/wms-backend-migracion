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
    String html = htmlSerialMac(asunto, ejemplo, 0, 0);
    LinkedHashMap<String, List<Object[]>> hojas = new LinkedHashMap<>();
    hojas.put("Anterior SERIAL-ORIGINAL", List.of());
    hojas.put("Actual SERIAL-REGULARIZADO", List.of());
    byte[] xlsx = excelMovimientos(hojas, 0);
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

  // ── sonidos de Ingreso por cliente ────────────────────────────────────────

  public static final String SONIDO_ALERTAS = "SONIDO_ALERTAS_INGRESO";

  /** Configuracion de sonidos del cliente (por defecto si no hay fila) y si el interruptor esta encendido. */
  public Map<String, Object> sonidos() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("activo", interruptorSonido());
    m.put("error", "pi-po");
    m.put("duplicado", "pip-pip");
    m.put("fallo", "pum-pom");
    m.put("ok", "tin");
    m.put("repetir", true);
    if (tablaSonidos()) {
      List<Object[]> r = filas("SELECT SonidoError, SonidoDuplicado, SonidoFallo, SonidoOk, Repetir FROM " + G
          + "NotificacionSonido WHERE ClienteId = ?1", cliente());
      if (!r.isEmpty()) {
        m.put("error", texto(r.get(0)[0]));
        m.put("duplicado", texto(r.get(0)[1]));
        m.put("fallo", texto(r.get(0)[2]));
        m.put("ok", texto(r.get(0)[3]));
        m.put("repetir", bool(r.get(0)[4]));
      }
    }
    return m;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> guardarSonidos(Map<String, Object> d, String usuario) {
    if (!tablaSonidos()) {
      throw new BusinessRuleException("Falta instalar la configuración de sonidos (script 2026-10-25_notificaciones_correo.sql).");
    }
    Map<String, Object> actual = sonidos();
    String error = sonido(d.get("error"), actual.get("error"));
    String duplicado = sonido(d.get("duplicado"), actual.get("duplicado"));
    String fallo = sonido(d.get("fallo"), actual.get("fallo"));
    String ok = sonido(d.get("ok"), actual.get("ok"));
    boolean repetir = d.containsKey("repetir") ? Boolean.TRUE.equals(d.get("repetir")) : Boolean.TRUE.equals(actual.get("repetir"));
    actualizar("MERGE " + G + "NotificacionSonido AS t USING (SELECT ?1 AS ClienteId) AS s ON t.ClienteId = s.ClienteId "
        + "WHEN MATCHED THEN UPDATE SET SonidoError = ?2, SonidoDuplicado = ?3, SonidoFallo = ?4, SonidoOk = ?5, "
        + "Repetir = ?6, Usuario = ?7, Fecha = GETDATE() "
        + "WHEN NOT MATCHED THEN INSERT (ClienteId, SonidoError, SonidoDuplicado, SonidoFallo, SonidoOk, Repetir, Usuario, Fecha) "
        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, GETDATE());",
        cliente(), error, duplicado, fallo, ok, repetir, corto(usuario, 150));
    return sonidos();
  }

  /** Enciende o apaga el interruptor SONIDO_ALERTAS_INGRESO del cliente. */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> activarSonidos(boolean activo) {
    Integer tipo = entero("SELECT Id FROM " + G + "ClienteValidacionTipo WHERE Codigo = ?1", SONIDO_ALERTAS);
    if (tipo == null) {
      throw new BusinessRuleException("Falta el interruptor " + SONIDO_ALERTAS + " (script 2026-10-25_instalar_por_base.sql).");
    }
    int n = actualizar("UPDATE " + G + "ClienteValidacion SET Activo = ?3 WHERE ClienteId = ?1 AND ValidacionTipoId = ?2",
        cliente(), tipo, activo);
    if (n == 0) {
      actualizar("INSERT INTO " + G + "ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (?1, ?2, ?3, GETDATE())",
          cliente(), tipo, activo);
    }
    return sonidos();
  }

  private boolean interruptorSonido() {
    Integer n = entero("SELECT COUNT(*) FROM " + G + "ClienteValidacion cv JOIN " + G + "ClienteValidacionTipo t "
        + "ON t.Id = cv.ValidacionTipoId WHERE cv.ClienteId = ?1 AND t.Codigo = ?2 AND cv.Activo = 1", cliente(), SONIDO_ALERTAS);
    return n != null && n > 0;
  }

  private boolean tablaSonidos() {
    Integer n = entero("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.NotificacionSonido', 'U') IS NULL THEN 0 ELSE 1 END");
    return n != null && n == 1;
  }

  private static String sonido(Object nuevo, Object actual) {
    String v = texto(nuevo);
    if (v.isEmpty()) {
      return texto(actual);
    }
    if (!v.matches("[a-z0-9-]{1,30}")) {
      throw new BusinessRuleException("Sonido no válido: " + v);
    }
    return v;
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

      // Excel: una hoja con los movimientos del serial anterior y otra con los del actual (todas sus vueltas)
      String original = datos.get("serialOriginal");
      String actual = datos.get("serialNuevo");
      List<Object[]> movAnterior = movimientosDe(original);
      List<Object[]> movActual = original.equalsIgnoreCase(actual) ? movAnterior : movimientosDe(actual);
      LinkedHashMap<String, List<Object[]>> hojas = new LinkedHashMap<>();
      if (original.equalsIgnoreCase(actual)) {
        hojas.put("Serial " + actual, movActual);
      } else {
        hojas.put("Anterior " + original, movAnterior);
        hojas.put("Actual " + actual, movActual);
      }

      String asunto = asuntoSerialMac(datos);
      String html = htmlSerialMac(asunto, datos, movAnterior.size(), movActual.size());
      byte[] xlsx = excelMovimientos(hojas, serialId);
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

  private List<Object[]> movimientosDe(String serial) {
    return filas("SELECT CONVERT(varchar(19), m.Fecha, 120), m.SerialId, m.Serial, eo.Nombre, ed.Nombre, "
        + "m.Descripcion, u.NombreUsuario FROM Movimiento m LEFT JOIN Estado eo ON eo.Id = m.OrigenId "
        + "LEFT JOIN Estado ed ON ed.Id = m.DestinoId LEFT JOIN " + G + "Usuario u ON u.Id = m.UsuarioId "
        + "WHERE m.Serial = ?1 ORDER BY m.Fecha, m.Id", serial);
  }

  private String asuntoSerialMac(Map<String, String> d) {
    String cli = texto(ClientDatabaseContext.getCurrentClientName());
    return "WMS " + (cli.isEmpty() ? "" : cli + " ") + "· Novedad ERROR SERIAL-MAC: " + d.get("serialOriginal") + " → "
        + d.get("serialNuevo");
  }

  private String htmlSerialMac(String asunto, Map<String, String> d, int movAnterior, int movActual) {
    // tabla horizontal: una fila de titulos y una de datos (la MAC va bajo su serial)
    List<String> columnas = List.of("Usuario", "Fecha novedad", "Fecha ingreso", "Fecha cambio", "SerialId",
        "Serial original", "Serial regularizado", "Tipo origen", "Origen", "Prealerta",
        "Último destino serial original", "Primer destino serial regularizado");
    List<Map<String, Object>> celdas = new ArrayList<>();
    celda(celdas, d.get("usuario"), null, false, false);
    celda(celdas, minuto(d.get("fechaNovedad")), null, false, true);
    celda(celdas, minuto(d.get("fechaIngreso")), null, false, true);
    celda(celdas, minuto(d.get("fechaCambio")), null, false, true);
    celda(celdas, d.get("serialId"), null, false, true);
    celda(celdas, d.get("serialOriginal"), "MAC " + d.get("macOriginal"), false, true);
    celda(celdas, d.get("serialNuevo"), "MAC " + d.get("macNuevo"), true, true);
    celda(celdas, d.get("tipoOrigen"), null, false, false);
    celda(celdas, d.get("origen"), null, false, false);
    celda(celdas, d.get("prealerta"), null, false, false);
    // "NOVEDAD · 2026-10-24 09:15": el destino arriba y su fecha debajo
    String[] ultimo = texto(d.get("ultimoDestinoOriginal")).split(" · ", 2);
    celda(celdas, ultimo[0], ultimo.length > 1 ? ultimo[1] : null, false, true);
    celda(celdas, d.get("primerDestinoNuevo"), null, true, false);
    Map<String, Object> ctx = new LinkedHashMap<>();
    ctx.put("asunto", asunto);
    ctx.put("titulo", EVENTOS.get(EVENTO_SERIAL_MAC));
    ctx.put("cliente", texto(ClientDatabaseContext.getCurrentClientName()));
    ctx.put("modelo", texto(d.get("modelo")));
    ctx.put("columnas", columnas);
    ctx.put("celdas", celdas);
    ctx.put("movAnterior", movAnterior);
    ctx.put("movActual", movActual);
    ctx.put("mismoSerial", texto(d.get("serialOriginal")).equalsIgnoreCase(texto(d.get("serialNuevo"))));
    try {
      StringWriter w = new StringWriter();
      pebble.getTemplate("mail/novedad-serial-mac.html").evaluate(w, ctx);
      return w.toString();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar la plantilla del correo: " + causa(e), e);
    }
  }

  private static void celda(List<Map<String, Object>> celdas, String valor, String sub, boolean resaltar,
      boolean nowrap) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("valor", valor == null || valor.isBlank() ? "—" : valor);
    m.put("sub", sub == null || sub.isBlank() || sub.trim().equals("MAC") ? "" : sub);
    m.put("resaltar", resaltar);
    m.put("nowrap", nowrap);
    celdas.add(m);
  }

  /** "2026-10-24 09:15:00" -> "2026-10-24 09:15" */
  private static String minuto(String fecha) {
    return fecha == null ? "" : fecha.length() > 16 ? fecha.substring(0, 16) : fecha;
  }

  /** Una hoja por serial (anterior y actual); "Vuelta" marca si el movimiento es de este equipo (su SerialId) o de otra vuelta. */
  private byte[] excelMovimientos(LinkedHashMap<String, List<Object[]>> hojas, int serialId) {
    try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle cab = wb.createCellStyle();
      Font f = wb.createFont();
      f.setBold(true);
      f.setColor(IndexedColors.WHITE.getIndex());
      cab.setFont(f);
      // naranja Woden (#E47D06); las filas de este equipo en naranja claro
      ((org.apache.poi.xssf.usermodel.XSSFCellStyle) cab).setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new byte[] { (byte) 0xE4, (byte) 0x7D, (byte) 0x06 }, null));
      cab.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      CellStyle propio = wb.createCellStyle();
      ((org.apache.poi.xssf.usermodel.XSSFCellStyle) propio).setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new byte[] { (byte) 0xFF, (byte) 0xF4, (byte) 0xE8 }, null));
      propio.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      String[] titulos = { "Fecha", "SerialId", "Vuelta", "Serial", "Origen", "Destino", "Descripción", "Usuario" };
      int[] anchos = { 20, 10, 14, 22, 22, 22, 70, 18 };
      for (Map.Entry<String, List<Object[]>> hoja : hojas.entrySet()) {
        // Excel no admite [ ] : * ? / \ en el nombre de la hoja
        String nombre = hoja.getKey().replaceAll("[\\[\\]:*?/\\\\]", "");
        Sheet sh = wb.createSheet(nombre.length() > 31 ? nombre.substring(0, 31) : nombre);
        Row r0 = sh.createRow(0);
        for (int i = 0; i < titulos.length; i++) {
          r0.createCell(i).setCellValue(titulos[i]);
          r0.getCell(i).setCellStyle(cab);
        }
        int n = 1;
        for (Object[] m : hoja.getValue()) {
          Row r = sh.createRow(n++);
          boolean esteEquipo = num(m[1]) == serialId;
          r.createCell(0).setCellValue(texto(m[0]));
          r.createCell(1).setCellValue(num(m[1]));
          r.createCell(2).setCellValue(esteEquipo ? "Este equipo" : "Otra vuelta");
          for (int i = 2; i < 7; i++) {
            r.createCell(i + 1).setCellValue(texto(m[i]));
          }
          if (esteEquipo) {
            for (int i = 0; i < titulos.length; i++) {
              r.getCell(i).setCellStyle(propio);
            }
          }
        }
        if (n == 1) {
          sh.createRow(1).createCell(0).setCellValue("Sin movimientos");
        }
        sh.createFreezePane(0, 1);
        sh.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(0, n - 1), 0, titulos.length - 1));
        for (int i = 0; i < anchos.length; i++) {
          sh.setColumnWidth(i, anchos[i] * 256);
        }
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

  private Integer entero(String sql, Object... params) {
    List<?> rs = consulta(sql, params).getResultList();
    return rs.isEmpty() || rs.get(0) == null ? null : num(rs.get(0));
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
