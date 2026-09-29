package com.woden.wms_backend.services.ClienteServices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.diagnostico.DiagnosticoRequest;
import com.woden.wms_backend.repositories.ClienteRepositories.DiagnosticoRepository;

import jakarta.transaction.Transactional;

@Service
public class DiagnosticoService {

  private static final Logger logger     = LoggerFactory.getLogger(DiagnosticoService.class);
  private static final int    CHUNK_SIZE = 50;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private DiagnosticoRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Ruta ÚNICA y global de la plantilla de hoja de vida — independiente del cliente.
   * A diferencia de RutaArchivos (que sí es por cliente, para las evidencias ya firmadas),
   * la plantilla es la misma para todos: agregar un cliente nuevo no requiere copiarla
   * a ningún lado, solo configurar RutaArchivos para sus evidencias.
   */
  @Value("${HOJA_VIDA_PLANTILLA_PATH:}")
  private String plantillaPath;

  @Transactional
  public void create(Integer serialId, String serial, String mac, Integer codigoSapId, Integer usuarioId,
      String variable1, String variable2, String variable3, String variable4) {
        System.out.println(variable1);
        System.out.println(variable2);
        System.out.println(variable3);
        System.out.println(variable4);
    repository.create(serialId, serial, mac, codigoSapId, usuarioId, variable1, variable2, variable3, variable4);
  }

