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

/**
 * Boletas de movimiento de Despacho guardadas (Reservar / Despachar / Cancelar) y el documento consecutivo AAAA-NNN.
 *
 * Las tablas viven en WmsWdGeneral por ClienteId (sql/2026-10-24_boleta_reservas_consecutivo.sql). Mientras la boleta
 * esta RESERVADA sus pallets no se pueden enviar, despachar, abrir ni eliminar: hay que despacharla o cancelarla.
 * Estados: RESERVADA, DESPACHANDO (el front esta despachando sus pallets), DESPACHADA, CANCELADA.
 */
@Service
public class BoletaReservaService {

  private static final Logger logger = LoggerFactory.getLogger(BoletaReservaService.class);
  private static final String G = "WmsWdGeneral.dbo.";
  private static final String CONSECUTIVO = "BOLETA_CONSECUTIVO";

  // las tablas pueden no existir aun (script sin correr): el bloqueo no debe tumbar el despacho normal
  private static volatile boolean tablasListas = false;

  @PersistenceContext
  private EntityManager em;

  // ── consecutivo ───────────────────────────────────────────────────────────

  public boolean usaConsecutivo() {
    Integer n = entero("SELECT COUNT(*) FROM " + G + "ClienteValidacion cv JOIN " + G
        + "ClienteValidacionTipo t ON t.Id = cv.ValidacionTipoId "
        + "WHERE cv.ClienteId = ?1 AND t.Codigo = ?2 AND cv.Activo = 1 AND t.Activo = 1", cliente(), CONSECUTIVO);
    return n != null && n > 0;
  }

  /** Configuracion para el modal: si usa consecutivo y el proximo documento (sin consumirlo). */
  public Map<String, Object> config(String zonaHoraria) {
    Map<String, Object> out = new LinkedHashMap<>();
    boolean consecutivo = tablas() && usaConsecutivo();
    out.put("consecutivo", consecutivo);
    if (consecutivo) {
      int anio = anio(zonaHoraria);
      Integer ultimo = entero("SELECT Ultimo FROM " + G + "BoletaConsecutivo WHERE ClienteId = ?1 AND Anio = ?2",
          cliente(), anio);
      out.put("siguiente", formato(anio, (ultimo == null ? 0 : ultimo) + 1));
    }
    return out;
  }

  /** Último documento del año (Administración > Documentos > Boleta de entrega): AAAA-NNN y el siguiente. */
  public Map<String, Object> consecutivo(String zonaHoraria) {
    exigirTablas();
    int anio = anio(zonaHoraria);
    Integer ultimo = entero("SELECT Ultimo FROM " + G + "BoletaConsecutivo WHERE ClienteId = ?1 AND Anio = ?2",
        cliente(), anio);
    Integer usado = entero("SELECT MAX(Consecutivo) FROM " + G + "BoletaMovimiento WHERE ClienteId = ?1 AND Anio = ?2",
        cliente(), anio);
    int u = ultimo == null ? 0 : ultimo;
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("activo", usaConsecutivo());
    out.put("anio", anio);
    out.put("ultimo", u);
    out.put("ultimoDocumento", u == 0 ? "" : formato(anio, u));
    out.put("siguiente", formato(anio, u + 1));
    out.put("minimo", usado == null ? 0 : usado);
    return out;
  }

