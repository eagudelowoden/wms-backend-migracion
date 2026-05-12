package com.woden.wms_backend.services.ClienteServices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.dto.clientDTO.EntryProgressDTO;
import com.woden.wms_backend.dto.clientDTO.IngresoModelDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateStateAllItemRequest;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.exception.EntryNotFoundException;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IlegibleRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class IngresoService extends BaseService<IngresoModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(IngresoService.class);

  @Autowired
  private IngresoRepository ingresoRepository;

  @Autowired
  private IlegibleRepository ilegibleRepository;

  @Autowired
  private ConsecutiveService consecutiveService;

  @Autowired
  private DataSource dataSource;

  @PersistenceContext
  private EntityManager entityManager;

  // ── Creación / eliminación ───────────────────────────────────────────────────

  public String createIngresoCallable(IngresoModel ingreso) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall(
          "{call pa_InsertEntry(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

      stmt.setString(1, ingreso.getSerial());
      stmt.setString(2, ingreso.getMac());
      stmt.setString(3, ingreso.getSerial3());
      stmt.setString(4, ingreso.getSerial4());
      stmt.setString(5, ingreso.getSerial5());
      stmt.setObject(6, ingreso.getCodigoSapId(), Types.INTEGER);
      stmt.setObject(7, ingreso.getPalletId(), Types.INTEGER);
      stmt.setObject(8, ingreso.getEstadoId(), Types.INTEGER);
      stmt.setObject(9, ingreso.getTipoOrigenId(), Types.INTEGER);
      stmt.setObject(10, ingreso.getOrigenId(), Types.INTEGER);
      stmt.setObject(11, ingreso.getTipologiaId(), Types.INTEGER);
      stmt.setObject(12, ingreso.getNivelId(), Types.INTEGER);
      stmt.setString(13, ingreso.getTramite());
      stmt.setString(14, ingreso.getDocumento());
      stmt.setString(15, ingreso.getGuia());
      stmt.setObject(16, ingreso.getCaja(), Types.INTEGER);
      stmt.setString(17, ingreso.getFalla());
      stmt.setString(18, ingreso.getTecnicoCliente());
      stmt.setObject(19, ingreso.getPrealertaId(), Types.INTEGER);
      stmt.setObject(20, ingreso.getCruce(), Types.INTEGER);
      stmt.setString(21, ingreso.getNovedad());
      stmt.setObject(22, ingreso.getGarantiaFabricante(), Types.BIT);
      stmt.setObject(23, ingreso.getUsuarioId(), Types.INTEGER);
      stmt.setString(24, ingreso.getObservaciones());
      stmt.setString(25, ingreso.getEstadoCliente());
      stmt.setObject(26, ingreso.getLoteId(), Types.INTEGER);
      stmt.setObject(27, ingreso.getCajaIngresoId(), Types.INTEGER);
      stmt.setObject(28, ingreso.getModeloId() != 0 ? ingreso.getModeloId() : null, Types.INTEGER);
      stmt.registerOutParameter(29, Types.INTEGER);
      stmt.registerOutParameter(30, Types.VARCHAR);

      stmt.execute();

      Integer codigo = stmt.getInt(29);
      String mensaje = stmt.getString(30);

      if (codigo == 0) return null;

      // Mapear código del SP a mensaje legible
      return switch (codigo) {
        case 1001 -> "El serial ya se encuentra registrado en el sistema.";
        case 1002 -> "La MAC ya se encuentra registrada en el sistema.";
        default -> mensaje != null ? mensaje : "Error al registrar el ingreso (código: " + codigo + ").";
      };

    } catch (SQLException e) {
      logger.error("[createIngresoCallable] Error al crear ingreso para serial {}: {}", ingreso.getSerial(), e.getMessage(), e);
      return "EXCEPCION: " + e.getMessage();
    }
  }

  @Transactional
  public void deleteEntries(List<String> seriales) {
    seriales.forEach(ingresoRepository::eliminarIngresos);
    logger.info("[deleteEntries] {} ingreso(s) eliminado(s)", seriales.size());
  }

  // ── Búsqueda / mapeo ─────────────────────────────────────────────────────────

  public IngresoModel getModelIngreso(String serial) {
    List<Object[]> results = ingresoRepository.searchIngreso(serial);
    if (results.isEmpty()) {
      throw EntryNotFoundException.forSerial(serial);
    }
    return mapToIngresoModel(results.get(0));
  }

  public IngresoModelDTO getModelIngresoV3(String serial) {
    List<Object[]> results = ingresoRepository.searchIngreso(serial);
    if (results.isEmpty()) {
      return null;
    }
    Object[] obj = results.get(0);
    IngresoModelDTO ingreso = new IngresoModelDTO();
    ingreso.setId((Integer) obj[0]);
    ingreso.setSerial((String) obj[1]);
    ingreso.setMac((String) obj[2]);
    ingreso.setSerial3((String) obj[3]);
    ingreso.setSerial4((String) obj[4]);
    ingreso.setCodigoSap((String) obj[5]);
    ingreso.setDescripcion((String) obj[6]);
    ingreso.setNombreUsuario((String) obj[7]);
    ingreso.setTipoOrigenId((Integer) obj[8]);
    ingreso.setTipologia((String) obj[9]);
    ingreso.setFecha(obj[10] != null ? ((Timestamp) obj[10]).toString() : null);
    ingreso.setLote((String) obj[11]);
    return ingreso;
  }

  public List<IngresoModel> getListModel(List<String> seriales) {
    return seriales.stream()
        .flatMap(serial -> ingresoRepository.searchIngreso(serial).stream())
        .map(this::mapToIngresoModel)
        .collect(Collectors.toList());
  }

  public List<IngresoDTO> searchEntryReingreso(int palletId) {
    List<Object[]> results = ingresoRepository.searchEntryReingreso(palletId);
    return results.stream().map(obj -> {
      IngresoDTO ingreso = new IngresoDTO();
      ingreso.setSerial((String) obj[0]);
      ingreso.setMac((String) obj[1]);
      ingreso.setSerial3((String) obj[2]);
      ingreso.setSerial4((String) obj[3]);
      ingreso.setSerial5((String) obj[4]);
      ingreso.setCodigoSap((String) obj[5]);
      ingreso.setDescripcion((String) obj[6]);
      ingreso.setTipologia((String) obj[7]);
      ingreso.setTipoOrigen((String) obj[8]);
      ingreso.setFecha(((Timestamp) obj[9]).toString());
      ingreso.setEstadoCliente((String) obj[10]);
      ingreso.setLote((String) obj[11]);
      ingreso.setModelo((String) obj[12]);
      ingreso.setReingreso((Integer) obj[13]);
      return ingreso;
    }).collect(Collectors.toList());
  }

  public List<IngresoTransitoDTO> getIngresoTransitByPalletId(Integer palletId) {
    List<Object[]> results = ingresoRepository.searchIngresoTransito(palletId);
    return results.stream().map(row -> {
      IngresoTransitoDTO dto = new IngresoTransitoDTO();
      dto.setSerial((String) row[0]);
      dto.setMac((String) row[1]);
      dto.setCodigoSap((String) row[2]);
      dto.setDescripcion((String) row[3]);
      dto.setTipologia((String) row[4]);
      dto.setLote((String) row[5]);
      dto.setGuia((String) row[6]);
      dto.setDocumento((String) row[7]);
      dto.setFecha(row[8] != null ? row[8].toString() : null);
      return dto;
    }).collect(Collectors.toList());
  }

  public String getSerialByMac(String mac) {
    List<Object[]> results = ingresoRepository.getSerialByMac(mac);
    return results.isEmpty() ? null : (String) results.get(0)[0];
  }

  public Integer getReingresos(String serial) {
    return ingresoRepository.getReingresos(serial);
  }

  public List<String> getLevelEntry(String serial) {
    return ingresoRepository.getLevelEntry(serial)
        .stream()
        .map(result -> (String) result[0])
        .collect(Collectors.toList());
  }

  public List<String> getProactiveRepairAll() {
    return ingresoRepository.getProactiveRepairAll();
  }

  public Integer getProactiveRepair(String serial) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_GetProactiveRepair(?, ?)}");
      stmt.setString(1, serial);
      stmt.registerOutParameter(2, Types.INTEGER);
      stmt.execute();
      return stmt.getInt(2);
    } catch (SQLException e) {
      logger.error("[getProactiveRepair] Error para serial {}: {}", serial, e.getMessage(), e);
      throw new BusinessRuleException("Error al consultar reparación proactiva: " + e.getMessage());
    }
  }

  public List<Map<String, String>> searchDiagnosticEntry(String estadoFinal, String perfil, Integer usuarioId) {
    List<Object[]> results = ingresoRepository.searchDiagnosticEntry(estadoFinal, perfil, usuarioId);
    return results.stream().map(result -> {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      entry.put("falla", (String) result[4]);
      entry.put("estado", (String) result[5]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<String> searchDeliveryEntry(String serial) {
    List<String> results = ingresoRepository.searchDeliveryEntry(serial);
    return results != null ? results : List.of();
  }

  public List<Map<String, String>> searchClasificationEntry() {
    return ingresoRepository.searchClasificationEntry().stream().map(result -> {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchPalletBoxEntry(String estado, Integer palletId, Integer cajaId) {
    return ingresoRepository.searchPalletBoxEntry(estado, palletId, cajaId).stream().map(result -> {
      Map<String, String> seriales = new HashMap<>();
      seriales.put("serial", toStr(result[0]));
      seriales.put("mac", toStr(result[1]));
      seriales.put("smartCard", toStr(result[2]));
      seriales.put("numeroSmartcard", toStr(result[3]));
      seriales.put("serial3", toStr(result[4]));
      seriales.put("serial4", toStr(result[5]));
      seriales.put("codigo", toStr(result[6]));
      seriales.put("descripcion", toStr(result[7]));
      seriales.put("nivel", toStr(result[8]));
      seriales.put("lote", toStr(result[9]));
      seriales.put("modelo", toStr(result[10]));
      return seriales;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchRepairEntry(String estadoFinal, String perfil, Integer usuarioId) {
    return ingresoRepository.searchRepairEntry(estadoFinal, perfil, usuarioId).stream().map(result -> {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      entry.put("estado", (String) result[4]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchQualityEntry() {
    return ingresoRepository.searchQualityEntry().stream().map(obj -> {
      Map<String, String> fila = new HashMap<>();
      fila.put("serial", toStr(obj[0]));
      fila.put("mac", toStr(obj[1]));
      fila.put("pallet", toStr(obj[2]));
      fila.put("caja", toStr(obj[3]));
      fila.put("smartCard", toStr(obj[4]));
      return fila;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchNoveltyEntry(String tipoNovedad) {
    return ingresoRepository.searchNoveltyEntry(tipoNovedad).stream().map(result -> {
      Map<String, Object> entry = new HashMap<>();
      entry.put("id", (Integer) result[0]);
      entry.put("serial", (String) result[1]);
      entry.put("mac", (String) result[2]);
      entry.put("codigoSap", (String) result[3]);
      entry.put("descripcion", (String) result[4]);
      entry.put("novedad", (String) result[5]);
      entry.put("observaciones", (String) result[6]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchNoveltyEntryDelivery(String perfil, Integer usuarioId, String tipoNovedad) {
    return ingresoRepository.searchNoveltyEntryDelivery(perfil, usuarioId, tipoNovedad).stream().map(result -> {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchScrapEntry(String estado) {
    return ingresoRepository.searchScrapEntry(estado).stream().map(result -> {
      Map<String, Object> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      return entry;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> getScrapUser(Integer usuarioIdMovimiento) {
    return ingresoRepository.getScrapUser(usuarioIdMovimiento).stream().map(row -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serial", row[1].toString());
      map.put("mac", row[2].toString());
      map.put("serial3", row[3] != null ? row[3].toString() : "");
      map.put("serial4", row[4] != null ? row[4].toString() : "");
      map.put("serial5", row[5] != null ? row[5].toString() : "");
      map.put("codigoSap", row[6].toString());
      map.put("descripcion", row[7].toString());
      map.put("usuarioAsignado", row[8] != null ? row[8].toString() : "");
      map.put("nivelId", row[9] != null ? Integer.parseInt(row[9].toString()) : null);
      map.put("loteId", row[10] != null ? Integer.parseInt(row[10].toString()) : null);
      map.put("lote", row[11] != null ? row[11].toString() : "");
      map.put("modelo", row[12] != null ? row[12].toString() : "");
      map.put("fecha", row[13] != null ? row[13].toString() : "");
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> getEtiquetadoUser(Integer usuarioIdMovimiento) {
    return ingresoRepository.getEtiquetadoUser(usuarioIdMovimiento).stream().map(row -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serial", row[1].toString());
      map.put("mac", row[2].toString());
      map.put("serial3", row[3] != null ? row[3].toString() : "");
      map.put("serial4", row[4] != null ? row[4].toString() : "");
      map.put("serial5", row[5] != null ? row[5].toString() : "");
      map.put("codigoSap", row[6].toString());
      map.put("descripcion", row[7].toString());
      map.put("estado", row[8] != null ? row[8].toString() : "");
      map.put("fecha", row[8] != null ? row[8].toString() : "");
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> getSerialsByPalletInventory(String pallet) {
    return ingresoRepository.getSerialsByPalletInventory(pallet).stream().map(result -> {
      Map<String, Object> entry = new HashMap<>();
      entry.put("id", (Integer) result[0]);
      entry.put("serial", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("codigoSapReal", (String) result[3]);
      entry.put("pallet", (String) result[4]);
      entry.put("palletReal", (String) result[5]);
      entry.put("estadoId", (String) result[6]);
      entry.put("estadoSap", (String) result[7]);
      entry.put("estadoRR", (String) result[8]);
      entry.put("ajuste", (String) result[9]);
      entry.put("fecha", (Date) result[10]);
      entry.put("usuarioId", (Integer) result[11]);
      entry.put("serialId", (Integer) result[12]);
      entry.put("mac", (String) result[13]);
      entry.put("sobrante", (Boolean) result[14]);
      return entry;
    }).collect(Collectors.toList());
  }

  public String getValidaStateInventory(String estado, String estadoInventario) {
    return ingresoRepository.getValidaStateInventory(estado, estadoInventario);
  }

  public EntryProgressDTO getEntryProgress(Integer palletId, Integer estadoId) {
    List<Object[]> result = ingresoRepository.getEntryProgress(palletId, estadoId);
    if (result == null || result.isEmpty()) {
      return new EntryProgressDTO(0, 0, 0);
    }
    Object[] row = result.get(0);
    return new EntryProgressDTO(
        row[0] != null ? ((Number) row[0]).intValue() : 0,
        row[1] != null ? ((Number) row[1]).intValue() : 0,
        row[2] != null ? ((Number) row[2]).intValue() : 0);
  }

  // ── Dispatch ──────────────────────────────────────────────────────────────────

  public List<IngresoModel> getModelDispatch(Integer palletId, Integer cajaId) {
    return ingresoRepository.getModelDispatch(palletId, cajaId).stream()
        .map(this::mapToDispatchModel)
        .collect(Collectors.toList());
  }

  public List<IngresoModel> getModelDispatchBox(Integer palletId, List<Integer> cajasIds) {
    return cajasIds.stream()
        .flatMap(caja -> ingresoRepository.getModelDispatch(palletId, caja).stream())
        .map(this::mapToDispatchModel)
        .collect(Collectors.toList());
  }

  @Transactional
  public void updateDispatchEntry(String serial, Integer estadoId, Integer palletId,
      Integer cajaDespachoId, Integer usuarioMovimientoId, Integer loteId) {
    Integer loteIdFinal = (loteId != null && loteId == 0) ? null : loteId;
    ingresoRepository.updateDispatchEntry(serial, estadoId, palletId, cajaDespachoId,
        usuarioMovimientoId, loteIdFinal, 4);
  }

  // ── Regularización ────────────────────────────────────────────────────────────

  public void regularizarSap(String serial, int codigoSapId, int usuarioIdMovimiento) {
    ingresoRepository.updateSapCode(codigoSapId, usuarioIdMovimiento, serial, 1);
  }

  public void regularizarLoteSerial(String serial, int loteId, int usuarioIdMovimiento) {
    ingresoRepository.updateBatchSerial(loteId, usuarioIdMovimiento, serial, 1);
  }

  public Integer updateSapCode(int codigoSapId, int usuarioIdMovimiento, String serial) {
    return ingresoRepository.updateSapCode(codigoSapId, usuarioIdMovimiento, serial, 0);
  }

  // ── Ingreso ilegible ──────────────────────────────────────────────────────────

  public String generarIngresoIlegible(IngresoIlegibleDTO ingresoDTO, String cliente, Integer usuarioId) {
    String consecutivo = consecutiveService.getConsecutive(cliente);
    String serialIlegible = (consecutivo == null) ? "1"
        : String.valueOf(Integer.parseInt(consecutivo.substring(6)) + 1);

    String prefijo = switch (cliente.toUpperCase()) {
      case "CLARO" -> "ILE-C-";
      case "ETB" -> "ILE-E-";
      case "TIGO BOGOTA", "TIGO COSTA RICA", "TIGO MEDELLIN", "TIGO SALVADOR", "TIGO PANAMA" -> "ILE-T-";
      case "RED EXTERNA" -> "ILE-R-";
      case "MOVIL" -> "ILE-M-";
      case "HUGHESNET" -> "ILE-H-";
      case "DIRECTV", "TIGO CORPORATIVO PA", "TIGO CORPORATIVO CR" -> "ILE-D-";
      default -> "ILE-" + cliente.charAt(0) + "-";
    };

    String serial = prefijo + serialIlegible;
    String mac = "MAC-ILEGIBLE-" + serialIlegible;
    Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

    int status = ilegibleRepository.insertIlegible(serial, mac, 0, fechaActual);
    if (status != 1) {
      throw new BusinessRuleException("No se pudo generar el ingreso ilegible para el serial: " + serial);
    }
    return serial;
  }

  // ── Envío ──────────────────────────────────────────────────────────────────────

  public void sendEntry(Integer estadoId, Integer tipologiaId, Integer usuarioId, Integer palletId, Integer opcion) {
    ingresoRepository.sendIngreso(estadoId, tipologiaId, usuarioId, palletId, opcion, 0);
  }

  public void sendStorageEntry(Integer palletId, Integer estadoId, Integer tipologiaId, Integer usuarioId) {
    ingresoRepository.SendStorageEntry(estadoId, tipologiaId, usuarioId, palletId, 0);
  }

  public void sendNoveltyEntry(Integer estadoId, Integer tipologiaId, Integer usuarioId, Integer palletId,
      Integer opcion) {
    ingresoRepository.sendNoveltyEntry(estadoId, tipologiaId, usuarioId, palletId, opcion, 0);
  }

  // ── Actualización de tipología / pallet / nivel ───────────────────────────────

  public void updateTipologia(Integer palletId, Integer tipologiaId) {
    ingresoRepository.updateTipologyEntry(palletId, tipologiaId, 0);
  }

  public void unifyEntry(Integer palletIdDestino, Integer tipologiaId, Integer usuarioId, List<Integer> palletIds) {
    palletIds.forEach(id -> ingresoRepository.unifyEntry(palletIdDestino, tipologiaId, usuarioId, id));
  }

  public void updatePalletEntry(Integer palletId, Integer usuarioIdMovimiento, List<String> seriales) {
    seriales.forEach(serial -> ingresoRepository.updatePalletEntry(palletId, usuarioIdMovimiento, serial));
  }

  public void updatePalletAndTipologyEntry(Integer palletId, Integer tipologiaId, Integer usuarioIdMovimiento,
      Integer estadoId, List<String> seriales) {
    seriales.forEach(serial -> ingresoRepository.updatePalletAndTipologyEntry(
        palletId, tipologiaId, usuarioIdMovimiento, estadoId, serial));
  }

  public void updateLevel(Integer levelId, Integer palletId) {
    ingresoRepository.updateLevel(levelId, palletId, 0);
  }

  public int updateLevelWeb(Integer levelId, Integer palletId) {
    Integer result = ingresoRepository.updateLevelWeb(levelId, palletId);
    return (result != null && result > 0) ? 1 : 0;
  }

  // ── Actualización de estado ───────────────────────────────────────────────────

  /** Usado por SmartCardService — actualiza un serial individual. */
  public void updateStateAllEntry(Integer estadoId, Integer usuarioIdMovimiento, String serial) {
    ingresoRepository.updateStateAllEntry(estadoId, usuarioIdMovimiento, serial);
  }

  @Transactional
  public void updateStateAllEntries(List<UpdateStateAllItemRequest> items) {
    items.forEach(item ->
        ingresoRepository.updateStateAllEntry(item.estadoId(), item.usuarioIdMovimiento(), item.serial()));
    logger.info("[updateStateAllEntries] {} serial(es) actualizado(s)", items.size());
  }

  public void updateStateEntry(Integer estadoId, Integer palletId, Integer usuarioId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntry(estadoId, palletId, usuarioId, fecha, serial);
  }

  public void updateChangedEntry(String serial1, String serial2, String mac, Integer estadoId) {
    ingresoRepository.updateChangedEntry(serial1, serial2, mac, estadoId);
  }

  public void updateClasificationEntry(String serial, Integer estadoId, Integer nivelId, Integer usuarioId) {
    ingresoRepository.updateClasificationEntry(serial, estadoId, nivelId, usuarioId);
  }

  public void updateStateOneEntry(Integer estadoId, Integer nivelId, Integer usuarioIdMovimiento, Date fecha,
      String serial) {
    ingresoRepository.updateStateOneEntry(estadoId, nivelId, usuarioIdMovimiento, fecha, serial);
  }

  public void updateStateEntryNotUsuario(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuario(estadoId, palletId, fecha, serial);
  }

  public void updateStateEntryNotUsuarioRepaired(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuarioRepaired(estadoId, palletId, fecha, serial);
  }

  public void updateStateEntryNotUsuarioDiagnosed(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuarioDiagnosed(estadoId, palletId, fecha, serial);
  }

  // ── Packing ───────────────────────────────────────────────────────────────────

  public void updatePackingEntry(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      Integer usuarioIdMovimiento, String serial, Integer loteId) {
    Integer loteIdFinal = (loteId != null && loteId == 0) ? null : loteId;
    ingresoRepository.UpdatePackingEntry(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento, serial, loteIdFinal, 4);
  }

  public void updatePackingEntrySmartCard(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      Integer usuarioIdMovimiento, String serial, Integer loteId, Integer smartCardId, String smartCard) {
    Integer loteIdFinal = (loteId != null && loteId != 0) ? loteId : null;
    Integer smartCardIdFinal = (smartCardId != null && smartCardId != 0) ? smartCardId : null;
    String smartCardFinal = (smartCard == null || smartCard.trim().isEmpty()) ? "0" : smartCard;
    ingresoRepository.UpdatePackingEntrySmartCard(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento,
        serial, loteIdFinal, smartCardIdFinal, smartCardFinal);
  }

  @Transactional
  public void updatePackingAllEntry(Integer estadoId, Integer usuarioIdMovimiento, List<String> seriales) {
    seriales.forEach(serial -> ingresoRepository.UpdatePackingAllEntry(estadoId, usuarioIdMovimiento, serial));
  }

  public void updateChangedPackingEntry(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      String serial, Integer usuarioIdMovimiento, Integer tipologiaId) {
    ingresoRepository.UpdateChangedPackingEntry(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento,
        serial, tipologiaId, 4);
  }

  public void updateSmartCardEntry(Integer smartCardId, String smartCardNuevo, String serial) {
    ingresoRepository.updateSmartCardEntry(smartCardId, smartCardNuevo, serial, 4);
  }

  public void packOffPalletEntry(Integer palletId, Integer usuarioId) {
    ingresoRepository.packOffPalletEntry(palletId, usuarioId);
  }

  public void packOffBoxEntry(Integer palletId, Integer cajaId, Integer usuarioId) {
    ingresoRepository.packOffBoxEntry(palletId, cajaId, usuarioId);
  }

  // ── Novedades ──────────────────────────────────────────────────────────────────

  public Integer updateNoveltyAllEntry(String serial, Integer estadoId, Integer tipologiaId, String observaciones,
      String novedad, Integer usuarioId, Integer fallaCosmeticaId, Integer fallaFuncionalId) {
    ingresoRepository.updateNoveltyAllEntry(serial, estadoId, tipologiaId, observaciones, novedad, usuarioId,
        fallaCosmeticaId, fallaFuncionalId);
    return 1;
  }

  public Integer updateNoveltyEntry(Integer id, String serial, String mac, Integer codigoSapId, String guia,
      String documento, Integer tipoOrigenId, Integer origenId, Integer tipologiaId, Integer estadoId,
      String tipoNovedad, Integer usuarioId) {
    ingresoRepository.updateNoveltyEntry(id, serial, mac, codigoSapId, guia, documento, tipoOrigenId,
        origenId, tipologiaId, estadoId, tipoNovedad, usuarioId);
    return 1;
  }

  // ── Reparación / scrap ──────────────────────────────────────────────────────────

  public void backRepairedEntry(String serial) {
    ingresoRepository.backRepairedEntry(serial, 0);
  }

  public void updateScrapAll(Integer estadoId, Integer usuarioIdMovimiento, String serial, String novedad) {
    ingresoRepository.updateScrapAll(estadoId, usuarioIdMovimiento, serial, novedad);
  }

  // ── Batch / despacho ────────────────────────────────────────────────────────────

  public void updateStatusBatch(Integer estadoId, Integer palletId, Integer usuarioIdMovimiento, Integer fecha,
      List<String> seriales, Integer loteId) {
    seriales.forEach(s -> ingresoRepository.updateStatusBatch(estadoId, palletId, usuarioIdMovimiento, fecha, s, loteId));
  }

  public void updateEntryDispatch(Integer estadoId, Integer palletId, Integer usuarioIdMovimiento,
      String serial, Integer loteId) {
    Integer loteIdFinal = (loteId != null && loteId == 0) ? null : loteId;
    ingresoRepository.UpdateEntryDispatch(estadoId, palletId, usuarioIdMovimiento, serial, loteIdFinal, 4);
  }

  // ── Inventario ───────────────────────────────────────────────────────────────────

  public void insertSerialInventory(String serial, String codigoSap, String palletNumero, Integer usuarioId) {
    ingresoRepository.insertSerialInventory(serial, codigoSap, palletNumero, usuarioId);
  }

  // ── Helpers privados ─────────────────────────────────────────────────────────────

  /**
   * Mapea un Object[] del query searchIngreso a un IngresoModel.
   * Extrae la lógica duplicada que existía en getModelIngreso y getListModel.
   */
  private IngresoModel mapToIngresoModel(Object[] obj) {
    IngresoModel ingreso = new IngresoModel();
    ingreso.setId((Integer) obj[0]);
    ingreso.setSerial((String) obj[1]);
    ingreso.setMac((String) obj[2]);
    ingreso.setSerial3((String) obj[3]);
    ingreso.setSerial4((String) obj[4]);
    ingreso.setSerial5((String) obj[5]);
    ingreso.setCodigoSap((String) obj[6]);
    ingreso.setCodigoSapId((Integer) obj[7]);
    ingreso.setDescripcion((String) obj[8]);
    ingreso.setEstado((String) obj[9]);
    ingreso.setEstadoId((Integer) obj[10]);
    ingreso.setUsuario((String) obj[11]);
    ingreso.setCajaEmpaqueId((Integer) obj[12]);
    ingreso.setCajaEmpaque((String) obj[13]);
    ingreso.setCajaDespacho((String) obj[14]);
    ingreso.setPallet((String) obj[15]);
    ingreso.setPalletId((Integer) obj[16]);
    ingreso.setPosicion((String) obj[17]);
    ingreso.setNivel((String) obj[18]);
    ingreso.setNivelId((Integer) obj[19]);
    ingreso.setTipoOrigenId((Integer) obj[20]);
    ingreso.setOrigenId((Integer) obj[21]);
    ingreso.setTipologiaId((Integer) obj[22]);
    ingreso.setTipologia((String) obj[23]);
    ingreso.setPalletIdIngreso((Integer) obj[24]);
    ingreso.setFecha(obj[25] != null ? ((Timestamp) obj[25]).toString() : null);
    ingreso.setGarantiaFabricante(TypeMapper.toBoolean(obj[26]));
    ingreso.setFalla((String) obj[27]);
    ingreso.setLoteId((Integer) obj[28]);
    ingreso.setPalletIdEmpaque((Integer) obj[29]);
    ingreso.setLote((String) obj[30]);
    ingreso.setSmartCardId((Integer) obj[31]);
    ingreso.setSmartCard((String) obj[32]);
    ingreso.setCajaIngresoId((Integer) obj[33]);
    ingreso.setCajaIngreso((String) obj[34]);
    ingreso.setModeloId((Integer) obj[35]);
    ingreso.setModelo((String) obj[36]);
    return ingreso;
  }

  /**
   * Mapea un Object[] del query getModelDispatch a un IngresoModel.
   * Extrae la lógica duplicada entre getModelDispatch y getModelDispatchBox.
   */
  private IngresoModel mapToDispatchModel(Object[] obj) {
    IngresoModel ingreso = new IngresoModel();
    ingreso.setId((Integer) obj[0]);
    ingreso.setSerial((String) obj[1]);
    ingreso.setMac((String) obj[2]);
    ingreso.setCodigoSapId((Integer) obj[3]);
    ingreso.setPalletId((Integer) obj[4]);
    ingreso.setPalletIdIngreso((Integer) obj[5]);
    ingreso.setCajaDespachoId((Integer) obj[6]);
    ingreso.setTipoOrigenId((Integer) obj[7]);
    ingreso.setOrigenId((Integer) obj[8]);
    ingreso.setTipologiaId((Integer) obj[9]);
    ingreso.setNivelId((Integer) obj[10]);
    ingreso.setTramite((String) obj[11]);
    ingreso.setDocumento((String) obj[12]);
    ingreso.setGuia((String) obj[13]);
    ingreso.setFalla((String) obj[14]);
    ingreso.setPrealertaId((Integer) obj[15]);
    ingreso.setCruce(TypeMapper.toBoolean(obj[16]));
    ingreso.setNovedad((String) obj[17]);
    ingreso.setFecha(obj[18] != null ? ((Timestamp) obj[18]).toString() : null);
    ingreso.setSmartCard((String) obj[19]);
    ingreso.setSmartCardId((Integer) obj[20]);
    ingreso.setLoteId((Integer) obj[21]);
    ingreso.setSerial3((String) obj[22]);
    ingreso.setCajaIngresoId((Integer) obj[23]);
    ingreso.setNumeroSmartcard((String) obj[24]);
    ingreso.setFallaCosmeticaId((Integer) obj[25]);
    ingreso.setFallaFuncionalId((Integer) obj[26]);
    if (obj.length > 27) {
      ingreso.setCausa((String) obj[27]);
    }
    return ingreso;
  }

  /**
   * Convierte un valor Object a String, devolviendo "" si es null o "null".
   */
  private String toStr(Object value) {
    if (value == null) return "";
    String str = String.valueOf(value).trim();
    return "null".equalsIgnoreCase(str) ? "" : str;
  }
}
