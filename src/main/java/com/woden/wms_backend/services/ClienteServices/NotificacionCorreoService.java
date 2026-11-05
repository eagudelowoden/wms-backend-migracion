package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.woden.wms_backend.util.PlantillaTexto;

import io.pebbletemplates.pebble.PebbleEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class NotificacionCorreoService {

  private static final Logger logger = LoggerFactory.getLogger(NotificacionCorreoService.class);
  private static final String G = "general.";
  public static final String EVENTO_SERIAL_MAC = "NOVEDAD_ERROR_SERIAL_MAC";
  public static final String EVENTO_PREALERTA_AUTO = "PREALERTA_CERRADA_AUTO";
  public static final String EVENTO_PREALERTA_MANUAL = "PREALERTA_CERRADA_MANUAL";
  private static final Map<String, String> EVENTOS = new LinkedHashMap<>();
  static {
    EVENTOS.put(EVENTO_SERIAL_MAC, "Novedad ERROR SERIAL-MAC regularizada");
    EVENTOS.put(EVENTO_PREALERTA_AUTO, "Prealerta cerrada automáticamente (100 %)");
    EVENTOS.put(EVENTO_PREALERTA_MANUAL, "Prealerta cerrada manualmente");
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

  @Autowired
  private DocumentoPlantillaService plantillas;

  @Autowired
  @org.springframework.context.annotation.Lazy
  private PrealertaCierreService prealertas;


  public List<Map<String, String>> eventos() {
    return EVENTOS.entrySet().stream().map(e -> Map.of("clave", e.getKey(), "nombre", e.getValue()))
        .collect(Collectors.toList());
  }

  public Map<String, Object> estado() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("instalado", tablas());
    m.put("smtp", correo.configurado());
    m.put("cuenta", correo.configurado() ? correo.cuenta() : "");
    m.put("faltan", correo.faltantes());
    return m;
  }

  public List<Map<String, Object>> reglas() {
    if (!tablas()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] r : filas("SELECT n.Id, n.Evento, n.ClienteId, n.Para, n.Cc, n.Activo, n.Usuario, "
        + "to_char(COALESCE(n.FechaModifica, n.Fecha), 'YYYY-MM-DD HH24:MI') FROM " + G + "NotificacionCorreo n "
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
          + "Cc = NULLIF(?4, ''), Activo = ?5, Usuario = ?6, FechaModifica = LOCALTIMESTAMP "
          + "WHERE Id = ?7 AND (ClienteId = ?8 OR ClienteId IS NULL)",
          evento, cli, para, cc, activo, corto(usuario, 150), id, cliente());
      if (n != 1) {
        throw new BusinessRuleException("La notificación no existe.");
      }
    } else {
      id = num(consulta("INSERT INTO " + G + "NotificacionCorreo (Evento, ClienteId, Para, Cc, Activo, Usuario, Fecha) "
          + "VALUES (?1, NULLIF(?2, 0), ?3, NULLIF(?4, ''), ?5, ?6, LOCALTIMESTAMP) RETURNING Id",
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
    for (Object[] r : filas("SELECT e.Id, e.Evento, e.Referencia, e.Destinatarios, e.Asunto, e.Ok, e.Error, "
        + "e.Usuario, to_char(e.Fecha, 'YYYY-MM-DD HH24:MI:SS') FROM " + G + "NotificacionCorreoEnvio e "
        + "WHERE e.ClienteId = ?1 OR e.ClienteId IS NULL ORDER BY e.Id DESC LIMIT 100", cliente())) {
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
    String evento = texto(r.get(0)[0]);
    if (EVENTO_PREALERTA_AUTO.equals(evento) || EVENTO_PREALERTA_MANUAL.equals(evento)) {
      PrealertaCierreService.Correo m = prealertas.ejemplo(EVENTO_PREALERTA_MANUAL.equals(evento), usuario);
      String asuntoPrueba = "[PRUEBA] " + m.asunto();
      String errorPrueba = null;
      try {
        correo.enviar(para, cc, asuntoPrueba, m.html(), m.adjuntos(), m.logo());
      } catch (Exception e) {
        errorPrueba = causa(e);
      }
      registrar(evento + "_PRUEBA", cliente(), "regla " + id, para, cc, asuntoPrueba, errorPrueba == null, errorPrueba,
          usuario);
      if (errorPrueba != null) {
        throw new BusinessRuleException("No se pudo enviar el correo de prueba: " + errorPrueba);
      }
      return Map.of("ok", true, "destinatarios", String.join("; ", para));
    }
    Map<String, Object> cfg = plantillas.config(DocumentoPlantillaService.CORREO_SERIAL_MAC);
    Map<String, String> ejemplo = ejemploSerialMac(usuario);
    String asunto = "[PRUEBA] " + asuntoSerialMac(ejemplo, cfg);
    String html = htmlSerialMac(asunto, ejemplo, 4567, 0, cfg);
    Map<String, byte[]> adjuntos = new LinkedHashMap<>();
    if (!Boolean.FALSE.equals(cfg.get("adjuntarExcel"))) {
      adjuntos.put("movimientos-ejemplo.xlsx", excelMovimientos(List.of(), 0));
    }
    String error = null;
    try {
      correo.enviar(para, cc, asunto, html, adjuntos, texto(cfg.get("logo")));
    } catch (Exception e) {
      error = causa(e);
    }
    registrar(EVENTO_SERIAL_MAC + "_PRUEBA", cliente(), "regla " + id, para, cc, asunto, error == null, error, usuario);
    if (error != null) {
      throw new BusinessRuleException("No se pudo enviar el correo de prueba: " + error);
    }
    return Map.of("ok", true, "destinatarios", String.join("; ", para));
  }


  public boolean hayDestinatarios(String evento) {
    try {
      return tablas() && !destinatarios(evento, cliente()).para.isEmpty();
    } catch (Exception e) {
      logger.warn("[Notificacion] {}: no se pudieron leer los destinatarios: {}", evento, causa(e));
      return false;
    }
  }

  public void enviarEvento(String evento, String referencia, String asunto, String html, Map<String, byte[]> adjuntos,
      String usuario, String logo) {
    try {
      if (!tablas()) {
        return;
      }
      int cli = cliente();
      Destinos dest = destinatarios(evento, cli);
      if (dest.para.isEmpty()) {
        return;
      }
      String nombre = ClientDatabaseContext.getCurrentClientName();
      String db = ClientDatabaseContext.getCurrentClientDb();
      ENVIOS.submit(() -> {
        try {
          ClientDatabaseContext.setCurrentClient(nombre, db, cli);
          String error = null;
          try {
            correo.enviar(dest.para, dest.cc, asunto, html, adjuntos, logo);
          } catch (Exception e) {
            error = causa(e);
            logger.warn("[Notificacion] {} {} no se envio: {}", evento, referencia, error);
          }
          registrar(evento, cli, referencia, dest.para, dest.cc, asunto, error == null, error, usuario);
        } catch (Exception e) {
          logger.warn("[Notificacion] {} {} no se registro: {}", evento, referencia, causa(e));
        } finally {
          ClientDatabaseContext.clear();
        }
      });
    } catch (Exception e) {
      logger.warn("[Notificacion] {} {}: {}", evento, referencia, causa(e));
    }
  }


  public static final String SONIDO_ALERTAS = "SONIDO_ALERTAS_INGRESO";

  public Map<String, Object> sonidos() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("activo", interruptorSonido());
    m.put("error", "pi-po");
    m.put("duplicado", "pip-pip");
    m.put("fallo", "pum-pom");
    m.put("ok", "tin");
    m.put("repetir", true);
    m.put("volumen", VOLUMEN);
    m.put("pulso", PULSO);
    m.put("pausa", PAUSA);
    if (tablaSonidos()) {
      boolean ajustes = columnasAjuste();
      List<Object[]> r = filas("SELECT SonidoError, SonidoDuplicado, SonidoFallo, SonidoOk, Repetir"
          + (ajustes ? ", Volumen, Pulso, Pausa" : "") + " FROM " + G + "NotificacionSonido WHERE ClienteId = ?1", cliente());
      if (!r.isEmpty()) {
        m.put("error", texto(r.get(0)[0]));
        m.put("duplicado", texto(r.get(0)[1]));
        m.put("fallo", texto(r.get(0)[2]));
        m.put("ok", texto(r.get(0)[3]));
        m.put("repetir", bool(r.get(0)[4]));
        if (ajustes) {
          m.put("volumen", r.get(0)[5] == null ? VOLUMEN : num(r.get(0)[5]));
          m.put("pulso", r.get(0)[6] == null ? PULSO : num(r.get(0)[6]));
          m.put("pausa", r.get(0)[7] == null ? PAUSA : num(r.get(0)[7]));
        }
      }
    }
    return m;
  }

  private static final int VOLUMEN = 80;
  private static final int PULSO = 550;
  private static final int PAUSA = 600;

  private boolean columnasAjuste() {
    Integer n = entero("SELECT CASE WHEN general.col_length('general.NotificacionSonido', 'Volumen') IS NULL THEN 0 ELSE 1 END");
    return n != null && n == 1;
  }

  private static int rango(Object nuevo, Object actual, int min, int max, String campo) {
    Object v = nuevo == null ? actual : nuevo;
    int n;
    try {
      n = v instanceof Number num ? num.intValue() : Integer.parseInt(String.valueOf(v).trim());
    } catch (Exception e) {
      throw new BusinessRuleException(campo + " no válido.");
    }
    if (n < min || n > max) {
      throw new BusinessRuleException(campo + " debe estar entre " + min + " y " + max + ".");
    }
    return n;
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
    boolean cambiaAjustes = d.containsKey("volumen") || d.containsKey("pulso") || d.containsKey("pausa");
    if (!columnasAjuste()) {
      if (cambiaAjustes) {
        throw new BusinessRuleException("Falta instalar volumen y ritmo de los sonidos (script 2026-10-26_documentos_prealertas.sql).");
      }
      actualizar("INSERT INTO " + G + "NotificacionSonido (ClienteId, SonidoError, SonidoDuplicado, SonidoFallo, SonidoOk, "
          + "Repetir, Usuario, Fecha) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, LOCALTIMESTAMP) "
          + "ON CONFLICT (ClienteId) DO UPDATE SET SonidoError = EXCLUDED.SonidoError, "
          + "SonidoDuplicado = EXCLUDED.SonidoDuplicado, SonidoFallo = EXCLUDED.SonidoFallo, SonidoOk = EXCLUDED.SonidoOk, "
          + "Repetir = EXCLUDED.Repetir, Usuario = EXCLUDED.Usuario, Fecha = LOCALTIMESTAMP",
          cliente(), error, duplicado, fallo, ok, repetir, corto(usuario, 150));
      return sonidos();
    }
    int volumen = rango(d.get("volumen"), actual.get("volumen"), 10, 100, "El volumen");
    int pulso = rango(d.get("pulso"), actual.get("pulso"), 100, 2000, "La duración del pitido");
    int pausa = rango(d.get("pausa"), actual.get("pausa"), 0, 3000, "La pausa");
    actualizar("INSERT INTO " + G + "NotificacionSonido (ClienteId, SonidoError, SonidoDuplicado, SonidoFallo, SonidoOk, "
        + "Repetir, Usuario, Fecha, Volumen, Pulso, Pausa) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, LOCALTIMESTAMP, ?8, ?9, ?10) "
        + "ON CONFLICT (ClienteId) DO UPDATE SET SonidoError = EXCLUDED.SonidoError, "
        + "SonidoDuplicado = EXCLUDED.SonidoDuplicado, SonidoFallo = EXCLUDED.SonidoFallo, SonidoOk = EXCLUDED.SonidoOk, "
        + "Repetir = EXCLUDED.Repetir, Usuario = EXCLUDED.Usuario, Fecha = LOCALTIMESTAMP, "
        + "Volumen = EXCLUDED.Volumen, Pulso = EXCLUDED.Pulso, Pausa = EXCLUDED.Pausa",
        cliente(), error, duplicado, fallo, ok, repetir, corto(usuario, 150), volumen, pulso, pausa);
    return sonidos();
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> activarSonidos(boolean activo) {
    Integer tipo = entero("SELECT Id FROM " + G + "ClienteValidacionTipo WHERE Codigo = ?1", SONIDO_ALERTAS);
    if (tipo == null) {
      throw new BusinessRuleException("Falta el interruptor " + SONIDO_ALERTAS + " (script 2026-10-25_instalar_por_base.sql).");
    }
    int n = actualizar("UPDATE " + G + "ClienteValidacion SET Activo = ?3 WHERE ClienteId = ?1 AND ValidacionTipoId = ?2",
        cliente(), tipo, activo);
    if (n == 0) {
      actualizar("INSERT INTO " + G + "ClienteValidacion (ClienteId, ValidacionTipoId, Activo, Fecha) VALUES (?1, ?2, ?3, LOCALTIMESTAMP)",
          cliente(), tipo, activo);
    }
    return sonidos();
  }

  private boolean interruptorSonido() {
    Integer n = entero("SELECT COUNT(*) FROM " + G + "ClienteValidacion cv JOIN " + G + "ClienteValidacionTipo t "
        + "ON t.Id = cv.ValidacionTipoId WHERE cv.ClienteId = ?1 AND t.Codigo = ?2 AND cv.Activo = true", cliente(), SONIDO_ALERTAS);
    return n != null && n > 0;
  }

  private boolean tablaSonidos() {
    Integer n = entero("SELECT CASE WHEN to_regclass('general.NotificacionSonido') IS NULL THEN 0 ELSE 1 END");
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


  public Map<String, String> fotoSerialMac(Integer ingresoId) {
    if (ingresoId == null || !tablas()) {
      return null;
    }
    try {
      List<Object[]> r = filas("SELECT i.Id, i.Serial, COALESCE(i.Mac, ''), to_char(i.Fecha, 'YYYY-MM-DD HH24:MI:SS'), i.EstadoId, "
          + "e.Nombre, cs.Codigo, cs.Descripcion, COALESCE(NULLIF(tor.Codigo, ''), tor.Descripcion), "
          + "COALESCE(NULLIF(o.Codigo, ''), o.Descripcion), pr.Nombre "
          + "FROM Ingreso i LEFT JOIN general.Estado e ON e.Id = i.EstadoId LEFT JOIN CodigoSap cs ON cs.Id = i.CodigoSapId "
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
      List<Object[]> nov = filas("SELECT to_char(m.Fecha, 'YYYY-MM-DD HH24:MI:SS') FROM Movimiento m "
          + "WHERE m.SerialId = ?1 AND m.DestinoId = ?2 ORDER BY m.Id DESC LIMIT 1", ingresoId, num(f[4]));
      m.put("fechaNovedad", nov.isEmpty() ? "" : texto(nov.get(0)[0]));
      List<Object[]> ult = filas("SELECT e.Nombre, to_char(m.Fecha, 'YYYY-MM-DD HH24:MI') FROM Movimiento m "
          + "LEFT JOIN general.Estado e ON e.Id = m.DestinoId WHERE m.Serial = ?1 ORDER BY m.Id DESC LIMIT 1", texto(f[1]));
      m.put("ultimoDestinoOriginal", ult.isEmpty() ? texto(f[5])
          : texto(ult.get(0)[0]) + " · " + texto(ult.get(0)[1]));
      return m;
    } catch (Exception e) {
      logger.warn("[Notificacion] no se pudo tomar la foto del serial {}: {}", ingresoId, causa(e));
      return null;
    }
  }

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
      Object[] nuevo = filas("SELECT i.Serial, COALESCE(i.Mac, ''), e.Nombre, to_char(LOCALTIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') "
          + "FROM Ingreso i LEFT JOIN general.Estado e ON e.Id = i.EstadoId WHERE i.Id = ?1", serialId).get(0);
      Map<String, String> datos = new LinkedHashMap<>(foto);
      datos.put("serialNuevo", texto(nuevo[0]));
      datos.put("macNuevo", texto(nuevo[1]));
      datos.put("primerDestinoNuevo", texto(nuevo[2]));
      datos.put("fechaCambio", texto(nuevo[3]));
      datos.put("usuario", nombreUsuario(usuarioId));

      // Excel con los movimientos del SerialId que cambió, no toda la historia de los dos seriales
      List<Object[]> movimientos = movimientosSerialId(serialId);
      Map<String, Object> cfg = plantillas.config(DocumentoPlantillaService.CORREO_SERIAL_MAC);
      String asunto = asuntoSerialMac(datos, cfg);
      String html = htmlSerialMac(asunto, datos, serialId, movimientos.size(), cfg);
      Map<String, byte[]> adjuntos = new LinkedHashMap<>();
      if (!Boolean.FALSE.equals(cfg.get("adjuntarExcel"))) {
        adjuntos.put("movimientos-serialid-" + serialId + ".xlsx", excelMovimientos(movimientos, serialId));
      }
      String logo = texto(cfg.get("logo"));
      String referencia = datos.get("serialOriginal") + " -> " + datos.get("serialNuevo");
      String usuario = datos.get("usuario");

      // el envío va en otro hilo: le paso el cliente de la sesión
      String nombre = ClientDatabaseContext.getCurrentClientName();
      String db = ClientDatabaseContext.getCurrentClientDb();
      ENVIOS.submit(() -> {
        try {
          ClientDatabaseContext.setCurrentClient(nombre, db, cli);
          String error = null;
          try {
            correo.enviar(dest.para, dest.cc, asunto, html, adjuntos, logo);
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

  private List<Object[]> movimientosSerialId(int serialId) {
    return filas("SELECT m.SerialId, m.Serial, eo.Nombre, ed.Nombre, m.Descripcion, u.NombreUsuario, m.Fecha "
        + "FROM Movimiento m LEFT JOIN general.Estado eo ON eo.Id = m.OrigenId LEFT JOIN general.Estado ed ON ed.Id = m.DestinoId "
        + "LEFT JOIN " + G + "Usuario u ON u.Id = m.UsuarioId WHERE m.SerialId = ?1 ORDER BY m.Fecha, m.Id", serialId);
  }

  private String asuntoSerialMac(Map<String, String> d, Map<String, Object> cfg) {
    String asunto = PlantillaTexto.texto(texto(cfg.get("asunto")), varsSerialMac(d, 0, 0)).replaceAll("\\s{2,}", " ").trim();
    return asunto.isEmpty() ? "WMS · Novedad ERROR SERIAL-MAC: " + d.get("serialOriginal") + " → " + d.get("serialNuevo")
        : asunto;
  }

  private static Map<String, String> ejemploSerialMac(String usuario) {
    Map<String, String> ejemplo = new LinkedHashMap<>();
    ejemplo.put("usuario", usuario == null || usuario.isBlank() ? "usuario" : usuario);
    ejemplo.put("fechaNovedad", "2026-10-24 09:15:00");
    ejemplo.put("fechaIngreso", "2026-10-20 14:02:11");
    ejemplo.put("fechaCambio", "2026-10-25 10:30:00");
    ejemplo.put("serialId", "4567");
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
    return ejemplo;
  }

  public String vistaPreviaSerialMac(Map<String, Object> cfg, String usuario) {
    String html = htmlSerialMac("Vista previa", ejemploSerialMac(usuario), 4567, 18, cfg);
    String logo = texto(cfg.get("logo"));
    if (!logo.startsWith("data:image/")) {
      try (java.io.InputStream in = getClass().getResourceAsStream("/mail/logo-woden.png")) {
        logo = in == null ? "" : "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(in.readAllBytes());
      } catch (Exception e) {
        logo = "";
      }
    }
    return html.replace("cid:logo", logo);
  }

  private static Map<String, String> varsSerialMac(Map<String, String> d, int serialId, int movimientos) {
    Map<String, String> v = new HashMap<>();
    v.put("cliente", texto(ClientDatabaseContext.getCurrentClientName()));
    v.put("usuario", texto(d.get("usuario")));
    v.put("fecha_novedad", minuto(d.get("fechaNovedad")));
    v.put("fecha_ingreso", minuto(d.get("fechaIngreso")));
    v.put("fecha_cambio", minuto(d.get("fechaCambio")));
    v.put("serial_id", serialId > 0 ? String.valueOf(serialId) : texto(d.get("serialId")));
    v.put("serial_original", texto(d.get("serialOriginal")));
    v.put("mac_original", texto(d.get("macOriginal")));
    v.put("serial_nuevo", texto(d.get("serialNuevo")));
    v.put("mac_nuevo", texto(d.get("macNuevo")));
    v.put("modelo", texto(d.get("modelo")));
    v.put("tipo_origen", texto(d.get("tipoOrigen")));
    v.put("origen", texto(d.get("origen")));
    v.put("prealerta", texto(d.get("prealerta")));
    String[] ultimo = texto(d.get("ultimoDestinoOriginal")).split(" · ", 2);
    v.put("ultimo_destino", ultimo[0]);
    v.put("ultimo_destino_fecha", ultimo.length > 1 ? ultimo[1] : "");
    v.put("primer_destino", texto(d.get("primerDestinoNuevo")));
    v.put("movimientos", String.valueOf(movimientos));
    return v;
  }

  @SuppressWarnings("unchecked")
  private String htmlSerialMac(String asunto, Map<String, String> d, int serialId, int movimientos,
      Map<String, Object> cfg) {
    Map<String, String> v = varsSerialMac(d, serialId, movimientos);
    Map<String, Object> col = cfg.get("colores") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
    Map<String, String> c = new HashMap<>();
    c.put("acento", PlantillaTexto.color(col.get("acento"), "#E47D06"));
    c.put("bordeAcento", PlantillaTexto.color(col.get("bordeAcento"), "#d96f00"));
    c.put("texto", PlantillaTexto.color(col.get("texto"), "#1b232d"));
    c.put("resaltado", PlantillaTexto.color(col.get("resaltado"), "#fff4e8"));
    c.put("resaltadoTexto", PlantillaTexto.color(col.get("resaltadoTexto"), "#b45f00"));
    c.put("tenue", PlantillaTexto.color(col.get("tenue"), "#6b7280"));
    c.put("fondo", PlantillaTexto.color(col.get("fondo"), "#f3f4f6"));
    c.put("pieFondo", PlantillaTexto.color(col.get("pieFondo"), "#1b232d"));
    c.put("pieTexto", PlantillaTexto.color(col.get("pieTexto"), "#c7ccd3"));

    List<Map<String, Object>> celdas = new ArrayList<>();
    if (cfg.get("columnas") instanceof List<?> columnas) {
      for (Object o : columnas) {
        Map<String, Object> f = o instanceof Map<?, ?> mf ? (Map<String, Object>) mf : Map.of();
        String valor = PlantillaTexto.html(texto(f.get("valor")), v);
        String sub = texto(f.get("sub"));
        Map<String, Object> celda = new HashMap<>();
        celda.put("titulo", PlantillaTexto.html(texto(f.get("titulo")), v));
        celda.put("valor", valor.isBlank() ? "—" : valor);
        celda.put("sub", PlantillaTexto.vacia(sub, v) ? "" : PlantillaTexto.html(sub, v));
        celda.put("resaltar", Boolean.TRUE.equals(f.get("resaltar")));
        celda.put("nowrap", Boolean.TRUE.equals(f.get("nowrap")));
        celdas.add(celda);
      }
    }
    List<String> despedida = new ArrayList<>();
    if (cfg.get("despedida") instanceof List<?> lineas) {
      for (Object l : lineas) {
        if (!PlantillaTexto.vacia(texto(l), v)) {
          despedida.add(PlantillaTexto.htmlRico(texto(l), v));
        }
      }
    }
    boolean vertical = "vertical".equals(texto(cfg.get("disposicion")));
    Map<String, Object> ctx = new LinkedHashMap<>();
    ctx.put("c", c);
    ctx.put("asunto", PlantillaTexto.escapar(asunto));
    ctx.put("usar_logo", !Boolean.FALSE.equals(cfg.get("usarLogo")));
    ctx.put("marca", PlantillaTexto.html(texto(cfg.get("marca")), v));
    ctx.put("titulo", PlantillaTexto.html(texto(cfg.get("titulo")), v));
    String subtitulo = texto(cfg.get("subtitulo"));
    ctx.put("subtitulo", PlantillaTexto.vacia(subtitulo, v) ? "" : PlantillaTexto.html(subtitulo, v));
    ctx.put("intro", PlantillaTexto.htmlRico(texto(cfg.get("intro")), v));
    ctx.put("vertical", vertical);
    ctx.put("ancho", vertical ? 760 : 1180);
    ctx.put("celdas", celdas);
    ctx.put("movimientos", Boolean.FALSE.equals(cfg.get("adjuntarExcel")) ? ""
        : PlantillaTexto.htmlRico(texto(cfg.get("textoMovimientos")), v));
    ctx.put("despedida", despedida);
    ctx.put("pie", PlantillaTexto.htmlRico(texto(cfg.get("pie")), v));
    try {
      StringWriter w = new StringWriter();
      pebble.getTemplate("mail/novedad-serial-mac.html").evaluate(w, ctx);
      return w.toString();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar la plantilla del correo: " + causa(e), e);
    }
  }

  private static String minuto(String fecha) {
    return fecha == null ? "" : fecha.length() > 16 ? fecha.substring(0, 16) : fecha;
  }

  private byte[] excelMovimientos(List<Object[]> movimientos, int serialId) {
    try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle cab = wb.createCellStyle();
      Font f = wb.createFont();
      f.setBold(true);
      f.setColor(IndexedColors.WHITE.getIndex());
      cab.setFont(f);
      ((org.apache.poi.xssf.usermodel.XSSFCellStyle) cab).setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new byte[] { (byte) 0xE4, (byte) 0x7D, (byte) 0x06 }, null));
      cab.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      CellStyle fecha = wb.createCellStyle();
      fecha.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss.000"));
      String[] titulos = { "SerialId", "Serial", "Origen", "Destino", "Descripcion", "NombreUsuario", "Fecha" };
      int[] anchos = { 10, 22, 22, 22, 70, 18, 24 };
      Sheet sh = wb.createSheet(serialId > 0 ? "SerialId " + serialId : "Movimientos");
      Row r0 = sh.createRow(0);
      for (int i = 0; i < titulos.length; i++) {
        r0.createCell(i).setCellValue(titulos[i]);
        r0.getCell(i).setCellStyle(cab);
      }
      int n = 1;
      for (Object[] m : movimientos) {
        Row r = sh.createRow(n++);
        r.createCell(0).setCellValue(num(m[0]));
        for (int i = 1; i <= 5; i++) {
          r.createCell(i).setCellValue(texto(m[i]));
        }
        if (m[6] instanceof java.util.Date dia) {
          r.createCell(6).setCellValue(dia);
          r.getCell(6).setCellStyle(fecha);
        } else if (m[6] instanceof java.time.LocalDateTime dia) {
          r.createCell(6).setCellValue(dia);
          r.getCell(6).setCellStyle(fecha);
        } else {
          r.createCell(6).setCellValue(texto(m[6]));
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
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo armar el Excel de movimientos: " + causa(e), e);
    }
  }


  private record Destinos(List<String> para, List<String> cc) {}

  private Destinos destinatarios(String evento, int cli) {
    LinkedHashSet<String> para = new LinkedHashSet<>();
    LinkedHashSet<String> cc = new LinkedHashSet<>();
    for (Object[] r : filas("SELECT Para, Cc FROM " + G + "NotificacionCorreo WHERE Evento = ?1 AND Activo = true "
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
        + "VALUES (?1, NULLIF(?2, 0), ?3, ?4, ?5, ?6, NULLIF(?7, ''), ?8, LOCALTIMESTAMP)",
        evento, cli, corto(referencia, 100), dest, corto(asunto, 300), ok, corto(error == null ? "" : error, 1000),
        corto(usuario, 150)));
  }

  private String nombreUsuario(Integer usuarioId) {
    if (usuarioId == null || usuarioId <= 0) {
      return "";
    }
    List<Object[]> r = filas("SELECT TRIM(CONCAT(Nombres, ' ', Apellidos)), NombreUsuario FROM " + G
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


  private boolean tablas() {
    List<Object[]> r = filas("SELECT CASE WHEN to_regclass('general.NotificacionCorreo') IS NULL "
        + "OR to_regclass('general.NotificacionCorreoEnvio') IS NULL THEN 0 ELSE 1 END");
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