  /**
   * Fija el último consecutivo del año (cuando la numeración ya venía de antes): el siguiente documento será
   * ultimo + 1. No puede quedar por debajo de una boleta ya guardada (se repetiría el documento).
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> fijarConsecutivo(int ultimo, String zonaHoraria) {
    exigirTablas();
    if (ultimo < 0 || ultimo > 999999) {
      throw new BusinessRuleException("El consecutivo debe estar entre 0 y 999999.");
    }
    int anio = anio(zonaHoraria);
    Integer usado = entero("SELECT MAX(Consecutivo) FROM " + G + "BoletaMovimiento WHERE ClienteId = ?1 AND Anio = ?2",
        cliente(), anio);
    if (usado != null && ultimo < usado) {
      throw new BusinessRuleException("Ya existe la boleta " + formato(anio, usado) + ": el último consecutivo no puede "
          + "ser menor.");
    }
    actualizar("MERGE " + G + "BoletaConsecutivo WITH (HOLDLOCK) AS t "
        + "USING (SELECT ?1 AS ClienteId, ?2 AS Anio) AS s ON t.ClienteId = s.ClienteId AND t.Anio = s.Anio "
        + "WHEN MATCHED THEN UPDATE SET Ultimo = ?3 "
        + "WHEN NOT MATCHED THEN INSERT (ClienteId, Anio, Ultimo) VALUES (s.ClienteId, s.Anio, ?3);", cliente(), anio, ultimo);
    return consecutivo(zonaHoraria);
  }

  private int tomarConsecutivo(int cliente, int anio) {
    Object r = consulta("MERGE " + G + "BoletaConsecutivo WITH (HOLDLOCK) AS t "
        + "USING (SELECT ?1 AS ClienteId, ?2 AS Anio) AS s ON t.ClienteId = s.ClienteId AND t.Anio = s.Anio "
        + "WHEN MATCHED THEN UPDATE SET Ultimo = t.Ultimo + 1 "
        + "WHEN NOT MATCHED THEN INSERT (ClienteId, Anio, Ultimo) VALUES (s.ClienteId, s.Anio, 1) "
        + "OUTPUT inserted.Ultimo;", cliente, anio).getSingleResult();
    return num(r);
  }

  // ── reservar ──────────────────────────────────────────────────────────────

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> reservar(BoletaMovimientoDTO r) {
    exigirTablas();
    // boletas guardadas y documento consecutivo van juntos (interruptor BOLETA_CONSECUTIVO del cliente)
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

    List<Object[]> ocupados = filas("SELECT TOP 1 p.NoPallet, b.Documento FROM " + G + "BoletaMovimientoPallet p JOIN "
        + G + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = 1 AND p.PalletId IN ("
        + enLista(ids) + ")", cliente);
    if (!ocupados.isEmpty()) {
      throw new BusinessRuleException("El pallet " + ocupados.get(0)[0] + " ya está en la boleta " + ocupados.get(0)[1]
          + ". Despáchela o cancele esa reserva.");
    }

    String documento;
    int anio = 0;
    int consecutivo = 0;
    if (usaConsecutivo()) {
      anio = anio(r.getZonaHoraria());
      consecutivo = tomarConsecutivo(cliente, anio);
      documento = formato(anio, consecutivo);
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
        + "FechaEntrega, Origen, Destino, Observaciones, Estado, UsuarioId, UsuarioNombre, Fecha) OUTPUT inserted.Id "
        + "VALUES (?1, ?2, NULLIF(?3, 0), NULLIF(?4, 0), TRY_CONVERT(date, NULLIF(?5, '')), ?6, ?7, NULLIF(?8, ''), "
        + "'RESERVADA', NULLIF(?9, 0), ?10, GETDATE())",
        cliente, documento, anio, consecutivo, limpio(r.getFechaEntrega()), corto(r.getOrigen(), 100),
        corto(r.getDestino(), 100), corto(r.getObservacionesGenerales(), 1000),
        r.getUsuarioId() == null ? 0 : r.getUsuarioId(), corto(r.getUsuarioNombre(), 150)).getSingleResult());

    LinkedHashSet<Integer> vistos = new LinkedHashSet<>();
    for (BoletaMovimientoDTO.ItemDTO it : items) {
      if (it.getId() == null || !vistos.add(it.getId())) {
        continue;
      }
      actualizar("INSERT INTO " + G + "BoletaMovimientoPallet (BoletaId, ClienteId, PalletId, NoPallet, CodigoSap, "
          + "Descripcion, Cantidad, Tipologia, Activo, Despachado) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, 1, 0)",
          boletaId, cliente, it.getId(), corto(it.getNoPallet(), 50), corto(it.getCodigoSap(), 50),
          corto(it.getDescripcion(), 200), it.getCantidad() == null ? 0 : it.getCantidad(), corto(it.getTipologia(), 100));
    }
    logger.info("[Boleta] reservada {} cliente {} pallets {}", documento, cliente, vistos.size());
    return detalle(boletaId);
  }

  // ── consultar ─────────────────────────────────────────────────────────────

  public List<Map<String, Object>> listar(String estado, String texto) {
    if (!tablas()) {
      return List.of();
    }
    String est = limpio(estado).toUpperCase();
    List<Object[]> rs = filas("SELECT TOP 300 b.Id, b.Documento, CONVERT(varchar(10), b.FechaEntrega, 23), b.Origen, "
        + "b.Destino, b.Estado, b.UsuarioNombre, CONVERT(varchar(16), b.Fecha, 120), "
        + "(SELECT COUNT(*) FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id), "
        + "(SELECT ISNULL(SUM(p.Cantidad), 0) FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id), "
        + "CONVERT(varchar(16), b.FechaDespacho, 120) "
        + "FROM " + G + "BoletaMovimiento b WHERE b.ClienteId = ?1 "
        + "AND (?2 = '' OR b.Estado = ?2 OR (?2 = 'RESERVADA' AND b.Estado = 'DESPACHANDO')) "
        + "AND (?3 = '' OR b.Documento LIKE '%' + ?3 + '%' OR EXISTS (SELECT 1 FROM " + G
        + "BoletaMovimientoPallet p WHERE p.BoletaId = b.Id AND p.NoPallet LIKE '%' + ?3 + '%')) "
        + "ORDER BY b.Fecha DESC", cliente(), est, limpio(texto));
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
    List<Object[]> cab = filas("SELECT b.Id, b.Documento, CONVERT(varchar(10), b.FechaEntrega, 23), b.Origen, b.Destino, "
        + "b.Observaciones, b.Estado, b.UsuarioNombre, CONVERT(varchar(16), b.Fecha, 120), "
        + "CONVERT(varchar(16), b.FechaDespacho, 120), CASE WHEN b.Consecutivo IS NULL THEN 0 ELSE 1 END "
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
    for (Object[] p : filas("SELECT p.PalletId, p.NoPallet, p.CodigoSap, p.Descripcion, p.Cantidad, p.Tipologia, "
        + "p.Activo, p.Despachado FROM " + G + "BoletaMovimientoPallet p WHERE p.BoletaId = ?1 ORDER BY p.Id", boletaId)) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", num(p[0]));
      m.put("noPallet", p[1]);
      m.put("codigoSap", p[2]);
      m.put("descripcion", p[3]);
      m.put("cantidad", num(p[4]));
      m.put("tipologia", p[5]);
      m.put("activo", bool(p[6]));
      m.put("despachado", bool(p[7]));
      pallets.add(m);
    }
    out.put("pallets", pallets);
    return out;
  }

  /** Pallets bloqueados por boletas abiertas del cliente (para el candado en Despacho). */
  public List<Map<String, Object>> reservados() {
    if (!tablas()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] f : filas("SELECT p.PalletId, b.Id, b.Documento, b.Estado FROM " + G + "BoletaMovimientoPallet p JOIN "
        + G + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = 1", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("palletId", num(f[0]));
      m.put("boletaId", num(f[1]));
      m.put("documento", f[2]);
      m.put("estado", f[3]);
      out.add(m);
    }
    return out;
  }

