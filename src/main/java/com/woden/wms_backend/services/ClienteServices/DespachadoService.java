package com.woden.wms_backend.services.ClienteServices;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Despacho > Despachado: devolucion de lo que el cliente rechaza (misma logica que ReverseCore > Novedades).
 *
 * El destino sale del Work flow (origen DESPACHADO, opciones Opc_Equipos / Opc_Accesorios de "Despachado"), asi se
 * puede cambiar sin tocar codigo; hoy NOVEDAD y NOVEDAD-ACCESORIOS.
 *
 * Equipos (por serie, caja o pallet de despacho): la ULTIMA vuelta del serial (su SerialId) vuelve de Despacho a Ingreso
 * con su mismo Id, desempacada, sin pallet, en el destino, tipologia NO CONFORMIDAD y observacion
 * "RECHAZO CLIENTE <salida>" (en NOVEDAD aparece en Novedades > Proceso). Las vueltas anteriores no se tocan; un
 * despacho duplicado de esa vuelta se borra con ella.
 *
 * Accesorios (por cantidad): las unidades mas recientes de esa salida vuelven de DespachoAccesorio a Accesorio con su
 * mismo Id, en un pallet nuevo por referencia (NO CONFORMIDAD) con el destino elegido; en NOVEDAD-ACCESORIOS se envia
 * desde Novedades > Proceso (ACCESORIOS). Requiere la novedad de accesorios instalada en la base del cliente.
 *
 * Todo en una transaccion con verificacion final; lo borrado queda respaldado en WmsWdGeneral (DespachoReversa*).
 */
@Service
public class DespachadoService {

  private static final Logger logger = LoggerFactory.getLogger(DespachadoService.class);
  private static final String G = "WmsWdGeneral.dbo.";
  private static final String TIPOLOGIA = "NO CONFORMIDAD";
  private static final String OBSERVACION = "RECHAZO CLIENTE";
  private static final int MAX_SERIES = 3000;
  private static final int BLOQUE = 900;
  // destinos por Work flow: origen DESPACHADO, opciones de la pestana Despachado (catalogo Permiso)
  public static final String WF_ORIGEN = "DESPACHADO";
  public static final String WF_DESCRIPCION = "Despachado";
  public static final String WF_EQUIPOS = "Opc_Equipos";
  public static final String WF_ACCESORIOS = "Opc_Accesorios";

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private PalletRepository palletRepository;

  // ── equipos ───────────────────────────────────────────────────────────────