  public Integer updateSapCode(String serial) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_UpdateSapCodeDiagnostic(?, ?)}");

      // Parámetro de entrada
      stmt.setString(1, serial);

      // Parámetro de salida
      stmt.registerOutParameter(2, Types.INTEGER); // "Filas" está en la posición 2

      stmt.execute();

      Integer filas = stmt.getInt(2); // Obtener el valor del OUT param

      stmt.close();
      return filas > 0 ? 1 : 0;

    } catch (SQLException e) {
      System.err.println("Error en updateSapCode: " + e.getMessage());
      return 0;
    }
  }

  @Transactional
  public void updateDiagnostico(Integer estadoFinalId, Integer fallaId, String serial, Integer estadoCalidadId) {
    try {
      repository.updateDiagnostico(estadoFinalId, fallaId, serial, estadoCalidadId);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  @Transactional
  public void updateDiagnosticos(List<Map<String, Object>> requestList) {
    System.out.println("[DIAGNOSTICO-PROCESO] Iniciando actualización diagnóstico de " + requestList.size() + " seriales");
    int procesados = 0;
    for (Map<String, Object> request : requestList) {
      Integer estadoFinalId = (Integer) request.get("estadoFinalId");
      Integer fallaId = (Integer) request.get("fallaId");
      String serial = (String) request.get("serial");
      Integer estadoCalidadId = (Integer) request.get("estadoCalidadId");
      System.out.println("[DIAGNOSTICO-PROCESO] Procesando diagnóstico " + (procesados + 1) + "/" + requestList.size() + ": serial=" + serial + " estadoFinalId=" + estadoFinalId + " fallaId=" + fallaId);
      repository.updateDiagnostico(estadoFinalId, fallaId, serial, estadoCalidadId);
      procesados++;
    }
    System.out.println("[DIAGNOSTICO-PROCESO] Diagnósticos actualizados correctamente: " + procesados + "/" + requestList.size());
  }

  public Integer getFailureIdDiagnostic(Integer serialId) {
    return repository.getFailureIdDiagnostic(serialId);
  }

  public String getFailureDiagnostic(String serial) {
    return repository.getFailureDiagnostic(serial);
  }

  public void delete(String serial) {
    try {
      repository.delete(serial);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public List<Map<String, Object>> getDiagnosedUser(String perfil, Integer usuarioId) {
    List<Object[]> results = repository.getDiagnosedUser(perfil, usuarioId);

    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serialId", row[1].toString());
      map.put("serial", row[2].toString());
      map.put("mac", row[3].toString());
      map.put("codigoSap", row[4].toString());
      map.put("descripcion", row[5].toString());
      map.put("fallaId", Integer.parseInt(row[6].toString()));
      map.put("estadoFinalId", Integer.parseInt(row[7].toString()));
      map.put("usuario", row[8].toString());
      map.put("loteId", row[9] != null ? Integer.parseInt(row[9].toString()) : 0);
      map.put("lote", row[10] != null ? row[10].toString() : "");
      map.put("pqrs", row[11] != null ? row[11].toString() : "");
      map.put("fecha", row[12] != null ? row[12].toString() : "");
      // El SP devuelve: ..., Fecha(12), TruckRollId(13), rn(14). Con el SP viejo (14 cols) la posición 13 es rn — no mapear.
      map.put("truckRollId", row.length > 13 && row[13] != null ? Integer.parseInt(row[13].toString()) : null);
      formattedResults.add(map);
    }
    return formattedResults;
  }

  @Transactional
  public void save(List<DiagnosticoRequest> items) {
    int total = items.size();
    int procesados = 0;
    try {
      for (int i = 0; i < total; i += CHUNK_SIZE) {
        List<DiagnosticoRequest> chunk = items.subList(i, Math.min(i + CHUNK_SIZE, total));
        String json = objectMapper.writeValueAsString(chunk);
        repository.save(json);
        procesados += chunk.size();
        logger.info("[DIAGNOSTICO] chunk guardado: {}/{}", procesados, total);
      }

      // Actualizar TruckRollId de los seriales clasificados TruckRoll/Garantía (1→2/3, 7→8/9)
      for (DiagnosticoRequest item : items) {
        if (item.getTruckRollId() != null) {
          repository.updateTruckRollId(item.getSerial(), item.getTruckRollId());
          logger.info("[DIAGNOSTICO] TruckRollId actualizado: serial={} → {}", item.getSerial(), item.getTruckRollId());
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al guardar diagnóstico: " + e.getMessage(), e);
    }
  }

  // ── Hoja de vida (evidencia TruckRoll/Garantía) ──────────────────────────────

  private static final List<String> EXTENSIONES_HOJA_VIDA = List.of("pdf", "docx");

  /**
   * Sube la hoja de vida de un serial TruckRoll/Garantía/PQRS.
   * Ruta final: {RutaEvidencias}\{NOMBRE-CLIENTE}\Hoja de vida-{serial}.{ext}
   * Un archivo por serial: si ya existe se reemplaza (en cualquier extensión permitida).
   * Registra el cargue en HojaVida para que se pueda ubicar por SQL sin acceso al filesystem.
   */
  public void uploadHojaVida(String serial, MultipartFile file, Integer usuarioId, String modulo) throws IOException {
    if (serial == null || serial.isBlank()) throw new IllegalArgumentException("Serial requerido");
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("Archivo requerido");

    String nombreOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
    String ext = nombreOriginal.contains(".")
        ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1).toLowerCase()
        : "";
    if (!EXTENSIONES_HOJA_VIDA.contains(ext)) {
      throw new IllegalArgumentException("Solo se permiten archivos PDF o DOCX");
    }

    Path dir = getDirectorioHojasVida();
    Files.createDirectories(dir);

    String serialLimpio = sanitizarSerial(serial);
    for (String e : EXTENSIONES_HOJA_VIDA) {
      Files.deleteIfExists(dir.resolve("Hoja de vida-" + serialLimpio + "." + e));
    }

    String nombreArchivo = "Hoja de vida-" + serialLimpio + "." + ext;
    Path destino = dir.resolve(nombreArchivo);
    Files.copy(file.getInputStream(), destino);
    logger.info("[HOJA-VIDA] Guardada: {}", destino.toAbsolutePath());

    registrarHojaVida(serial, nombreArchivo, destino.toAbsolutePath().toString(), ext, modulo, usuarioId);
  }

  /**
   * Registra el cargue en la tabla HojaVida (BD del cliente actual): desactiva cualquier
   * registro previo del serial y crea uno nuevo con la ruta/extensión/módulo actuales.
   * SQL no puede servir el binario, pero sí permite ubicar la ruta exacta del archivo.
   */
  private void registrarHojaVida(String serial, String nombreArchivo, String rutaCompleta,
                                  String extension, String modulo, Integer usuarioId) {
    String sqlIngreso = "SELECT TOP 1 Id, TruckRollId FROM Ingreso WHERE Serial = ? ORDER BY Id DESC";
    String sqlDesactivar = "UPDATE HojaVida SET Activo = 0 WHERE Serial = ? AND Activo = 1";
    String sqlInsertar = "INSERT INTO HojaVida " +
        "(SerialId, Serial, NombreArchivo, RutaCompleta, Extension, Modulo, TruckRollId, UsuarioId, ClienteId) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = dataSource.getConnection()) {
      Integer serialId = null;
      Integer truckRollId = null;
      try (PreparedStatement ps = conn.prepareStatement(sqlIngreso)) {
        ps.setString(1, serial);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            serialId = rs.getInt(1);
            truckRollId = (Integer) rs.getObject(2);
          }
        }
      }
      if (serialId == null) {
        logger.warn("[HOJA-VIDA] No se encontró Ingreso para serial {} — no se registra en HojaVida", serial);
        return;
      }

      try (PreparedStatement ps = conn.prepareStatement(sqlDesactivar)) {
        ps.setString(1, serial);
        ps.executeUpdate();
      }

      try (PreparedStatement ps = conn.prepareStatement(sqlInsertar)) {
        ps.setInt(1, serialId);
        ps.setString(2, serial);
        ps.setString(3, nombreArchivo);
        ps.setString(4, rutaCompleta);
        ps.setString(5, extension);
        ps.setString(6, modulo != null ? modulo : "DESCONOCIDO");
        if (truckRollId != null) ps.setInt(7, truckRollId); else ps.setNull(7, Types.INTEGER);
        if (usuarioId != null) ps.setInt(8, usuarioId); else ps.setNull(8, Types.INTEGER);
        Integer clienteId = ClientDatabaseContext.getCurrentClientId();
        if (clienteId != null) ps.setInt(9, clienteId); else ps.setNull(9, Types.INTEGER);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      logger.error("[HOJA-VIDA] Error registrando HojaVida para serial {}: {}", serial, e.getMessage());
    }
  }

  /** Indica si el serial ya tiene hoja de vida cargada (en cualquier extensión permitida). */
  public boolean hasHojaVida(String serial) {
    try {
      Path dir = getDirectorioHojasVida();
      String serialLimpio = sanitizarSerial(serial);
      for (String e : EXTENSIONES_HOJA_VIDA) {
        if (Files.exists(dir.resolve("Hoja de vida-" + serialLimpio + "." + e))) return true;
      }
    } catch (Exception e) {
      logger.error("[HOJA-VIDA] Error verificando hoja de vida para serial {}: {}", serial, e.getMessage());
    }
    return false;
  }

  /** Elimina la hoja de vida cargada del serial (cualquier extensión permitida). */
  public void deleteHojaVida(String serial, Integer usuarioId) throws IOException {
    if (serial == null || serial.isBlank()) throw new IllegalArgumentException("Serial requerido");
    Path dir = getDirectorioHojasVida();
    String serialLimpio = sanitizarSerial(serial);
    boolean eliminado = false;
    for (String e : EXTENSIONES_HOJA_VIDA) {
      if (Files.deleteIfExists(dir.resolve("Hoja de vida-" + serialLimpio + "." + e))) {
        eliminado = true;
      }
    }
    if (eliminado) {
      logger.info("[HOJA-VIDA] Eliminada hoja de vida del serial {}", serial);
      desactivarHojaVida(serial, usuarioId);
    }
  }

  /** Marca como inactivo (soft-delete) el registro activo del serial en HojaVida. */
  private void desactivarHojaVida(String serial, Integer usuarioId) {
    String sql = "UPDATE HojaVida SET Activo = 0, UsuarioIdEliminacion = ?, FechaEliminacion = GETDATE() " +
        "WHERE Serial = ? AND Activo = 1";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      if (usuarioId != null) ps.setInt(1, usuarioId); else ps.setNull(1, Types.INTEGER);
      ps.setString(2, serial);
      ps.executeUpdate();
    } catch (SQLException e) {
      logger.error("[HOJA-VIDA] Error desactivando registro HojaVida para serial {}: {}", serial, e.getMessage());
    }
  }

  /**
   * Rutas conocidas donde puede vivir la carpeta PLANTILLA, en orden de intento:
   * la unidad mapeada (Z:) solo existe en algunos servidores/sesiones; la ruta UNC
   * directa siempre es válida si el servidor tiene acceso de red al share.
   */
  private static final List<String> RUTAS_PLANTILLA_CANDIDATAS = List.of(
      "Z:\\ENV\\PRD\\pqrs\\PLANTILLA",
      "\\\\10.128.0.28\\archivos\\ENV\\PRD\\pqrs\\PLANTILLA"
  );

  /**
   * Devuelve la plantilla de hoja de vida. Prueba primero HOJA_VIDA_PLANTILLA_PATH
   * (si está configurada) y luego las rutas conocidas (mapeada y UNC), en orden,
   * usando la primera que sea accesible y tenga un archivo — sin requerir configurar
   * nada distinto por servidor.
   */
  public Path getPlantillaHojaVida() {
    List<String> candidatas = new java.util.ArrayList<>();
    if (plantillaPath != null && !plantillaPath.isBlank()) candidatas.add(plantillaPath);
    candidatas.addAll(RUTAS_PLANTILLA_CANDIDATAS);

    IOException ultimoError = null;
    for (String candidata : candidatas) {
      Path dir = Paths.get(candidata);
      try (var stream = Files.list(dir)) {
        Path encontrado = stream.filter(Files::isRegularFile).findFirst().orElse(null);
        if (encontrado != null) {
          logger.info("[HOJA-VIDA] Plantilla encontrada en {}", dir);
          return encontrado;
        }
        logger.debug("[HOJA-VIDA] Carpeta accesible pero vacía: {}", dir);
      } catch (IOException e) {
        ultimoError = e;
        logger.debug("[HOJA-VIDA] Ruta de plantilla no accesible: {} ({})", dir, e.getMessage());
      }
    }

    if (ultimoError != null) {
      logger.error("[HOJA-VIDA] No se pudo acceder a ninguna ruta de plantilla conocida ({}). Último error: {}",
          candidatas, ultimoError.getMessage());
      throw new RuntimeException("No se pudo acceder a la carpeta de plantilla. Último error: " + ultimoError.getMessage(), ultimoError);
    }
    return null; // todas las rutas accesibles pero sin ningún archivo dentro
  }

  /** Carpeta del cliente actual: {RutaArchivos}\{NOMBRE-CLIENTE} */
  private Path getDirectorioHojasVida() {
    String ruta = getRutaArchivos();
    if (ruta == null || ruta.isBlank()) {
      throw new IllegalStateException("RutaArchivos no configurada en Parametro_TruckrollsLiberty para este cliente");
    }
    String cliente = ClientDatabaseContext.getCurrentClientName();
    String carpetaCliente = cliente != null ? cliente.trim().toUpperCase().replace(" ", "-") : "SIN-CLIENTE";
    return Paths.get(ruta, carpetaCliente);
  }

  /** Lee RutaArchivos de la regla TruckRoll activa del cliente actual. */
  private String getRutaArchivos() {
    String clientDb = ClientDatabaseContext.getCurrentClientDb();
    String sql = "SELECT TOP 1 RutaArchivos FROM [WmsWdGeneral].[dbo].[Parametro_TruckrollsLiberty] " +
        "WHERE Activo = 1 AND BaseDestino = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, clientDb);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getString(1);
      }
    } catch (SQLException e) {
      logger.error("[HOJA-VIDA] Error consultando RutaArchivos para {}: {}", clientDb, e.getMessage());
    }
    return null;
  }

  /** Evita path traversal: el serial solo conserva caracteres alfanuméricos, guion y guion bajo. */
  private String sanitizarSerial(String serial) {
    return serial.replaceAll("[^A-Za-z0-9_-]", "");
  }

  public List<Map<String, Object>> getDiagnosticVariables(String serial) {
    List<Object[]> results = repository.getDiagnosticVariables(serial);

    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("variable1", row[0] != null ? row[0].toString() : "");
      map.put("variable2", row[1] != null ? row[1].toString() : "");
      map.put("variable3", row[2] != null ? row[2].toString() : "");
      map.put("variable4", row[3] != null ? row[3].toString() : "");
      formattedResults.add(map);
    }
    return formattedResults;
  }
}