  // ── despachar / cancelar ──────────────────────────────────────────────────

  /** El front va a despachar los pallets: mientras tanto la reserva no los bloquea. */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> iniciarDespacho(int boletaId) {
    String estado = estado(boletaId);
    if (!"RESERVADA".equals(estado) && !"DESPACHANDO".equals(estado)) {
      throw new BusinessRuleException("La boleta está " + estado.toLowerCase() + ".");
    }
    actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'DESPACHANDO' WHERE Id = ?1", boletaId);
    return detalle(boletaId);
  }

  /**
   * Cierra el despacho segun lo que quedo en la base: un pallet de la boleta inactivo (Pallet.Activo = 0) ya salio.
   * Si salieron todos la boleta queda DESPACHADA; si no, vuelve a RESERVADA con los que faltan bloqueados.
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> finalizarDespacho(int boletaId, Integer usuarioId) {
    String estado = estado(boletaId);
    if (!"DESPACHANDO".equals(estado) && !"RESERVADA".equals(estado)) {
      throw new BusinessRuleException("La boleta está " + estado.toLowerCase() + ".");
    }
    marcarDespachados(boletaId);
    Integer pendientes = entero("SELECT COUNT(*) FROM " + G + "BoletaMovimientoPallet WHERE BoletaId = ?1 AND Despachado = 0",
        boletaId);
    if (pendientes == null || pendientes == 0) {
      actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'DESPACHADA', FechaDespacho = GETDATE(), "
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
    actualizar("UPDATE " + G + "BoletaMovimientoPallet SET Activo = 0 WHERE BoletaId = ?1", boletaId);
    actualizar("UPDATE " + G + "BoletaMovimiento SET Estado = 'CANCELADA', FechaCancela = GETDATE(), "
        + "UsuarioCancelaId = NULLIF(?2, 0) WHERE Id = ?1", boletaId, usuarioId == null ? 0 : usuarioId);
    logger.info("[Boleta] cancelada {} cliente {}", boletaId, cliente());
    return detalle(boletaId);
  }

  private void marcarDespachados(int boletaId) {
    // Pallet esta en la base del cliente (conexion actual); la boleta en WmsWdGeneral
    actualizar("UPDATE bp SET Despachado = 1, Activo = 0 FROM " + G + "BoletaMovimientoPallet bp "
        + "JOIN Pallet p ON p.Id = bp.PalletId WHERE bp.BoletaId = ?1 AND bp.Despachado = 0 AND ISNULL(p.Activo, 0) = 0",
        boletaId);
  }

  // ── bloqueo ───────────────────────────────────────────────────────────────

  /** Lanza error si alguno de los pallets esta en una boleta RESERVADA. */
  public void validarPalletsLibres(Collection<Integer> palletIds) {
    List<Integer> ids = palletIds == null ? List.of()
        : palletIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    if (ids.isEmpty() || !tablas()) {
      return;
    }
    List<Object[]> r = filas("SELECT TOP 1 p.NoPallet, b.Documento FROM " + G + "BoletaMovimientoPallet p JOIN " + G
        + "BoletaMovimiento b ON b.Id = p.BoletaId WHERE p.ClienteId = ?1 AND p.Activo = 1 AND b.Estado = 'RESERVADA' "
        + "AND p.PalletId IN (" + enLista(ids) + ")", cliente());
    if (!r.isEmpty()) {
      throw new BusinessRuleException("El pallet " + r.get(0)[0] + " está reservado en la boleta " + r.get(0)[1]
          + ". Despache la boleta o cancele la reserva para liberarlo.");
    }
  }

