package com.woden.wms_backend.services.ClienteServices;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class BoletaReservaService {

  private static final Logger logger = LoggerFactory.getLogger(BoletaReservaService.class);
  private static final String G = "general.";
  private static final String CONSECUTIVO = "BOLETA_CONSECUTIVO";

  // si el script no está corrido no bloqueo el despacho normal
  private static volatile boolean tablasListas = false;
  private static volatile boolean cantidadesListas = false;

  @PersistenceContext
  private EntityManager em;

  // {AAAA} año, {AA} año corto, {MM} mes, {NNN} número. Reinicio el contador cada mes si lleva {MM}, cada año
  // si lleva el año y nunca si no lleva ninguno
  private static final String FORMATO_TIPO = "CONSECUTIVO_BOLETA";
  private static final String FORMATO_FABRICA = "{AAAA}-{NNN}";
  private static final Pattern TOKEN = Pattern.compile("\\{([A-Z]+)\\}");
  private static final Pattern FORMATO_JSON = Pattern.compile("\"formato\"\\s*:\\s*\"([^\"]*)\"");
  private static final int MAX_DOCUMENTO = 20;

  private record Periodo(int clave, int anio, int mes) {}

  public boolean usaConsecutivo() {
    Integer n = entero("SELECT COUNT(*) FROM " + G + "ClienteValidacion cv JOIN " + G
        + "ClienteValidacionTipo t ON t.Id = cv.ValidacionTipoId "
        + "WHERE cv.ClienteId = ?1 AND t.Codigo = ?2 AND cv.Activo = true AND t.Activo = true", cliente(), CONSECUTIVO);
    return n != null && n > 0;
  }

  public Map<String, Object> config(String zonaHoraria) {
    Map<String, Object> out = new LinkedHashMap<>();
    boolean consecutivo = tablas() && usaConsecutivo();
    out.put("consecutivo", consecutivo);
    if (consecutivo) {
      String formato = formatoCliente();
      Periodo p = periodo(formato, zonaHoraria);
      Integer ultimo = entero("SELECT Ultimo FROM " + G + "BoletaConsecutivo WHERE ClienteId = ?1 AND Anio = ?2",
          cliente(), p.clave());
      out.put("siguiente", documento(formato, p, (ultimo == null ? 0 : ultimo) + 1));
    }
    return out;
  }

  public Map<String, Object> consecutivo(String zonaHoraria, String formatoPropuesto) {
    exigirTablas();
    String guardado = formatoCliente();
    String propuesto = limpio(formatoPropuesto);
    String error = propuesto.isEmpty() ? null : errorFormato(propuesto);
    String formato = propuesto.isEmpty() || error != null ? guardado : propuesto;
    Periodo p = periodo(formato, zonaHoraria);
    Integer ultimo = entero("SELECT Ultimo FROM " + G + "BoletaConsecutivo WHERE ClienteId = ?1 AND Anio = ?2",
        cliente(), p.clave());
    List<Object[]> usado = filas("SELECT Consecutivo, Documento FROM " + G + "BoletaMovimiento "
        + "WHERE ClienteId = ?1 AND Anio = ?2 AND Consecutivo IS NOT NULL ORDER BY Consecutivo DESC LIMIT 1", cliente(), p.clave());
    int u = ultimo == null ? 0 : ultimo;
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("activo", usaConsecutivo());
    out.put("formato", formato);
    out.put("formatoGuardado", guardado);
    out.put("errorFormato", error == null ? "" : error);
    out.put("anio", p.anio());
    out.put("mes", p.mes());
    out.put("ultimo", u);
    out.put("ultimoDocumento", u == 0 ? "" : documento(formato, p, u));
    out.put("siguiente", documento(formato, p, u + 1));
    out.put("minimo", usado.isEmpty() ? 0 : num(usado.get(0)[0]));
    out.put("minimoDocumento", usado.isEmpty() ? "" : limpio(String.valueOf(usado.get(0)[1])));
    return out;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> fijarConsecutivo(int ultimo, String zonaHoraria, String formatoNuevo) {
    exigirTablas();
    String guardado = formatoCliente();
    String formato = limpio(formatoNuevo).isEmpty() ? guardado : limpio(formatoNuevo);
    String error = errorFormato(formato);
    if (error != null) {
      throw new BusinessRuleException(error);
    }
    if (ultimo < 0 || ultimo > 999999) {
      throw new BusinessRuleException("El consecutivo debe estar entre 0 y 999999.");
    }
    Periodo p = periodo(formato, zonaHoraria);
    List<Object[]> usado = filas("SELECT Consecutivo, Documento FROM " + G + "BoletaMovimiento "
        + "WHERE ClienteId = ?1 AND Anio = ?2 AND Consecutivo IS NOT NULL ORDER BY Consecutivo DESC LIMIT 1", cliente(), p.clave());
    if (!usado.isEmpty() && ultimo < num(usado.get(0)[0])) {
      throw new BusinessRuleException("Ya existe la boleta " + usado.get(0)[1] + ": el último consecutivo no puede "
          + "ser menor.");
    }
    String siguiente = documento(formato, p, ultimo + 1);
    if (siguiente.length() > MAX_DOCUMENTO) {
      throw new BusinessRuleException("El documento " + siguiente + " pasa de " + MAX_DOCUMENTO + " caracteres.");
    }
    if (!formato.equals(guardado)) {
      guardarFormato(formato);
    }
    actualizar("INSERT INTO " + G + "BoletaConsecutivo (ClienteId, Anio, Ultimo) VALUES (?1, ?2, ?3) "
        + "ON CONFLICT (ClienteId, Anio) DO UPDATE SET Ultimo = EXCLUDED.Ultimo",
        cliente(), p.clave(), ultimo);
    return consecutivo(zonaHoraria, "");
  }

  private int tomarConsecutivo(int cliente, int clave) {
    Object r = consulta("INSERT INTO " + G + "BoletaConsecutivo (ClienteId, Anio, Ultimo) VALUES (?1, ?2, 1) "
        + "ON CONFLICT (ClienteId, Anio) DO UPDATE SET Ultimo = " + G + "BoletaConsecutivo.Ultimo + 1 "
        + "RETURNING Ultimo", cliente, clave).getSingleResult();
    return num(r);
  }

  private String formatoCliente() {
    Integer hay = entero("SELECT CASE WHEN to_regclass('general.DocumentoPlantilla') IS NULL THEN 0 ELSE 1 END");
    if (hay == null || hay == 0) {
      return FORMATO_FABRICA;
    }
    List<Object[]> r = filas("SELECT Config FROM " + G + "DocumentoPlantilla WHERE ClienteId = ?1 AND Tipo = ?2",
        cliente(), FORMATO_TIPO);
    if (r.isEmpty() || r.get(0)[0] == null) {
      return FORMATO_FABRICA;
    }
    Matcher m = FORMATO_JSON.matcher(String.valueOf(r.get(0)[0]));
    String f = m.find() ? m.group(1) : "";
    return errorFormato(f) == null ? f : FORMATO_FABRICA;
  }

  private void guardarFormato(String formato) {
    Integer hay = entero("SELECT CASE WHEN to_regclass('general.DocumentoPlantilla') IS NULL THEN 0 ELSE 1 END");
    if (hay == null || hay == 0) {
      throw new BusinessRuleException("Para cambiar el formato falta el script 2026-10-26_documentos_prealertas.sql.");
    }
    actualizar("INSERT INTO " + G + "DocumentoPlantilla (ClienteId, Tipo, Config, Activo, Usuario, Fecha) "
        + "VALUES (?1, ?2, ?3, true, '', LOCALTIMESTAMP) "
        + "ON CONFLICT (ClienteId, Tipo) DO UPDATE SET Config = EXCLUDED.Config, Activo = true, Fecha = LOCALTIMESTAMP",
        cliente(), FORMATO_TIPO, "{\"formato\":\"" + formato + "\"}");
  }

  private static String errorFormato(String formato) {
    String f = limpio(formato);
    if (f.isEmpty()) {
      return "Indique el formato del consecutivo.";
    }
    if (f.length() > 40) {
      return "El formato admite máximo 40 caracteres.";
    }
    int numeros = 0;
    boolean anio = false;
    boolean mes = false;
    Matcher m = TOKEN.matcher(f);
    while (m.find()) {
      String t = m.group(1);
      if (t.matches("N{1,9}")) {
        numeros++;
      } else if (t.equals("AAAA") || t.equals("AA")) {
        anio = true;
      } else if (t.equals("MM")) {
        mes = true;
      } else {
        return "Variable no válida en el formato: {" + t + "}.";
      }
    }
    if (!TOKEN.matcher(f).replaceAll("").matches("[A-Za-z0-9 ._/#-]*")) {
      return "El formato solo admite letras, números, espacios, . _ / # - y las variables {AAAA} {AA} {MM} {NNN}.";
    }
    if (numeros != 1) {
      return "El formato debe llevar una vez el número: {NNN}.";
    }
    if (mes && !anio) {
      return "Con {MM} el formato debe llevar también el año ({AAAA} o {AA}).";
    }
    return null;
  }

  private static Periodo periodo(String formato, String zonaHoraria) {
    LocalDate hoy = hoy(zonaHoraria);
    int clave = formato.contains("{MM}") ? hoy.getYear() * 100 + hoy.getMonthValue()
        : formato.contains("{AAAA}") || formato.contains("{AA}") ? hoy.getYear() : 1;
    return new Periodo(clave, hoy.getYear(), hoy.getMonthValue());
  }

  private static String documento(String formato, Periodo p, int numero) {
    Matcher m = TOKEN.matcher(formato);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String t = m.group(1);
      String v = switch (t) {
        case "AAAA" -> String.valueOf(p.anio());
        case "AA" -> String.format("%02d", p.anio() % 100);
        case "MM" -> String.format("%02d", p.mes());
        default -> String.format("%0" + t.length() + "d", numero);
      };
      m.appendReplacement(sb, Matcher.quoteReplacement(v));
    }
    m.appendTail(sb);
    return sb.toString();
  }


  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> reservar(BoletaMovimientoDTO r) {
    exigirTablas();
    if (!usaConsecutivo()) {
      throw new BusinessRuleException("El cliente no tiene activas las boletas guardadas (BOLETA_CONSECUTIVO).");
    }
    int cliente = cliente();
    List<BoletaMovimientoDTO.ItemDTO> items = r.getEquipos() == null ? List.of() : r.getEquipos();
    List<Integer> ids = items.stream().map(BoletaMovimientoDTO.ItemDTO::getId).filter(Objects::nonNull).distinct()
        .collect(Collectors.toList());
    if (ids.isEmpty()) {
      throw new BusinessRuleException("Seleccione al menos un pallet.");
    }
    if (limpio(r.getFechaEntrega()).isEmpty() || limpio(r.getOrigen()).isEmpty() || limpio(r.getDestino()).isEmpty()) {
      throw new BusinessRuleException("Complete fecha, origen y destino.");
    }

    List<Object[]> ocupados = filas("SELECT p.NoPallet, b.Documento FROM " + G + "BoletaMovimientoPallet p JOIN "
        + G + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = true AND p.PalletId IN ("
        + enLista(ids) + ") LIMIT 1", cliente);
    if (!ocupados.isEmpty()) {
      throw new BusinessRuleException("El pallet " + ocupados.get(0)[0] + " ya está en la boleta " + ocupados.get(0)[1]
          + ". Despáchela o cancele esa reserva.");
    }

    String documento;
    int anio = 0;
    int consecutivo = 0;
    if (usaConsecutivo()) {
      String formato = formatoCliente();
      Periodo p = periodo(formato, r.getZonaHoraria());
      anio = p.clave();
      consecutivo = tomarConsecutivo(cliente, anio);
      documento = documento(formato, p, consecutivo);
      if (documento.length() > MAX_DOCUMENTO) {
        throw new BusinessRuleException("El documento " + documento + " pasa de " + MAX_DOCUMENTO
            + " caracteres: revise el formato del consecutivo en Documentos.");
      }
    } else {
      documento = limpio(r.getPedidoSap());
      if (documento.isEmpty()) {
        throw new BusinessRuleException("Indique el documento.");
      }
      if (documento.length() > 20) {
        throw new BusinessRuleException("El documento admite máximo 20 caracteres.");
      }
    }

    Integer boletaId = num(consulta("INSERT INTO " + G + "BoletaMovimiento (ClienteId, Documento, Anio, Consecutivo, "
        + "FechaEntrega, Origen, Destino, Observaciones, Estado, UsuarioId, UsuarioNombre, Fecha) "
        + "VALUES (?1, ?2, NULLIF(?3, 0), NULLIF(?4, 0), CAST(NULLIF(?5, '') AS date), ?6, ?7, NULLIF(?8, ''), "
        + "'RESERVADA', NULLIF(?9, 0), ?10, LOCALTIMESTAMP) RETURNING Id",
        cliente, documento, anio, consecutivo, limpio(r.getFechaEntrega()), corto(r.getOrigen(), 100),
        corto(r.getDestino(), 100), corto(r.getObservacionesGenerales(), 1000),
        r.getUsuarioId() == null ? 0 : r.getUsuarioId(), corto(r.getUsuarioNombre(), 150)).getSingleResult());

    LinkedHashSet<Integer> vistos = new LinkedHashSet<>();
    for (BoletaMovimientoDTO.ItemDTO it : items) {
      if (it.getId() == null || !vistos.add(it.getId())) {
        continue;
      }
      actualizar("INSERT INTO " + G + "BoletaMovimientoPallet (BoletaId, ClienteId, PalletId, NoPallet, CodigoSap, "
          + "Descripcion, Cantidad, Tipologia, Activo, Despachado) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, true, false)",
          boletaId, cliente, it.getId(), corto(it.getNoPallet(), 50), corto(it.getCodigoSap(), 50),
          corto(it.getDescripcion(), 200), it.getCantidad() == null ? 0 : it.getCantidad(), corto(it.getTipologia(), 100));
      if (cantidades()) {
        actualizar("UPDATE " + G + "BoletaMovimientoPallet SET Equipos = ?3, Accesorios = ?4, Smartcards = ?5 "
            + "WHERE BoletaId = ?1 AND PalletId = ?2", boletaId, it.getId(), cero(it.getCantidadEquipos()),
            cero(it.getCantidadAccesorios()), cero(it.getCantidadSmartcard()));
      }
    }
    logger.info("[Boleta] reservada {} cliente {} pallets {}", documento, cliente, vistos.size());
    return detalle(boletaId);
  }


  public List<Map<String, Object>> listar(String estado, String texto) {
    if (!tablas()) {
      return List.of();
    }
    String est = limpio(estado).toUpperCase();
    List<Object[]> rs = filas("SELECT b.Id, b.Documento, to_char(b.FechaEntrega, 'YYYY-MM-DD'), b.Origen, "
        + "b.Destino, b.Estado, b.UsuarioNombre, to_char(b.Fecha, 'YYYY-MM-DD HH24:MI'), "
        + "(SELECT COUNT(*) FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id), "
        + "(SELECT COALESCE(SUM(p.Cantidad), 0) FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id), "
        + "to_char(b.FechaDespacho, 'YYYY-MM-DD HH24:MI') "
        + "FROM " + G + "BoletaMovimiento b WHERE b.ClienteId = ?1 "
        + "AND (?2 = '' OR b.Estado = ?2 OR (?2 = 'RESERVADA' AND b.Estado = 'DESPACHANDO')) "
        + "AND (?3 = '' OR b.Documento LIKE '%' || ?3 || '%' OR EXISTS (SELECT 1 FROM " + G
        + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id AND p.NoPallet LIKE '%' || ?3 || '%')) "
        + "ORDER BY b.Fecha DESC LIMIT 300", cliente(), est, limpio(texto));
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] f : rs) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(f[0]));
      m.put("documento", f[1]);
      m.put("fechaEntrega", f[2]);
      m.put("origen", f[3]);
      m.put("destino", f[4]);
      m.put("estado", f[5]);
      m.put("usuarioNombre", f[6]);
      m.put("fecha", f[7]);
      m.put("pallets", num(f[8]));
      m.put("unidades", num(f[9]));
      m.put("fechaDespacho", f[10]);
      out.add(m);
    }
    return out;
  }

  public Map<String, Object> detalle(int boletaId) {
    exigirTablas();
    List<Object[]> cab = filas("SELECT b.Id, b.Documento, to_char(b.FechaEntrega, 'YYYY-MM-DD'), b.Origen, b.Destino, "
        + "b.Observaciones, b.Estado, b.UsuarioNombre, to_char(b.Fecha, 'YYYY-MM-DD HH24:MI'), "
        + "to_char(b.FechaDespacho, 'YYYY-MM-DD HH24:MI'), CASE WHEN b.Consecutivo IS NULL THEN 0 ELSE 1 END "
        + "FROM " + G + "BoletaMovimiento b WHERE b.Id = ?1 AND b.ClienteId = ?2", boletaId, cliente());
    if (cab.isEmpty()) {
      throw new BusinessRuleException("La boleta no existe.");
    }
    Object[] c = cab.get(0);
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("id", num(c[0]));
    out.put("documento", c[1]);
    out.put("fechaEntrega", c[2]);
    out.put("origen", c[3]);
    out.put("destino", c[4]);
    out.put("observaciones", c[5]);
    out.put("estado", c[6]);
    out.put("usuarioNombre", c[7]);
    out.put("fecha", c[8]);
    out.put("fechaDespacho", c[9]);
    out.put("consecutivo", num(c[10]) == 1);

    List<Map<String, Object>> pallets = new ArrayList<>();
    String cantidades = cantidades() ? "p.Equipos, p.Accesorios, p.Smartcards" : "NULL, NULL, NULL";
    for (Object[] p : filas("SELECT p.PalletId, p.NoPallet, p.CodigoSap, p.Descripcion, p.Cantidad, p.Tipologia, "
        + "p.Activo, p.Despachado, " + cantidades + " FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = ?1 "
        + "ORDER BY p.Id", boletaId)) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(p[0]));
      m.put("noPallet", p[1]);
      m.put("codigoSap", p[2]);
      m.put("descripcion", p[3]);
      m.put("cantidad", num(p[4]));
      m.put("tipologia", p[5]);
      m.put("activo", bool(p[6]));
      m.put("despachado", bool(p[7]));
      m.put("cantidadEquipos", p[8] == null ? null : num(p[8]));
      m.put("cantidadAccesorios", p[9] == null ? null : num(p[9]));
      m.put("cantidadSmartcard", p[10] == null ? null : num(p[10]));
      pallets.add(m);
    }
    out.put("pallets", pallets);
    return out;
  }

  public List<Map<String, Object>> reservados() {
    if (!tablas()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] f : filas("SELECT p.PalletId, b.Id, b.Documento, b.Estado FROM " + G + "BoletaMovimientoPallet p JOIN "
        + G + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = true", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("palletId", num(f[0]));
      m.put("boletaId", num(f[1]));
      m.put("documento", f[2]);
      m.put("estado", f[3]);
      out.add(m);
    }
    return out;
  }


  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> iniciarDespacho(int boletaId) {
    String estado = estado(boletaId);
    if (!"RESERVADA".equals(estado) && !"DESPACHANDO".equals(estado)) {
      throw new BusinessRuleException("La boleta está " + estado.toLowerCase() + ".");
    }
    actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'DESPACHANDO' WHERE Id = ?1", boletaId);
    return detalle(boletaId);
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> finalizarDespacho(int boletaId, Integer usuarioId) {
    String estado = estado(boletaId);
    if (!"DESPACHANDO".equals(estado) && !"RESERVADA".equals(estado)) {
      throw new BusinessRuleException("La boleta está " + estado.toLowerCase() + ".");
    }
    marcarDespachados(boletaId);
    Integer pendientes = entero("SELECT COUNT(*) FROM " + G + "BoletaMovimientoPallet WHERE BoletaId = ?1 AND Despachado = false",
        boletaId);
    if (pendientes == null || pendientes == 0) {
      actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'DESPACHADA', FechaDespacho = LOCALTIMESTAMP, "
          + "UsuarioDespachoId = NULLIF(?2, 0) WHERE Id = ?1", boletaId, usuarioId == null ? 0 : usuarioId);
    } else {
      actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'RESERVADA' WHERE Id = ?1", boletaId);
    }
    return detalle(boletaId);
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> cancelar(int boletaId, Integer usuarioId) {
    String estado = estado(boletaId);
    if (!"RESERVADA".equals(estado) && !"DESPACHANDO".equals(estado)) {
      throw new BusinessRuleException("La boleta está " + estado.toLowerCase() + ".");
    }
    marcarDespachados(boletaId);
    actualizar("UPDATE " + G + "BoletaMovimientoPallet SET Activo = false WHERE BoletaId = ?1", boletaId);
    actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'CANCELADA', FechaCancela = LOCALTIMESTAMP, "
        + "UsuarioCancelaId = NULLIF(?2, 0) WHERE Id = ?1", boletaId, usuarioId == null ? 0 : usuarioId);
    logger.info("[Boleta] cancelada {} cliente {}", boletaId, cliente());
    return detalle(boletaId);
  }

  private void marcarDespachados(int boletaId) {
    // Pallet esta en la base del cliente (conexion actual); la boleta en WmsWdGeneral
    actualizar("UPDATE " + G + "BoletaMovimientoPallet bp SET Despachado = true, Activo = false FROM Pallet p "
        + "WHERE p.Id = bp.PalletId AND bp.BoletaId = ?1 AND bp.Despachado = false AND COALESCE(p.Activo, false) = false",
        boletaId);
  }


  public void validarPalletsLibres(Collection<Integer> palletIds) {
    List<Integer> ids = palletIds == null ? List.of()
        : palletIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    if (ids.isEmpty() || !tablas()) {
      return;
    }
    List<Object[]> r = filas("SELECT p.NoPallet, b.Documento FROM " + G + "BoletaMovimientoPallet p JOIN " + G
        + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = true AND b.Estado = 'RESERVADA' "
        + "AND p.PalletId IN (" + enLista(ids) + ") LIMIT 1", cliente());
    if (!r.isEmpty()) {
      throw new BusinessRuleException("El pallet " + r.get(0)[0] + " está reservado en la boleta " + r.get(0)[1]
          + ". Despache la boleta o cancele la reserva para liberarlo.");
    }
  }


  private boolean tablas() {
    if (!tablasListas) {
      Integer n = entero("SELECT CASE WHEN to_regclass('general.BoletaMovimientoPallet') IS NULL "
          + "OR to_regclass('general.BoletaConsecutivo') IS NULL THEN 0 ELSE 1 END");
      tablasListas = n != null && n == 1;
    }
    return tablasListas;
  }

  private boolean cantidades() {
    if (!cantidadesListas) {
      Integer n = entero("SELECT CASE WHEN general.col_length('general.BoletaMovimientoPallet', 'Smartcards') IS NULL "
          + "THEN 0 ELSE 1 END");
      cantidadesListas = n != null && n == 1;
    }
    return cantidadesListas;
  }

  private static int cero(Integer n) {
    return n == null ? 0 : n;
  }

  private void exigirTablas() {
    if (!tablas()) {
      throw new BusinessRuleException("Las boletas guardadas aún no están instaladas en la base (falta el script "
          + "2026-10-24_boleta_reservas_consecutivo.sql).");
    }
  }

  private String estado(int boletaId) {
    exigirTablas();
    List<Object[]> r = filas("SELECT Estado, Id FROM " + G + "BoletaMovimiento WHERE Id = ?1 AND ClienteId = ?2 FOR UPDATE",
        boletaId, cliente());
    if (r.isEmpty()) {
      throw new BusinessRuleException("La boleta no existe.");
    }
    return String.valueOf(r.get(0)[0]);
  }

  private int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    if (id == null || id <= 0) {
      throw new BusinessRuleException("No se identificó el cliente de la sesión.");
    }
    return id;
  }

  private static LocalDate hoy(String zonaHoraria) {
    try {
      return LocalDate.now(ZoneId.of(limpio(zonaHoraria)));
    } catch (DateTimeException e) {
      return LocalDate.now(ZoneId.of("America/Bogota"));
    }
  }

  private static String enLista(List<Integer> ids) {
    // solo enteros: sin riesgo de inyección
    return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
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
    return o == null ? 0 : ((Number) o).intValue();
  }

  private static boolean bool(Object o) {
    return o instanceof Boolean b ? b : o instanceof Number n && n.intValue() != 0;
  }

  private static String limpio(String s) {
    return s == null ? "" : s.trim();
  }

  private static String corto(String s, int max) {
    String v = limpio(s);
    return v.length() > max ? v.substring(0, max) : v;
  }
}
