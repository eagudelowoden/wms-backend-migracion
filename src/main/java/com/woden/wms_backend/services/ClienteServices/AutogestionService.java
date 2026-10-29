package com.woden.wms_backend.services.ClienteServices;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.AutogestionSolicitudDTO;
import com.woden.wms_backend.repositories.ClienteRepositories.AutogestionRepository;

@Service
public class AutogestionService {
  private static final Set<String> ACCIONES_SERIAL = Set.of("ESTADO", "CAJA", "PALLET");
  private static final Set<String> ACCIONES_PALLET = Set.of("MOVER", "CAJA_VACIAR", "CAJA_ELIMINAR", "CAJA_PALLET");
  private static final SecureRandom RANDOM = new SecureRandom();

  @Autowired
  private AutogestionRepository repository;

  @Autowired
  private AutogestionCorreoService correoService;

  @Value("${autogestion.frontend-url:http://localhost:4200}")
  private String frontendUrl;

  /** Correos que reciben el enlace de aprobación, separados por coma o punto y coma. */
  @Value("${autogestion.aprobadores.correos:}")
  private String correosAprobadores;

  @Value("${autogestion.solicitud.horas:48}")
  private int horasVigencia;

  // ── Consultas ──────────────────────────────────────────────────────────

  public List<Map<String, Object>> estados(String ambito) {
    String filtro = "PALLET".equalsIgnoreCase(ambito) ? "PALLET" : "SERIAL";
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] row : repository.estados(filtro)) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", row[0]);
      map.put("nombre", row[1]);
      result.add(map);
    }
    return result;
  }

  public Map<String, Object> buscarSerial(String serial) {
    List<Object[]> rows = repository.buscarSerial(serial.trim());
    if (rows.isEmpty()) {
      return null;
    }
    Object[] r = rows.get(0);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r[0]);
    map.put("serial", r[1]);
    map.put("mac", r[2]);
    map.put("codigoSap", r[3]);
    map.put("descripcion", r[4]);
    map.put("estadoId", r[5]);
    map.put("estado", r[6]);
    map.put("palletId", r[7]);
    map.put("pallet", r[8]);
    map.put("cajaEmpaqueId", r[9]);
    map.put("cajaEmpaque", r[10]);
    map.put("modulo", r[11]);
    map.put("seccion", r[12]);
    map.put("esProduccion", r[13] != null && ((Number) r[13]).intValue() == 1);
    map.put("esScrap", r[14] != null && ((Number) r[14]).intValue() == 1);
    map.put("empacado", r[15] != null && ((Number) r[15]).intValue() > 0);
    return map;
  }

  public Map<String, Object> buscarPallet(String pallet) {
    List<Object[]> rows = repository.buscarPallet(pallet.trim());
    if (rows.isEmpty()) {
      return null;
    }
    Object[] r = rows.get(0);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r[0]);
    map.put("numero", r[1]);
    map.put("codigoSap", r[2]);
    map.put("descripcion", r[3]);
    map.put("estadoId", r[4]);
    map.put("estado", r[5]);
    map.put("modulo", r[6]);
    map.put("seccion", r[7]);
    map.put("esProduccion", r[8] != null && ((Number) r[8]).intValue() == 1);
    map.put("totalSeriales", r[9]);
    map.put("totalCajas", r[10]);
    map.put("serialesScrap", r[11]);
    map.put("serialesEmpacados", r[12]);
    List<Map<String, Object>> cajas = new ArrayList<>();
    for (Object[] c : repository.palletCajas(((Number) r[0]).intValue())) {
      Map<String, Object> caja = new LinkedHashMap<>();
      caja.put("id", c[0]);
      caja.put("numero", c[1]);
      caja.put("cantidad", c[2]);
      cajas.add(caja);
    }
    map.put("cajas", cajas);
    return map;
  }

  public List<Map<String, Object>> logs(String serial, Integer limite) {
    String filtro = (serial == null || serial.isBlank()) ? null : serial.trim();
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] r : repository.logs(filtro, limite == null ? 200 : limite)) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", r[0]);
      map.put("fecha", r[1] == null ? null : r[1].toString());
      map.put("usuario", r[2]);
      map.put("accion", r[3]);
      map.put("serial", r[4]);
      map.put("estadoOrigen", r[5]);
      map.put("estadoDestino", r[6]);
      map.put("palletOrigen", r[7]);
      map.put("palletDestino", r[8]);
      map.put("cajaOrigen", r[9]);
      map.put("justificacion", r[10]);
      map.put("cantidad", r[11]);
      result.add(map);
    }
    return result;
  }

  public List<Map<String, Object>> solicitudes(Integer limite) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] r : repository.solicitudes(limite == null ? 100 : limite)) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", r[0]);
      map.put("fecha", str(r[1]));
      map.put("ambito", r[2]);
      map.put("accion", r[3]);
      map.put("objetivo", r[4]);
      map.put("estadoDestino", r[5]);
      map.put("solicitante", r[6]);
      map.put("estado", r[7]);
      map.put("venceEn", str(r[8]));
      map.put("resueltaPor", r[9]);
      map.put("fechaResolucion", str(r[10]));
      map.put("resultado", r[11]);
      map.put("justificacion", r[12]);
      result.add(map);
    }
    return result;
  }

  // ── Solicitud (usuario autenticado) ─────────────────────────────────────

  /**
   * Valida en seco, crea la solicitud y envía un enlace de un solo uso a cada
   * aprobador. No mueve nada hasta que alguno autorice.
   */
  public Map<String, Object> crearSolicitud(AutogestionSolicitudDTO dto) {
    validar(dto);
    Integer clienteId = ClientDatabaseContext.getCurrentClientId();
    if (clienteId == null || clienteId <= 0) {
      throw new IllegalStateException("No se pudo identificar el cliente de la sesión.");
    }

    List<Object[]> aprobadores = aprobadoresConfigurados();
    if (aprobadores.isEmpty()) {
      throw new IllegalStateException(
          "No hay correos de aprobadores configurados: defina autogestion.aprobadores.correos en application.properties.");
    }

    Integer solicitudId = repository.crearSolicitud(
        dto.getAmbito(), dto.getAccion(), dto.getObjetivo().trim(), dto.getEstadoDestinoId(),
        vacioANulo(dto.getCajaNumero()), vacioANulo(dto.getPalletDestino()),
        dto.getUsuarioId(), dto.getJustificacion().trim(), horasVigencia);

    int enviados = 0;
    try {
      enviados = generarEnlaces(dto, clienteId, solicitudId, aprobadores);
    } catch (RuntimeException e) {
      // No dejar una solicitud "pendiente" sin enlaces: bloquearía reintentos.
      try {
        repository.cancelarSolicitud(solicitudId, "No se pudo generar/enviar los enlaces: " + e.getMessage());
      } catch (RuntimeException ignorada) {
        // si también falla la cancelación, se propaga el error original
      }
      throw e;
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("solicitudId", solicitudId);
    result.put("aprobadores", aprobadores.size());
    result.put("correosEnviados", enviados);
    return result;
  }

  /** Crea un token por aprobador, guarda su hash y envía el correo. Devuelve cuántos correos salieron. */
  private int generarEnlaces(AutogestionSolicitudDTO dto, Integer clienteId, Integer solicitudId,
      List<Object[]> aprobadores) {
    String resumen = resumen(dto.getAmbito(), dto.getAccion(), dto.getObjetivo().trim(),
        nombreEstado(dto.getAmbito(), dto.getEstadoDestinoId()), dto.getCajaNumero(), dto.getPalletDestino());
    String solicitante = null;

    int enviados = 0;
    for (Object[] a : aprobadores) {
      Integer aprobadorId = ((Number) a[0]).intValue();
      String nombre = a[1] == null ? "" : a[1].toString();
      String correo = a[2] == null ? "" : a[2].toString();

      String token = nuevoToken();
      repository.guardarToken(solicitudId, aprobadorId, nombre, correo, hash(token));
      if (solicitante == null) {
        List<Object[]> detalle = repository.solicitudPorToken(hash(token));
        solicitante = detalle.isEmpty() || detalle.get(0)[7] == null
            ? "Usuario " + dto.getUsuarioId()
            : detalle.get(0)[7].toString();
      }

      String enlace = frontendUrl.replaceAll("/+$", "") + "/autorizar/" + clienteId + "/" + token;
      if (correoService.enviarSolicitud(correo, nombre, solicitante, resumen,
          dto.getJustificacion().trim(), enlace, horasVigencia)) {
        enviados++;
      }
    }
    return enviados;
  }

  // ── Aprobación pública (sin sesión; el token es la credencial) ──────────

  public Map<String, Object> detalleSolicitudPublica(Integer clienteId, String token) {
    List<Object[]> rows = repository.solicitudPorToken(hash(token));
    if (rows.isEmpty()) {
      return null;
    }
    Object[] r = rows.get(0);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r[0]);
    map.put("resumen", resumen(String.valueOf(r[1]), String.valueOf(r[2]), String.valueOf(r[3]),
        r[4] == null ? null : r[4].toString(), r[5] == null ? null : r[5].toString(),
        r[6] == null ? null : r[6].toString()));
    map.put("solicitante", r[7]);
    map.put("fecha", str(r[8]));
    map.put("justificacion", r[9]);
    map.put("estado", r[10]);
    map.put("venceEn", str(r[11]));
    map.put("aprobador", r[12]);
    map.put("vigente", r[13] != null && ((Number) r[13]).intValue() == 1);
    return map;
  }

  public Map<String, Object> resolverSolicitudPublica(Integer clienteId, String token, boolean aprobar) {
    List<Object[]> rows = repository.resolverSolicitud(hash(token), aprobar ? "APROBAR" : "RECHAZAR");
    Map<String, Object> map = new LinkedHashMap<>();
    if (rows.isEmpty()) {
      map.put("resultado", "ERROR");
      map.put("mensaje", "No se obtuvo respuesta del servidor.");
    } else {
      map.put("resultado", rows.get(0)[0]);
      map.put("mensaje", rows.get(0)[1]);
    }
    return map;
  }

  // ── Utilidades ──────────────────────────────────────────────────────────


  private void validar(AutogestionSolicitudDTO dto) {
    boolean serial = "SERIAL".equals(dto.getAmbito());
    boolean pallet = "PALLET".equals(dto.getAmbito());
    if (!serial && !pallet) {
      throw new IllegalArgumentException("Ámbito no válido.");
    }
    if (dto.getObjetivo() == null || dto.getObjetivo().isBlank()) {
      throw new IllegalArgumentException(serial ? "El serial es obligatorio." : "El pallet es obligatorio.");
    }
    if (dto.getUsuarioId() == null || dto.getUsuarioId() <= 0) {
      throw new IllegalArgumentException("No se pudo identificar el usuario.");
    }
    Set<String> permitidas = serial ? ACCIONES_SERIAL : ACCIONES_PALLET;
    if (dto.getAccion() == null || !permitidas.contains(dto.getAccion())) {
      throw new IllegalArgumentException("Acción no válida.");
    }
    if (dto.getJustificacion() == null || dto.getJustificacion().trim().length() < 10) {
      throw new IllegalArgumentException("La justificación es obligatoria (mínimo 10 caracteres).");
    }
  }

  private String resumen(String ambito, String accion, String objetivo, String estadoDestino,
      String caja, String palletDestino) {
    if ("SERIAL".equals(ambito)) {
      switch (accion) {
        case "ESTADO":
          return "Mover el serial " + objetivo + " al estado " + nz(estadoDestino)
              + " (queda sin pallet ni caja y se elimina de Empaque)";
        case "CAJA":
          return "Sacar el serial " + objetivo + " de su caja (queda sin pallet ni caja, vuelve a P-EMPAQUE y se elimina de Empaque)";
        case "PALLET":
          return "Pasar el serial " + objetivo + " al pallet " + nz(palletDestino);
        default:
          return accion + " sobre el serial " + objetivo;
      }
    }
    switch (accion) {
      case "MOVER":
        return "Mover el pallet " + objetivo + " con todos sus seriales al estado " + nz(estadoDestino);
      case "CAJA_VACIAR":
        return "Sacar los seriales de la caja " + nz(caja) + " del pallet " + objetivo
            + " (quedan sin pallet ni caja, vuelven a P-EMPAQUE y se eliminan de Empaque)";
      case "CAJA_ELIMINAR":
        return "Eliminar la caja " + nz(caja) + " del pallet " + objetivo
            + " (sus seriales quedan sin pallet ni caja, vuelven a P-EMPAQUE y se eliminan de Empaque)";
      case "CAJA_PALLET":
        return "Pasar los seriales de la caja " + nz(caja) + " del pallet " + objetivo + " al pallet " + nz(palletDestino);
      default:
        return accion + " sobre el pallet " + objetivo;
    }
  }

  private String nombreEstado(String ambito, Integer estadoId) {
    if (estadoId == null) {
      return null;
    }
    for (Object[] row : repository.estados(ambito)) {
      if (((Number) row[0]).intValue() == estadoId) {
        return String.valueOf(row[1]);
      }
    }
    return null;
  }

  /**
   * Aprobadores definidos en application.properties (autogestion.aprobadores.correos).
   * Cada fila es {id=0, nombre=correo, correo}: no dependen de usuarios ni perfiles del WMS.
   */
  private List<Object[]> aprobadoresConfigurados() {
    List<Object[]> result = new ArrayList<>();
    if (correosAprobadores == null) {
      return result;
    }
    Set<String> vistos = new java.util.LinkedHashSet<>();
    for (String correo : correosAprobadores.split("[,;\\s]+")) {
      String limpio = correo.trim().toLowerCase();
      if (limpio.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") && vistos.add(limpio)) {
        result.add(new Object[] { 0, limpio, limpio });
      }
    }
    return result;
  }

  private static String nuevoToken() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private static String hash(String token) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder(64);
      for (byte b : digest) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo calcular el hash del token.", e);
    }
  }

  private static String vacioANulo(String valor) {
    return valor == null || valor.isBlank() ? null : valor.trim();
  }

  private static String nz(String valor) {
    return valor == null || valor.isBlank() ? "?" : valor;
  }

  private static String str(Object valor) {
    return valor == null ? null : valor.toString();
  }
}