  /** Estado de cada serie: se regresa solo si su ultimo despacho es de esa salida y no esta en Ingreso. */
  public List<Map<String, Object>> previa(List<String> series, String salida) {
    String sal = limpio(salida).toUpperCase(Locale.ROOT);
    if (sal.isEmpty()) {
      throw new BusinessRuleException("Escriba la salida (PedidoSap) de las series rechazadas.");
    }
    List<String> lista = normalizar(series);
    if (lista.isEmpty()) {
      return List.of();
    }
    if (lista.size() > MAX_SERIES) {
      throw new BusinessRuleException("Máximo " + MAX_SERIES + " series por vez.");
    }

    // por bloques: SQL Server admite ~2100 parametros por consulta y un pallet puede traer cientos de series
    Map<String, Object[]> desp = new HashMap<>();
    Map<String, Object[]> ing = new HashMap<>();
    for (int i0 = 0; i0 < lista.size(); i0 += BLOQUE) {
      List<String> bloque = lista.subList(i0, Math.min(lista.size(), i0 + BLOQUE));
      String marcas = marcas(bloque.size(), 1);
      for (Object[] r : filas("SELECT d.Id, d.SerialId, d.Serial, d.PedidoSap, CONVERT(varchar(16), d.Fecha, 120), p.Numero, "
          + "cs.Descripcion, "
          + "(SELECT COUNT(*) FROM Empaque e WITH (NOLOCK) WHERE e.SerialId = d.SerialId), "
          + "(SELECT COUNT(*) FROM Despacho x WITH (NOLOCK) WHERE x.SerialId = d.SerialId), "
          + "(SELECT COUNT(DISTINCT ISNULL(x.PedidoSap, '')) FROM Despacho x WITH (NOLOCK) WHERE x.SerialId = d.SerialId), "
          + "(SELECT COUNT(*) FROM Despacho x WITH (NOLOCK) WHERE x.Serial = d.Serial AND x.SerialId <> d.SerialId) "
          + "FROM Despacho d WITH (NOLOCK) "
          + "INNER JOIN (SELECT Serial, MAX(Id) AS Id FROM Despacho WITH (NOLOCK) WHERE Serial IN (" + marcas + ") "
          + "GROUP BY Serial) u ON u.Id = d.Id "
          + "LEFT JOIN Pallet p WITH (NOLOCK) ON p.Id = d.PalletId "
          + "LEFT JOIN CodigoSap cs WITH (NOLOCK) ON cs.Id = d.CodigoSapId", bloque.toArray())) {
        desp.put(texto(r[2]).toUpperCase(Locale.ROOT), r);
      }
      for (Object[] r : filas("SELECT i.Serial, i.Id, e.Nombre, p.Numero FROM Ingreso i WITH (NOLOCK) "
          + "LEFT JOIN Estado e ON e.Id = i.EstadoId LEFT JOIN Pallet p ON p.Id = i.PalletId "
          + "WHERE i.Serial IN (" + marcas + ")", bloque.toArray())) {
        ing.put(texto(r[0]).toUpperCase(Locale.ROOT), r);
      }
    }

    List<Map<String, Object>> out = new ArrayList<>();
    for (String s : lista) {
      Object[] d = desp.get(s);
      Object[] i = ing.get(s);
      Map<String, Object> f = new LinkedHashMap<>();
      f.put("serial", s);
      f.put("ok", false);
      f.put("motivo", "");
      f.put("despachoId", d == null ? null : num(d[0]));
      f.put("serialId", d == null ? null : num(d[1]));
      f.put("pedido", d == null ? null : d[3]);
      f.put("fecha", d == null ? null : d[4]);
      f.put("pallet", d == null ? null : d[5]);
      f.put("modelo", d == null ? null : d[6]);
      f.put("empaques", d == null ? 0 : num(d[7]));
      f.put("registros", d == null ? 0 : num(d[8]));
      f.put("anteriores", d == null ? 0 : num(d[10]));
      if (i != null) {
        f.put("motivo", "Está en Ingreso (" + texto(i[2]) + ", pallet " + (i[3] == null ? "-" : texto(i[3])) + ")");
      } else if (d == null) {
        f.put("motivo", "No está despachada en este WMS");
      } else if (!texto(d[3]).toUpperCase(Locale.ROOT).equals(sal)) {
        f.put("motivo", "Su último despacho es de la salida " + (d[3] == null ? "-" : texto(d[3])));
      } else if (num(d[9]) > 1) {
        f.put("motivo", "Su último Id tiene despachos en salidas distintas: revisar");
      } else {
        f.put("ok", true);
      }
      out.add(f);
    }
    return out;
  }

  /**
   * Captura como en Ingreso: una serie, un pallet de despacho (todas sus series de esa salida) o una caja de despacho
   * (numero de caja dentro del pallet). Devuelve la previa de cada serie encontrada.
   */
  public List<Map<String, Object>> capturar(String salida, String tipo, String valor, String pallet) {
    String sal = limpio(salida).toUpperCase(Locale.ROOT);
    String v = limpio(valor).toUpperCase(Locale.ROOT);
    if (sal.isEmpty()) {
      throw new BusinessRuleException("Escriba la salida (PedidoSap).");
    }
    if (v.isEmpty()) {
      return List.of();
    }
    String t = limpio(tipo).toUpperCase(Locale.ROOT);
    List<String> series;
    if ("PALLET".equals(t)) {
      series = filas("SELECT DISTINCT d.Serial FROM Despacho d WITH (NOLOCK) JOIN Pallet p WITH (NOLOCK) ON p.Id = d.PalletId "
          + "WHERE p.Numero = ?1 AND d.PedidoSap = ?2", v, sal).stream().map(r -> texto(r[0])).collect(Collectors.toList());
      if (series.isEmpty()) {
        throw new BusinessRuleException("El pallet " + v + " no tiene series despachadas en la salida " + sal + ".");
      }
    } else if ("CAJA".equals(t)) {
      String pal = limpio(pallet).toUpperCase(Locale.ROOT);
      if (pal.isEmpty()) {
        throw new BusinessRuleException("Escriba el pallet de la caja.");
      }
      series = filas("SELECT DISTINCT d.Serial FROM Despacho d WITH (NOLOCK) JOIN Pallet p WITH (NOLOCK) ON p.Id = d.PalletId "
          + "JOIN CajaDespacho cd WITH (NOLOCK) ON cd.Id = d.CajaDespachoId "
          + "WHERE p.Numero = ?1 AND cd.Numero = ?2 AND d.PedidoSap = ?3", pal, v, sal)
          .stream().map(r -> texto(r[0])).collect(Collectors.toList());
      if (series.isEmpty()) {
        throw new BusinessRuleException("La caja " + v + " del pallet " + pal + " no tiene series despachadas en la salida "
            + sal + ".");
      }
    } else {
      series = List.of(v);
    }
    return previa(series, sal);
  }

