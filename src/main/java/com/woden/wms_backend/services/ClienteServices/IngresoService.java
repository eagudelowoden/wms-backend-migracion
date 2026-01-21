package com.woden.wms_backend.services.ClienteServices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.dto.clientDTO.IngresoModelDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IlegibleRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class IngresoService extends BaseService<IngresoModel, Integer> {
  @Autowired
  private IngresoRepository ingresoRepository;
  private IlegibleRepository ilegibleRepository;
  private ConsecutiveService consecutiveService;

  @Autowired
  private DataSource dataSource;

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

      stmt.close();
      conn.close();

      if (codigo == 0)
        return null; // éxito
      return mensaje != null ? mensaje : "Error desconocido";

    } catch (SQLException e) {
      return "EXCEPCION: " + e.getMessage();
    }
  }

  @Transactional
  public int eliminarIngresos(List<String> seriales) {
    int count = 0;
    for (String serial : seriales) {
      ingresoRepository.eliminarIngresos(serial);
      count++;
    }

    return count > 0 ? 1 : 0;
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

  public IngresoModel getModelIngreso(String serial) {
    try {
      List<Object[]> results = ingresoRepository.searchIngreso(serial);
      if (results.isEmpty()) {
        return null;
      }

      Object[] obj = results.get(0);
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
    } catch (Exception e) {
      logger.error("Error: {}", e.getMessage());
      logger.error("Error: ", e);
      logger.error("Error al obtener el ingreso para serial {}: {}", serial, e.getMessage(), e);
      throw new RuntimeException("Error al ejecutar procedimiento: " + e.getMessage(), e);
    }
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
    // long end = System.currentTimeMillis();
    // log.info("Tiempo en mapear a modelo: {} ms", (end - afterQuery));
    // log.info("Tiempo total en servicio: {} ms", (end - start));

    return ingreso;
    // try {

    // } catch (Exception e) {
    // long end = System.currentTimeMillis();
    // // log.info("Tiempo total en servicio: {} ms", (end - start));
    // return null;
    // }
  }

  // public String getModelIngresoV2(String serial) {
  // long start = System.currentTimeMillis();
  // String ingreso = ingresoRepository.searchIngresoV2(serial);
  // long end = System.currentTimeMillis();
  // log.info("Tiempo total en servicio V2: {} ms", (end - start));
  // return ingreso;
  // }

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

  public void sendEntry(Integer estadoId, Integer tipologiaId, Integer usuarioId, Integer palletId,
      Integer opcion) {
    Integer filas = 0;
    ingresoRepository.sendIngreso(estadoId, tipologiaId, usuarioId, palletId, opcion, filas);
  }

  public List<IngresoTransitoDTO> getIngresoTransitByPalletId(Integer palletId) {
    List<Object[]> results = ingresoRepository.searchIngresoTransito(palletId);
    List<IngresoTransitoDTO> response = new ArrayList<>();

    for (Object[] row : results) {
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
      response.add(dto);
    }

    return response;
  }

  public void regularizarSap(String serial, int codigoSapId, int usuarioIdMovimiento) {
    ingresoRepository.updateSapCode(codigoSapId, usuarioIdMovimiento, serial, 1);
  }

  public void regularizarLoteSerial(String serial, int loteId, int usuarioIdMovimiento) {
    ingresoRepository.updateBatchSerial(loteId, usuarioIdMovimiento, serial, 1);
  }

  public String generarIngresoIlegible(IngresoIlegibleDTO ingresoDTO, String cliente, Integer usuarioId) {
    String consecutivo = consecutiveService.getConsecutive(cliente);

    String serialIlegible = (consecutivo == null) ? "1"
        : String.valueOf(Integer.parseInt(consecutivo.substring(6)) + 1);

    String prefijo;
    switch (cliente.toUpperCase()) {
      case "CLARO":
        prefijo = "ILE-C-";
        break;
      case "ETB":
        prefijo = "ILE-E-";
        break;
      case "TIGO BOGOTA":
      case "TIGO COSTA RICA":
      case "TIGO MEDELLIN":
      case "TIGO SALVADOR":
      case "TIGO PANAMA":
        prefijo = "ILE-T-";
        break;
      case "RED EXTERNA":
        prefijo = "ILE-R-";
        break;
      case "MOVIL":
        prefijo = "ILE-M-";
        break;
      case "HUGHESNET":
        prefijo = "ILE-H-";
        break;
      case "DIRECTV":
      case "TIGO CORPORATIVO PA":
      case "TIGO CORPORATIVO CR":
        prefijo = "ILE-D-";
        break;
      default:
        prefijo = "ILE-" + cliente.charAt(0) + "-";
        break;
    }

    String serial = prefijo + serialIlegible;
    String mac = "MAC-ILEGIBLE-" + serialIlegible;
    Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

    // Insertar en la base de datos
    int status = ilegibleRepository.insertIlegible(serial, mac, 0, fechaActual);

    return (status == 1) ? serial : null;
  }

  public String getSerialByMac(String mac) {
    List<Object[]> results = ingresoRepository.getSerialByMac(mac);
    return (results.size() > 0) ? (String) results.get(0)[0] : null;
  }

  @PersistenceContext
  private EntityManager entityManager;

  public Integer getReingresos(String serial) {
    Integer results = ingresoRepository.getReingresos(serial);
    return results;
  }

  public Integer UpdateSapCode(int codigoSapId, int usuarioIdMovimiento, String serial) {
    Integer results = ingresoRepository.updateSapCode(codigoSapId, usuarioIdMovimiento, serial, 0);
    return results;
  }

  public void SendStorageEntry(Integer palletId, Integer estadoId, Integer tipologiaId, Integer usuarioId) {
    Integer filas = 0; // aquí el OUT lo usamos de forma simbólica
    ingresoRepository.SendStorageEntry(estadoId, tipologiaId, usuarioId, palletId, filas);
  }

  public void updateTipologia(Integer palletId, Integer tipologiaId) {
    ingresoRepository.updateTipologyEntry(palletId, tipologiaId, 0);
  }

  public void unifyEntry(Integer palletIdDestino, Integer tipologiaId, Integer usuarioId, List<Integer> palletId) {
    palletId.forEach(id -> ingresoRepository.unifyEntry(palletIdDestino, tipologiaId, usuarioId, id));
  }

  public void updatePalletEntry(Integer palletId, Integer usuarioIdMovimiento, List<String> seriales) {
    seriales.forEach(serial -> ingresoRepository.updatePalletEntry(palletId, usuarioIdMovimiento, serial));
  }

  public void updatePalletAndTipologyEntry(Integer palletId, Integer tipologiaId, Integer usuarioIdMovimiento,
      Integer estadoId, List<String> seriales) {
    seriales.forEach(serial -> ingresoRepository.updatePalletAndTipologyEntry(palletId, tipologiaId,
        usuarioIdMovimiento, estadoId, serial));
  }

  public List<String> getLevelEntry(String serial) {
    List<Object[]> results = ingresoRepository.getLevelEntry(serial);
    List<String> levelEntry = new ArrayList<>();
    results.forEach(result -> levelEntry.add((String) result[0]));
    return levelEntry;
  }

  public Integer getProactiveRepair(String serial) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_GetProactiveRepair(?, ?)}");

      // Parámetro de entrada
      stmt.setString(1, serial);

      // Parámetro de salida
      stmt.registerOutParameter(2, Types.INTEGER);

      stmt.execute();

      Integer filas = stmt.getInt(2);

      stmt.close();
      conn.close();

      return filas;
    } catch (SQLException e) {
      System.err.println("Error en getProactiveRepairCallable: " + e.getMessage());
      return 0;
    }
  }

  public List<String> getProactiveRepairAll() {
    List<String> seriales = ingresoRepository.getProactiveRepairAll();
    return seriales;
  }

  public void updateStateAllEntry(Integer estadoId, Integer usuarioId, String serial) {
    ingresoRepository.updateStateAllEntry(estadoId, usuarioId, serial);
  }

  public List<Map<String, String>> searchDiagnosticEntry(String estadoFinal, String perfil, Integer usuarioId) {
    List<Object[]> results = ingresoRepository.searchDiagnosticEntry(estadoFinal, perfil, usuarioId);
    List<Map<String, String>> diagnosticEntries = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      entry.put("falla", (String) result[4]);
      entry.put("estado", (String) result[5]);
      diagnosticEntries.add(entry);
    }
    return diagnosticEntries;
  }

  public List<String> searchDeliveryEntry(String serial) {
    List<String> results = ingresoRepository.searchDeliveryEntry(serial);
    return results.isEmpty() ? null : results;
  }

  public void updateLevel(Integer levelId, Integer palletId) {
    Integer filas = 0;
    ingresoRepository.updateLevel(levelId, palletId, filas);
  }

  public void updateStateEntryNotUsuario(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuario(estadoId, palletId, fecha, serial);
  }

  public void updateChangedEntry(String serial1, String serial2, String mac, Integer estadoId) {
    ingresoRepository.updateChangedEntry(serial1, serial2, mac, estadoId);
  }

  public void updateClasificationEntry(String serial, Integer estadoId, Integer nivelId, Integer usuarioId) {
    ingresoRepository.updateClasificationEntry(serial, estadoId, nivelId, usuarioId);
  }

  public void updateStateEntry(Integer estadoId, Integer palletId, Integer usuarioId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntry(estadoId, palletId, usuarioId, fecha, serial);
  }

  public List<Map<String, String>> searchClasificationEntry() {
    List<Object[]> results = ingresoRepository.searchClasificationEntry();
    List<Map<String, String>> clasificationEntries = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      clasificationEntries.add(entry);
    }
    return clasificationEntries;
  }

  public void updateStateOneEntry(Integer estadoId, Integer nivelId, Integer usuarioIdMovimiento, Date fecha,
      String serial) {
    ingresoRepository.updateStateOneEntry(estadoId, nivelId, usuarioIdMovimiento, fecha, serial);
  }

  public int UpdatePackingEntry(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      Integer usuarioIdMovimiento, String serial, Integer loteId) {
    try {
      Integer filas = 4;
      if (loteId == 0) {
        loteId = null;
      }
      ingresoRepository.UpdatePackingEntry(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento, serial, loteId,
          filas);
      return 1; // ✅ éxito
    } catch (Exception e) {
      System.err.println("Error en UpdatePackingEntry: " + e.getMessage());
      e.printStackTrace();
      return 0;
    }
  }

  public void UpdatePackingEntrySmartCard(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      Integer usuarioIdMovimiento,
      String serial, Integer loteId, Integer smartCardId, String smartCard) {
    // 🔹 Normalización de parámetros para evitar conflicto FK
    Integer loteIdFinal = (loteId != null && loteId != 0) ? loteId : null;
    Integer smartCardIdFinal = (smartCardId != null && smartCardId != 0) ? smartCardId : null;
    String smartCardFinal = (smartCard == null || smartCard.trim().isEmpty()) ? "0" : smartCard;
    ingresoRepository.UpdatePackingEntrySmartCard(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento, serial,
        loteIdFinal,
        smartCardIdFinal, smartCardFinal);
  }

  @Transactional
  public int UpdatePackingAllEntry(Integer estadoId, Integer usuarioIdMovimiento, List<String> seriales) {
    int count = 0;

    for (String serial : seriales) {
      // Llamada al repositorio pasando los parámetros requeridos
      ingresoRepository.UpdatePackingAllEntry(estadoId, usuarioIdMovimiento, serial);
      count++;
    }
    return count > 0 ? 1 : 0;
  }

  public void updateChangedPackingEntry(Integer estadoId, Integer palletId, Integer cajaEmpaqueId,
      String serial, Integer usuarioIdMovimiento, Integer tipologiaId) {
    Integer filas = 4;
    ingresoRepository.UpdateChangedPackingEntry(estadoId, palletId, cajaEmpaqueId, usuarioIdMovimiento, serial,
        tipologiaId, filas);
  }

  public void updateSmartCardEntry(Integer smartCardId, String smartCardNuevo,
      String serial) {
    Integer filas = 4;
    ingresoRepository.updateSmartCardEntry(smartCardId, smartCardNuevo, serial, filas);
  }

  public void backRepairedEntry(String serial) {
    Integer filas = 0;
    ingresoRepository.backRepairedEntry(serial, filas);
  }

  public List<Map<String, String>> searchRepairEntry(String estadoFinal, String perfil, Integer usuarioId) {
    List<Object[]> results = ingresoRepository.searchRepairEntry(estadoFinal, perfil, usuarioId);
    List<Map<String, String>> repairEntries = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      entry.put("estado", (String) result[4]);
      repairEntries.add(entry);
    }
    return repairEntries;
  }

  public void updateStateEntryNotUsuarioRepaired(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuarioRepaired(estadoId, palletId, fecha, serial);
  }

  public void updateStateEntryNotUsuarioDiagnosed(Integer estadoId, Integer palletId, Integer fecha, String serial) {
    ingresoRepository.updateStateEntryNotUsuarioDiagnosed(estadoId, palletId, fecha, serial);
  }

  public List<Map<String, String>> searchPalletBoxEntry(String estado, Integer palletId, Integer cajaId) {
    List<Object[]> results = ingresoRepository.searchPalletBoxEntry(estado, palletId, cajaId);
    List<Map<String, String>> serialesBOx = new ArrayList<>();

    for (Object[] result : results) {
      Map<String, String> seriales = new HashMap<>();
      seriales.put("serial", result[0] != null ? result[0].toString() : "");
      seriales.put("mac", result[1] != null ? result[1].toString() : "");
      seriales.put("smartCard", result[2] != null ? result[2].toString() : "");
      seriales.put("numeroSmartcard", result[3] != null ? result[3].toString() : "");
      seriales.put("serial3", result[4] != null ? result[4].toString() : "");
      seriales.put("serial4", result[5] != null ? result[5].toString() : "");
      seriales.put("codigo", result[6] != null ? result[6].toString() : "");
      seriales.put("descripcion", result[7] != null ? result[7].toString() : "");
      seriales.put("nivel", result[8] != null ? result[8].toString() : "");
      seriales.put("lote", result[9] != null ? result[9].toString() : "");
      seriales.put("modelo", result[10] != null ? result[10].toString() : "");

      serialesBOx.add(seriales);
    }
    return serialesBOx;
  }

  public List<IngresoModel> getListModel(List<String> seriales) {
    List<IngresoModel> ingresos = new ArrayList<>();

    for (String serial : seriales) {
      List<Object[]> results = ingresoRepository.searchIngreso(serial);

      for (Object[] obj : results) {
        IngresoModel ingreso = new IngresoModel();

        ingreso.setId((Integer) obj[0]);
        ingreso.setSerial((String) obj[1]);
        ingreso.setMac((String) obj[2]);
        ingreso.setSerial3((String) obj[3]);
        ingreso.setSerial4((String) obj[4]);
        ingreso.setCodigoSap((String) obj[5]);
        ingreso.setCodigoSapId((Integer) obj[6]);
        ingreso.setDescripcion((String) obj[7]);
        ingreso.setEstado((String) obj[8]);
        ingreso.setEstadoId((Integer) obj[9]);
        ingreso.setUsuario((String) obj[10]);
        ingreso.setCajaEmpaqueId((Integer) obj[11]);
        ingreso.setCajaEmpaque((String) obj[12]);
        ingreso.setCajaDespacho((String) obj[13]);
        ingreso.setPallet((String) obj[14]);
        ingreso.setPalletId((Integer) obj[15]);
        ingreso.setPosicion((String) obj[16]);
        ingreso.setNivel((String) obj[17]);
        ingreso.setNivelId((Integer) obj[18]);
        ingreso.setTipoOrigenId((Integer) obj[19]);
        ingreso.setOrigenId((Integer) obj[20]);
        ingreso.setTipologiaId((Integer) obj[21]);
        ingreso.setTipologia((String) obj[22]);
        ingreso.setPalletIdIngreso((Integer) obj[23]);
        ingreso.setFecha(obj[24] != null ? ((Timestamp) obj[24]).toString() : null);
        ingreso.setGarantiaFabricante(TypeMapper.toBoolean(obj[25]));
        ingreso.setFalla((String) obj[26]);
        ingreso.setLoteId((Integer) obj[27]);
        ingreso.setPalletIdEmpaque((Integer) obj[28]);
        ingreso.setLote((String) obj[29]);
        ingreso.setSmartCardId((Integer) obj[30]);
        ingreso.setSmartCard((String) obj[31]);
        ingreso.setCajaIngresoId((Integer) obj[32]);
        ingreso.setCajaIngreso((String) obj[33]);
        ingreso.setModeloId((Integer) obj[34]);
        ingreso.setModelo((String) obj[35]);

        ingresos.add(ingreso);
      }
    }

    return ingresos;
  }

  @Transactional
  public void updateDispatchEntry(String serial, Integer estadoId, Integer palletId, Integer cajaDespachoId,
      Integer usuarioMovimientoId, Integer loteId) {
    Integer filas = 4;
    loteId = (loteId != null && loteId == 0) ? null : loteId;
    ingresoRepository.updateDispatchEntry(serial, estadoId, palletId, cajaDespachoId, usuarioMovimientoId, loteId,
        filas);
  }

  public List<Map<String, String>> searchQualityEntry() {
    List<Object[]> results = ingresoRepository.searchQualityEntry();
    List<Map<String, String>> data = new ArrayList<>();

    for (Object[] obj : results) {
      Map<String, String> fila = new HashMap<>();

      fila.put("serial", getValue(obj[0]));
      fila.put("mac", getValue(obj[1]));
      fila.put("pallet", getValue(obj[2]));
      fila.put("caja", getValue(obj[3]));
      fila.put("smartCard", getValue(obj[4]));

      data.add(fila);
    }

    return data;
  }

  /**
   * 🔹 Convierte el valor en String, reemplazando null o "null" por vacío.
   */
  private String getValue(Object value) {
    if (value == null)
      return "";
    String str = String.valueOf(value).trim();
    return "null".equalsIgnoreCase(str) ? "" : str;
  }

  public List<IngresoModel> getModelDispatch(Integer palletId, Integer cajaId) {
    List<IngresoModel> ingresos = new ArrayList<>();
    List<Object[]> results = ingresoRepository.getModelDispatch(palletId, cajaId);
    for (Object[] obj : results) {
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
      ingreso.setCruce((TypeMapper.toBoolean(obj[16])));
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
      ingresos.add(ingreso);
    }
    return ingresos;
  }

  public Integer packOffPalletEntry(Integer palletId, Integer usuarioId) {
    try {
      ingresoRepository.packOffPalletEntry(palletId, usuarioId);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public List<IngresoModel> getModelDispatchBox(Integer palletId, List<Integer> cajasIds) {
    List<IngresoModel> ingresos = new ArrayList<>();
    for (Integer caja : cajasIds) {
      List<Object[]> results = ingresoRepository.getModelDispatch(palletId, caja);
      for (Object[] obj : results) {
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
        ingreso.setCruce((TypeMapper.toBoolean(obj[16])));
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
        ingresos.add(ingreso);
      }
    }
    return ingresos;
  }

  public Integer packOffBoxEntry(Integer palletId, Integer cajaId, Integer usuarioId) {
    try {
      ingresoRepository.packOffBoxEntry(palletId, cajaId, usuarioId);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public int updateLevelWeb(Integer levelId, Integer palletId) {
    Integer result = ingresoRepository.updateLevelWeb(levelId, palletId);
    return (result != null && result > 0) ? 1 : 0;
  }

  public Integer updateNoveltyAllEntry(String serial, Integer estadoId, Integer tipologiaId, String observaciones,
      String novedad, Integer usuarioId, Integer fallaCosmeticaId, Integer fallaFuncionalId) {
    try {
      ingresoRepository.updateNoveltyAllEntry(serial, estadoId, tipologiaId, observaciones, novedad, usuarioId,
          fallaCosmeticaId, fallaFuncionalId);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public List<Map<String, Object>> searchNoveltyEntry(String tipoNovedad) {
    List<Object[]> results = ingresoRepository.searchNoveltyEntry(tipoNovedad);
    List<Map<String, Object>> novedades = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, Object> entry = new HashMap<>();
      entry.put("id", (Integer) result[0]);
      entry.put("serial", (String) result[1]);
      entry.put("mac", (String) result[2]);
      entry.put("codigoSap", (String) result[3]);
      entry.put("descripcion", (String) result[4]);
      entry.put("novedad", (String) result[5]);
      entry.put("observaciones", (String) result[6]);
      novedades.add(entry);
    }
    return novedades;
  }

  public Integer updateNoveltyEntry(Integer id, String serial, String mac,
      Integer codigoSapId, String guia, String documento,
      Integer tipoOrigenId, Integer origenId,
      Integer tipologiaId, Integer estadoId,
      String tipoNovedad, Integer usuarioId) {
    try {
      ingresoRepository.updateNoveltyEntry(id, serial, mac, codigoSapId, guia, documento, tipoOrigenId, origenId,
          tipologiaId, estadoId, tipoNovedad, usuarioId);
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public List<Map<String, String>> searchNoveltyEntryDelivery(String perfil, Integer usuarioId, String tipoNovedad) {
    List<Object[]> results = ingresoRepository.searchNoveltyEntryDelivery(perfil, usuarioId, tipoNovedad);
    List<Map<String, String>> noveltyEntries = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, String> entry = new HashMap<>();
      entry.put("serial", (String) result[0]);
      entry.put("mac", (String) result[1]);
      entry.put("codigoSap", (String) result[2]);
      entry.put("descripcion", (String) result[3]);
      noveltyEntries.add(entry);
    }
    return noveltyEntries;
  }

  public void sendNoveltyEntry(Integer estadoId, Integer tipologiaId, Integer usuarioId, Integer palletId,
      Integer opcion) {
    Integer filas = 0;
    ingresoRepository.sendNoveltyEntry(estadoId, tipologiaId, usuarioId, palletId, opcion, filas);
  }

  public void updateStatusBatch(Integer estadoId, Integer palletId, Integer usuarioIdMovimiento, Integer fecha,
      List<String> serial, Integer loteId) {
    for (String s : serial) {
      ingresoRepository.updateStatusBatch(estadoId, palletId, usuarioIdMovimiento, fecha, s, loteId);
    }
  }

  public int UpdateEntryDispatch(Integer estadoId, Integer palletId,
      Integer usuarioIdMovimiento, String serial, Integer loteId) {
    try {
      Integer filas = 4;
      if (loteId == 0) {
        loteId = null;
      }
      ingresoRepository.UpdateEntryDispatch(estadoId, palletId, usuarioIdMovimiento, serial, loteId,
          filas);
      return 1; // ✅ éxito
    } catch (Exception e) {
      System.err.println("Error en UpdateEntryDispatch: " + e.getMessage());
      e.printStackTrace();
      return 0;
    }
  }
}