  // ── utilidades ────────────────────────────────────────────────────────────

  private boolean tablas() {
    if (!tablasListas) {
      Integer n = entero("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.BoletaMovimientoPallet', 'U') IS NULL "
          + "OR OBJECT_ID('WmsWdGeneral.dbo.BoletaConsecutivo', 'U') IS NULL THEN 0 ELSE 1 END");
      tablasListas = n != null && n == 1;
    }
    return tablasListas;
  }

  private void exigirTablas() {
    if (!tablas()) {
      throw new BusinessRuleException("Las boletas guardadas aún no están instaladas en la base (falta el script "
          + "2026-10-24_boleta_reservas_consecutivo.sql).");
    }
  }

  private String estado(int boletaId) {
    exigirTablas();
    List<Object[]> r = filas("SELECT Estado, Id FROM " + G + "BoletaMovimiento WITH (UPDLOCK) WHERE Id = ?1 AND ClienteId = ?2",
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

  private static int anio(String zonaHoraria) {
    try {
      return LocalDate.now(ZoneId.of(limpio(zonaHoraria))).getYear();
    } catch (DateTimeException e) {
      return LocalDate.now(ZoneId.of("America/Bogota")).getYear();
    }
  }

  private static String formato(int anio, int consecutivo) {
    return anio + "-" + String.format("%03d", consecutivo);
  }

  private static String enLista(List<Integer> ids) {
    // solo enteros: no hay riesgo de inyeccion
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