  /** Destinos del Work flow para Despachado (opcion Opc_Equipos u Opc_Accesorios). */
  public List<String> destinos(String opcion) {
    return filas("EXEC pa_GetNameWorflowDestiny ?1, ?2, ?3, ?4", opcion, WF_ORIGEN, WF_DESCRIPCION, 0).stream()
        .map(r -> texto(r[0])).filter(s -> !s.isEmpty()).collect(Collectors.toList());
  }

  /** El destino elegido debe estar en el Work flow; devuelve su EstadoId. */
  private int destinoValido(String opcion, String destino) {
    String d = limpio(destino);
    if (d.isEmpty()) {
      throw new BusinessRuleException("Seleccione el destino.");
    }
    List<String> permitidos = destinos(opcion);
    if (permitidos.isEmpty()) {
      throw new BusinessRuleException("No hay destinos configurados en Work flow para Despachado (" + opcion + ").");
    }
    if (permitidos.stream().noneMatch(p -> p.equalsIgnoreCase(d))) {
      throw new BusinessRuleException("El destino " + d + " no está en el Work flow de Despachado.");
    }
    return estadoId(d);
  }

  /** Regresa la ultima vuelta de las series al destino del Work flow; si una falla o algo no cuadra no se regresa ninguna. */
  public List<Map<String, Object>> regresar(List<String> series, String salida, String destino, Integer usuarioId,
      String usuarioNombre, boolean simular) {
    exigirRespaldo();
    int usuario = usuarioId == null ? 0 : usuarioId;
    String db = baseActual();
    Integer tip = tipologiaId();
    if (tip == null) {
      throw new BusinessRuleException("El WMS de este cliente no tiene la tipología " + TIPOLOGIA + ".");
    }
    int estado = destinoValido(WF_EQUIPOS, destino);
    List<Map<String, Object>> filas = previa(series, salida);
    if (filas.isEmpty()) {
      throw new BusinessRuleException("Escriba al menos una serie.");
    }
    List<String> malas = filas.stream().filter(f -> !Boolean.TRUE.equals(f.get("ok")))
        .limit(10).map(f -> f.get("serial") + ": " + f.get("motivo")).collect(Collectors.toList());
    if (!malas.isEmpty()) {
      throw new BusinessRuleException("Revise las series: " + String.join("; ", malas));
    }
    String insertIngreso = insertIngresoDesdeDespacho();

    return plantilla().execute(status -> {
      for (Map<String, Object> f : filas) {
        String serial = String.valueOf(f.get("serial"));
        int serialId = num(f.get("serialId"));
        int despachoId = num(f.get("despachoId"));
        String obs = corto((OBSERVACION + " " + texto(f.get("pedido"))).trim(), 50);

        List<Object[]> vuelta = filas("SELECT Id, Serial FROM Despacho WITH (UPDLOCK) WHERE SerialId = ?1 ORDER BY Id", serialId);
        boolean igual = vuelta.size() == num(f.get("registros"))
            && vuelta.stream().anyMatch(v -> num(v[0]) == despachoId)
            && vuelta.stream().allMatch(v -> texto(v[1]).toUpperCase(Locale.ROOT).equals(serial));
        if (!igual) {
          throw new BusinessRuleException(serial + ": su despacho cambió mientras tanto; valide de nuevo. No se regresó ninguna serie.");
        }
        // respaldo de lo que se borra (la vuelta completa y su empaque)
        actualizar("INSERT INTO " + G + "DespachoReversa (ClienteId, Db, Serial, SerialId, DespachoId, PedidoSap, FechaDespacho, "
            + "PalletDespacho, Motivo, DespachoJson, EmpaqueJson, UsuarioId, UsuarioNombre) "
            + "SELECT ?1, ?2, ?3, ?4, ?5, d.PedidoSap, d.Fecha, p.Numero, ?6, "
            + "(SELECT * FROM Despacho x WHERE x.SerialId = ?4 ORDER BY x.Id FOR JSON PATH), "
            + "(SELECT * FROM Empaque e WHERE e.SerialId = ?4 FOR JSON PATH), NULLIF(?7, 0), ?8 "
            + "FROM Despacho d LEFT JOIN Pallet p ON p.Id = d.PalletId WHERE d.Id = ?5",
            cliente(), db, serial, serialId, despachoId, obs, usuario, corto(usuarioNombre, 150));

        // vuelve a Ingreso con su mismo Id (Empaque, Reparacion y Diagnostico siguen enlazados)
        actualizar("SET IDENTITY_INSERT Ingreso ON");
        try {
          int n = actualizar(insertIngreso, estado, tip, TIPOLOGIA, obs, usuario, despachoId);
          if (n != 1) {
            throw new BusinessRuleException(serial + ": no se pudo regresar a Ingreso. No se regresó ninguna serie.");
          }
        } finally {
          actualizar("SET IDENTITY_INSERT Ingreso OFF");
        }
        actualizar("DELETE FROM Empaque WHERE SerialId = ?1", serialId);
        // solo esta vuelta (con su despacho duplicado si lo hay); las anteriores tienen otro SerialId
        actualizar("DELETE FROM Despacho WHERE SerialId = ?1 AND Serial = ?2", serialId, serial);

        Object[] q = filas("SELECT (SELECT COUNT(*) FROM Despacho WHERE Serial = ?1), "
            + "(SELECT COUNT(*) FROM Despacho WHERE SerialId = ?2), (SELECT COUNT(*) FROM Ingreso WHERE Serial = ?1), "
            + "(SELECT COUNT(*) FROM Empaque WHERE SerialId = ?2)", serial, serialId).get(0);
        if (num(q[0]) != num(f.get("anteriores")) || num(q[1]) != 0 || num(q[2]) != 1 || num(q[3]) != 0) {
          throw new BusinessRuleException(serial + ": el WMS no quedó como se esperaba (despachos anteriores " + num(q[0])
              + " de " + num(f.get("anteriores")) + ", ingresos " + num(q[2]) + ", empaques " + num(q[3])
              + "). No se regresó ninguna serie.");
        }
      }
      if (simular) {
        status.setRollbackOnly();
      }
      logger.info("[Despachado] {} series de la salida {} a {} ({}){}", filas.size(), salida, destino, db,
          simular ? " (simulado, deshecho)" : "");
      return filas;
    });
  }

  /**
   * INSERT Ingreso ... SELECT ... FROM Despacho d WHERE d.Id = ?6, con las columnas que tenga esta base:
   * lo que no se fija aqui se copia de Despacho si existe con el mismo nombre.
   * Parametros: ?1 estado, ?2 tipologia, ?3 novedad, ?4 observacion, ?5 usuario, ?6 despacho.
   */
  private String insertIngresoDesdeDespacho() {
    Map<String, String> fijos = new HashMap<>();
    fijos.put("id", "d.SerialId");
    fijos.put("mac", "ISNULL(d.Mac, '')");
    fijos.put("palletid", "0");
    fijos.put("palletidingreso", "ISNULL(d.PalletIdIngreso, 0)");
    fijos.put("palletidalmacen", "0");
    fijos.put("palletidempaque", "0");
    fijos.put("cajaempaqueid", "0");
    fijos.put("cajadespachoid", "0");
    fijos.put("estadoid", "?1");
    fijos.put("tipologiaid", "?2");
    fijos.put("novedad", "?3");
    fijos.put("observaciones", "?4");
    fijos.put("usuarioidmovimiento", "?5");
    fijos.put("fechamovimiento", "GETDATE()");
    fijos.put("prealertaid", "ISNULL(d.PrealertaId, 0)");
    fijos.put("cruce", "ISNULL(d.Cruce, 0)");
    fijos.put("modeloid", "NULL"); // el modelo no viaja al despacho (un 0 rompe la FK)
    Set<String> despacho = columnas("Despacho");
    fijos.put("fecha", despacho.contains("fechaingreso") ? "ISNULL(d.FechaIngreso, d.Fecha)" : "d.Fecha");
    return insertCopiando("Ingreso", despacho, fijos, "FROM Despacho d WHERE d.Id = ?6");
  }

  // ── accesorios ────────────────────────────────────────────────────────────

  /** Accesorios despachados en esa salida que se pueden regresar, por referencia, tipo y estado de limpieza. */
  public List<Map<String, Object>> accPrevia(String salida) {
    String sal = limpio(salida).toUpperCase(Locale.ROOT);
    if (sal.isEmpty()) {
      throw new BusinessRuleException("Escriba la salida (PedidoSap) de los accesorios.");
    }
    // las unidades se cuentan por AccesoryId: hay salidas con el mismo accesorio registrado 2 o 3 veces
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] r : filas("SELECT d.CodigoSapId, cs.Codigo, cs.Descripcion, ISNULL(d.TipoAccesorio, ''), "
        + "ISNULL(d.EstadoLimpiezaId, 0), ISNULL(m.Codigo, ''), COUNT(DISTINCT d.AccesoryId), COUNT(*), "
        + "CONVERT(varchar(16), MAX(d.Fecha), 120) "
        + "FROM DespachoAccesorio d WITH (NOLOCK) LEFT JOIN CodigoSap cs WITH (NOLOCK) ON cs.Id = d.CodigoSapId "
        + "LEFT JOIN Maestro m WITH (NOLOCK) ON m.Id = d.EstadoLimpiezaId "
        + "WHERE d.PedidoSap = ?1 AND NOT EXISTS (SELECT 1 FROM Accesorio a WITH (NOLOCK) WHERE a.Id = d.AccesoryId) "
        + "GROUP BY d.CodigoSapId, cs.Codigo, cs.Descripcion, ISNULL(d.TipoAccesorio, ''), ISNULL(d.EstadoLimpiezaId, 0), "
        + "ISNULL(m.Codigo, '') ORDER BY cs.Codigo, 4, 6", sal)) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("codigoSapId", num(r[0]));
      m.put("codigoSap", r[1]);
      m.put("modelo", r[2]);
      m.put("tipo", r[3]);
      m.put("estadoLimpiezaId", num(r[4]));
      m.put("estadoLimpieza", r[5]);
      m.put("unidades", num(r[6]));
      m.put("registros", num(r[7]));
      m.put("fecha", r[8]);
      out.add(m);
    }
    if (out.isEmpty()) {
      throw new BusinessRuleException("La salida " + sal + " no tiene accesorios despachados para regresar.");
    }
    return out;
  }

  /** Regresa la cantidad pedida de cada grupo a un pallet nuevo (uno por grupo) con el destino del Work flow. Todo o nada. */
  public List<Map<String, Object>> accRegresar(String salida, List<Map<String, Object>> items, String destino,
      Integer usuarioId, String usuarioNombre, String zonaHoraria, boolean simular) {
    exigirRespaldo();
    String sal = limpio(salida).toUpperCase(Locale.ROOT);
    int usuario = usuarioId == null ? 0 : usuarioId;
    String db = baseActual();
    novedadAccesoriosLista();
    int estado = destinoValido(WF_ACCESORIOS, destino);
    int origen = estadoId("DESPACHADO");
    Integer tip = tipologiaId();
    if (tip == null) {
      throw new BusinessRuleException("El WMS de este cliente no tiene la tipología " + TIPOLOGIA + ".");
    }

    Map<String, Map<String, Object>> grupos = new HashMap<>();
    for (Map<String, Object> g : accPrevia(sal)) {
      grupos.put(clave(g.get("codigoSapId"), g.get("tipo"), g.get("estadoLimpiezaId")), g);
    }
    List<Map<String, Object>> pedir = new ArrayList<>();
    List<Integer> cantidades = new ArrayList<>();
    for (Map<String, Object> it : items == null ? List.<Map<String, Object>>of() : items) {
      int n = num(it.get("cantidad"));
      if (n <= 0) {
        continue;
      }
      Map<String, Object> g = grupos.get(clave(it.get("codigoSapId"), it.get("tipo"), it.get("estadoLimpiezaId")));
      if (g == null) {
        throw new BusinessRuleException("Los accesorios de la salida cambiaron; búsquela de nuevo.");
      }
      if (n > num(g.get("unidades"))) {
        throw new BusinessRuleException(g.get("codigoSap") + ": solo hay " + num(g.get("unidades")) + " despachados en esta salida.");
      }
      pedir.add(g);
      cantidades.add(n);
    }
    if (pedir.isEmpty()) {
      throw new BusinessRuleException("Escriba cuántos regresa de al menos un accesorio.");
    }
    String obs = corto(OBSERVACION + " " + sal, 100);
    // numeros de pallet FUERA de la transaccion: el SP bloquea el consecutivo global mientras dura la suya
    List<String> numeros = new ArrayList<>();
    for (int i = 0; i < pedir.size(); i++) {
      numeros.add(numeroPallet(zonaHoraria));
    }
    String insertAccesorio = insertAccesorioDesdeDespacho();

    return plantilla().execute(status -> {
      List<Map<String, Object>> out = new ArrayList<>();
      for (int k = 0; k < pedir.size(); k++) {
        Map<String, Object> g = pedir.get(k);
        int n = cantidades.get(k);
        String numero = numeros.get(k);
        int palletId = num(consulta("INSERT INTO Pallet (Numero, PosicionId, CodigoSapId, TipologiaId, OrigenId, DestinoId, "
            + "UsuarioId, Fecha, UsuarioIdModifica, FechaModifica, Activo, loteId) OUTPUT inserted.Id "
            + "VALUES (?1, 0, ?2, ?3, ?4, ?5, ?6, GETDATE(), ?6, GETDATE(), 1, NULL)",
            numero, num(g.get("codigoSapId")), tip, origen, estado, usuario).getSingleResult());

        List<Integer> ids = filas("SELECT TOP (" + n + ") d.AccesoryId, MAX(d.Id) FROM DespachoAccesorio d WITH (UPDLOCK) "
            + "WHERE d.PedidoSap = ?1 AND d.CodigoSapId = ?2 AND ISNULL(d.TipoAccesorio, '') = ?3 "
            + "AND ISNULL(d.EstadoLimpiezaId, 0) = ?4 AND NOT EXISTS (SELECT 1 FROM Accesorio a WHERE a.Id = d.AccesoryId) "
            + "GROUP BY d.AccesoryId ORDER BY MAX(d.Id) DESC",
            sal, num(g.get("codigoSapId")), texto(g.get("tipo")), num(g.get("estadoLimpiezaId")))
            .stream().map(r -> num(r[0])).collect(Collectors.toList());
        if (ids.size() != n) {
          throw new BusinessRuleException(g.get("codigoSap") + ": la salida cambió mientras tanto; búsquela de nuevo. No se regresó nada.");
        }
        String lista = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        actualizar("INSERT INTO " + G + "DespachoReversaAcc (ClienteId, Db, PedidoSap, CodigoSap, Modelo, TipoAccesorio, "
            + "EstadoLimpieza, Cantidad, AccesorioIds, Motivo, DespachoJson, PalletNovedad, UsuarioId, UsuarioNombre) "
            + "SELECT ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, "
            + "(SELECT * FROM DespachoAccesorio x WHERE x.PedidoSap = ?3 AND x.AccesoryId IN (" + lista + ") ORDER BY x.Id FOR JSON PATH), "
            + "?11, NULLIF(?12, 0), ?13",
            cliente(), db, sal, texto(g.get("codigoSap")), corto(texto(g.get("modelo")), 200), texto(g.get("tipo")),
            texto(g.get("estadoLimpieza")), n, "[" + lista + "]", obs, numero, usuario, corto(usuarioNombre, 150));

        // vuelven con su mismo Id (el historial de MovimientoAccesorio sigue enlazado) al pallet de novedad
        actualizar("SET IDENTITY_INSERT Accesorio ON");
        try {
          actualizar(insertAccesorio.replace("{IDS}", lista), palletId, estado, obs, usuario, TIPOLOGIA, sal);
        } finally {
          actualizar("SET IDENTITY_INSERT Accesorio OFF");
        }
        // todas las copias de esas unidades en la salida (despacho doble o triple)
        actualizar("DELETE FROM DespachoAccesorio WHERE PedidoSap = ?1 AND AccesoryId IN (" + lista + ")", sal);

        Object[] q = filas("SELECT (SELECT COUNT(*) FROM Accesorio WHERE Id IN (" + lista + ") AND EstadoId = ?1 "
            + "AND PalletId = ?2 AND Novedad = ?3), (SELECT COUNT(*) FROM DespachoAccesorio WHERE PedidoSap = ?4 "
            + "AND AccesoryId IN (" + lista + "))", estado, palletId, TIPOLOGIA, sal).get(0);
        if (num(q[0]) != n || num(q[1]) != 0) {
          throw new BusinessRuleException(g.get("codigoSap") + ": el WMS no quedó como se esperaba (en novedad " + num(q[0])
              + " de " + n + ", despachos que siguen " + num(q[1]) + "). No se regresó nada.");
        }
        Map<String, Object> r = new LinkedHashMap<>(g);
        r.put("regresados", n);
        r.put("pallet", numero);
        out.add(r);
      }
      if (simular) {
        status.setRollbackOnly();
      }
      logger.info("[Despachado] accesorios de la salida {} a {} ({} grupos, {}){}", sal, destino, out.size(), db,
          simular ? " (simulado, deshecho)" : "");
      return out;
    });
  }

  /**
   * INSERT Accesorio ... SELECT ... FROM DespachoAccesorio (ultima copia de cada unidad), con las columnas de esta base.
   * Parametros: ?1 pallet, ?2 estado, ?3 observacion, ?4 usuario, ?5 novedad, ?6 salida; {IDS} = lista de AccesoryId.
   */
  private String insertAccesorioDesdeDespacho() {
    Map<String, String> fijos = new HashMap<>();
    fijos.put("id", "d.AccesoryId");
    fijos.put("palletid", "?1");
    fijos.put("estadoid", "?2");
    fijos.put("estadolimpiezaid", "0");
    fijos.put("observacion", "?3");
    fijos.put("serialempaque", "'0'");
    fijos.put("caja", "1");
    fijos.put("fecha_movimiento", "GETDATE()");
    fijos.put("previouspalletid", "d.PalletId");
    fijos.put("prealertaid", "0");
    fijos.put("cruce", "0");
    fijos.put("usuarioidmovimiento", "?4");
    fijos.put("novedad", "?5");
    Set<String> despacho = columnas("DespachoAccesorio");
    fijos.put("fecha", despacho.contains("fechaingreso") ? "ISNULL(d.FechaIngreso, d.Fecha)" : "d.Fecha");
    return insertCopiando("Accesorio", despacho, fijos, "FROM DespachoAccesorio d WHERE d.Id IN (SELECT MAX(x.Id) "
        + "FROM DespachoAccesorio x WHERE x.PedidoSap = ?6 AND x.AccesoryId IN ({IDS}) GROUP BY x.AccesoryId)");
  }

  private void novedadAccesoriosLista() {
    Object[] r = filas("SELECT CASE WHEN COL_LENGTH('dbo.Accesorio', 'Novedad') IS NULL THEN 0 ELSE 1 END, "
        + "(SELECT COUNT(*) FROM sys.objects WHERE name = 'pa_SearchNoveltyAccesoryPallet'), "
        + "(SELECT COUNT(*) FROM Maestro m JOIN TipoMaestro t ON t.Id = m.TipoMaestroId "
        + "WHERE t.Nombre = 'Tipo Novedad Accesorios' AND m.Codigo = ?1 AND m.Activo = 1)", TIPOLOGIA).get(0);
    if (num(r[0]) == 0 || num(r[1]) == 0 || num(r[2]) == 0) {
      throw new BusinessRuleException("El WMS de este cliente no tiene la novedad de accesorios " + TIPOLOGIA
          + "; hay que instalarla primero.");
    }
  }

  private String numeroPallet(String zonaHoraria) {
    LocalDateTime ahora;
    try {
      ahora = LocalDateTime.now(ZoneId.of(limpio(zonaHoraria)));
    } catch (DateTimeException e) {
      ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));
    }
    String minuto = ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    for (int i = 0; i < 5; i++) {
      String numero = palletRepository.incrementarYObtenerConsecutivo(minuto);
      if (!palletRepository.existsByNumero(numero)) {
        return numero;
      }
    }
    throw new BusinessRuleException("No se pudo reservar un número de pallet; intente de nuevo.");
  }

  // ── historial ─────────────────────────────────────────────────────────────

  public List<Map<String, Object>> historial() {
    if (!respaldoListo()) {
      return List.of();
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (Object[] r : filas("SELECT TOP 200 h.* FROM ("
        + "SELECT r.Fecha, 'Equipo' AS Tipo, r.Serial AS Item, 1 AS Cantidad, r.PedidoSap, r.PalletDespacho, "
        + "CAST(NULL AS varchar(30)) AS PalletNovedad, r.UsuarioNombre FROM " + G + "DespachoReversa r WHERE r.ClienteId = ?1 "
        + "UNION ALL SELECT a.Fecha, 'Accesorio', CONCAT(a.CodigoSap, ' · ', a.Modelo), a.Cantidad, a.PedidoSap, NULL, "
        + "a.PalletNovedad, a.UsuarioNombre FROM " + G + "DespachoReversaAcc a WHERE a.ClienteId = ?1) h "
        + "ORDER BY h.Fecha DESC", cliente())) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("fecha", r[0] == null ? null : String.valueOf(r[0]).substring(0, Math.min(16, String.valueOf(r[0]).length())));
      m.put("tipo", r[1]);
      m.put("item", r[2]);
      m.put("cantidad", num(r[3]));
      m.put("pedidoSap", r[4]);
      m.put("palletDespacho", r[5]);
      m.put("palletNovedad", r[6]);
      m.put("usuario", r[7]);
      out.add(m);
    }
    return out;
  }

  // ── utilidades ────────────────────────────────────────────────────────────

  /** INSERT destino (cols) SELECT exprs desde: fijos manda; si no, la columna de origen con el mismo nombre. */
  private String insertCopiando(String destino, Set<String> origen, Map<String, String> fijos, String desde) {
    List<String> cols = new ArrayList<>();
    List<String> exprs = new ArrayList<>();
    for (Object[] c : filas("SELECT c.name FROM sys.columns c WHERE c.object_id = OBJECT_ID(?1) AND c.is_computed = 0 "
        + "ORDER BY c.column_id", "dbo." + destino)) {
      String nombre = texto(c[0]);
      String k = nombre.toLowerCase(Locale.ROOT);
      String expr = fijos.containsKey(k) ? fijos.get(k) : origen.contains(k) ? "d.[" + nombre + "]" : null;
      if (expr != null) {
        cols.add("[" + nombre + "]");
        exprs.add(expr);
      }
    }
    return "INSERT INTO " + destino + " (" + String.join(", ", cols) + ") SELECT " + String.join(", ", exprs) + " " + desde;
  }

  private Set<String> columnas(String tabla) {
    return filas("SELECT c.name FROM sys.columns c WHERE c.object_id = OBJECT_ID(?1)", "dbo." + tabla).stream()
        .map(r -> texto(r[0]).toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Integer tipologiaId() {
    List<Object[]> r = filas("SELECT TOP 1 m.Id FROM Maestro m JOIN TipoMaestro t ON t.Id = m.TipoMaestroId "
        + "WHERE t.Nombre = 'Tipologias' AND m.Codigo = ?1 AND m.Activo = 1 ORDER BY m.Id", TIPOLOGIA);
    return r.isEmpty() ? null : num(r.get(0)[0]);
  }

  private int estadoId(String nombre) {
    List<Object[]> r = filas("SELECT TOP 1 Id FROM Estado WHERE Nombre = ?1", nombre);
    if (r.isEmpty()) {
      throw new BusinessRuleException("El WMS no tiene el estado " + nombre + ".");
    }
    return num(r.get(0)[0]);
  }

  private boolean respaldoListo() {
    List<Object[]> r = filas("SELECT CASE WHEN OBJECT_ID('WmsWdGeneral.dbo.DespachoReversa', 'U') IS NULL "
        + "OR OBJECT_ID('WmsWdGeneral.dbo.DespachoReversaAcc', 'U') IS NULL THEN 0 ELSE 1 END");
    return !r.isEmpty() && num(r.get(0)[0]) == 1;
  }

  private void exigirRespaldo() {
    if (!respaldoListo()) {
      throw new BusinessRuleException("Falta instalar el respaldo de devoluciones en la base (script "
          + "2026-10-25_despachado_reversa.sql).");
    }
  }

  private TransactionTemplate plantilla() {
    TransactionTemplate t = new TransactionTemplate(transactionManager);
    t.setTimeout(600); // un pallet completo puede traer cientos de series
    return t;
  }

  private String baseActual() {
    return texto(filas("SELECT DB_NAME()").get(0)[0]);
  }

  private int cliente() {
    Integer id = ClientDatabaseContext.getCurrentClientId();
    return id == null ? 0 : id;
  }

  private static String clave(Object codigoSapId, Object tipo, Object estadoLimpiezaId) {
    return num(codigoSapId) + "|" + texto(tipo) + "|" + num(estadoLimpiezaId);
  }

  private static List<String> normalizar(List<String> series) {
    LinkedHashSet<String> out = new LinkedHashSet<>();
    for (String s : series == null ? List.<String>of() : series) {
      for (String p : String.valueOf(s).replace(',', '\n').split("\\R")) {
        String v = p.trim().toUpperCase(Locale.ROOT);
        if (!v.isEmpty()) {
          out.add(v);
        }
      }
    }
    return new ArrayList<>(out);
  }

  private static String marcas(int n, int desde) {
    List<String> m = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      m.add("?" + (desde + i));
    }
    return String.join(", ", m);
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

  private static String limpio(String s) {
    return s == null ? "" : s.trim();
  }

  private static String corto(String s, int max) {
    String v = limpio(s);
    return v.length() > max ? v.substring(0, max) : v;
  }
}
