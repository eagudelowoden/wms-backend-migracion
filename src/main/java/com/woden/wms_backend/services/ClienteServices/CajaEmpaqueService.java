package com.woden.wms_backend.services.ClienteServices;

import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.dto.*;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.*;
//import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.BaseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
//import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CajaEmpaqueService extends BaseService<CajaEmpaqueModel, Integer> {

  @Autowired
  private CajaEmpaqueRepository cajaEmpaqueRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private IngresoRepository ingresoRepository;

  @Autowired
  private EmpaqueRepository empaqueRepository;

  /** Estado con el que queda una caja al ser rechazada en Calidad. */
  private static final int ESTADO_CAJA_RECHAZADA = 38;

  public List<CajaEmpaqueDTO> searchRejectedPallets() {
    List<Object[]> results = cajaEmpaqueRepository.searchRejectedPallets(ESTADO_CAJA_RECHAZADA);
    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setCantidad(obj[2] != null ? ((Number) obj[2]).intValue() : 0);
      return caja;
    }).collect(Collectors.toList());
  }

  public List<CajaEmpaqueDTO> searchRejectedBoxes(Integer palletId) {
    List<Object[]> results = cajaEmpaqueRepository.searchRejectedBoxes(palletId, ESTADO_CAJA_RECHAZADA);
    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setSeriales(obj[2] != null ? ((Number) obj[2]).intValue() : 0);
      caja.setPallet((String) obj[3]);
      caja.setEstado("Empaque");
      return caja;
    }).collect(Collectors.toList());
  }

  /** Cajas de un pallet excluyendo las rechazadas (estado 38) — para la vista de Calidad. */
  public List<CajaEmpaqueDTO> searchBoxesNoRejected(Integer palletId) {
    List<Object[]> results = cajaEmpaqueRepository.searchPackingNoRejected(palletId, ESTADO_CAJA_RECHAZADA);
    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setSeriales(obj[2] != null ? ((Number) obj[2]).intValue() : 0);
      return caja;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchBoxSeriales(Integer cajaEmpaqueId) {
    List<Object[]> results = cajaEmpaqueRepository.searchBoxEntry(cajaEmpaqueId);
    List<Map<String, String>> seriales = new ArrayList<>();
    for (Object[] r : results) {
      Map<String, String> fila = new HashMap<>();
      fila.put("serial", r[0] != null ? r[0].toString() : "");
      fila.put("mac", r[1] != null ? r[1].toString() : "");
      fila.put("codigo", r[2] != null ? r[2].toString() : "");
      fila.put("descripcion", r[3] != null ? r[3].toString() : "");
      fila.put("tipologia", r[4] != null ? r[4].toString() : "");
      fila.put("smartCard", r[5] != null ? r[5].toString() : "");
      seriales.add(fila);
    }
    return seriales;
  }

  public void updateStatusBoxPacking(Integer cajaEmpaqueId, Integer estadoId) {
    Integer filas = 0;
    cajaEmpaqueRepository.updateStatusBoxPacking(cajaEmpaqueId, estadoId, filas);
  }

  public List<CajaEmpaqueDTO> SearchReceivePacking(String numero, String pallet) {
    List<Object[]> results = cajaEmpaqueRepository.SearchReceivePacking(numero, pallet);

    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setPallet((String) obj[2]);
      caja.setCantidad((Integer) obj[3]);
      return caja;
    }).collect(Collectors.toList());
  }

  public List<CajaEmpaqueDTO> SearchProcessBoxPacking(Integer palletId, Integer cajaEmpaqueId, String estado) {
    List<Object[]> results = cajaEmpaqueRepository.SearchProcessBoxPacking(palletId, cajaEmpaqueId, estado);

    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setSeriales((Integer) obj[2]);
      caja.setEstado((String) obj[3]);
      caja.setPallet((String) obj[4]);
      return caja;
    }).collect(Collectors.toList());
  }

  public List<CajaEmpaqueDTO> SearchPacking(Integer palletId) {
    List<Object[]> results = cajaEmpaqueRepository.SearchPacking(palletId);

    return results.stream().map(obj -> {
      CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
      caja.setId((Integer) obj[0]);
      caja.setNumero((String) obj[1]);
      caja.setSeriales((Integer) obj[2]);
      return caja;
    }).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<String> getSerialesByPallet(Integer palletId) {
    Query query = entityManager.createNativeQuery("EXEC pa_SerialesByPallet :palletId");
    query.setParameter("palletId", palletId);
    return query.getResultList();
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

  public List<Map<String, String>> searchPalletBoxEntry(String estado, Integer palletId, Integer cajaEmpaqueId) {
    List<Object[]> results = ingresoRepository.searchPalletBoxEntry(estado, palletId, cajaEmpaqueId);
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
      // estos metodos se hacen para mostrar los valores

      serialesBOx.add(seriales);
    }
    return serialesBOx;
  }

  public List<Map<String, String>> searchPalletBoxValidate(String estado, Integer palletId, Integer cajaEmpaqueId) {
    // 1. Llamamos al NUEVO repositorio que apunta al nuevo SP
    List<Object[]> results = ingresoRepository.searchPalletBoxValidate(estado, palletId, cajaEmpaqueId);
    List<Map<String, String>> serialesBOx = new ArrayList<>();

    for (Object[] result : results) {
      Map<String, String> seriales = new HashMap<>();

      // Mapeo técnico original (Índices 0 al 10)
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

      // NUEVOS CAMPOS DE VALIDACIÓN (Basados en el SP de Daniel Agudelo)
      // 11 -> existeEnEmpaque (1 o 0)
      seriales.put("existeEnEmpaque", result[11] != null ? result[11].toString() : "0");

      // DATOS LEGIBLES DE INGRESO (Índices 12 y 13)
      seriales.put("numPalletIngreso", result[12] != null ? result[12].toString() : "");
      seriales.put("numCajaIngreso", result[13] != null ? result[13].toString() : "");

      // DATOS LEGIBLES DE EMPAQUE (Índices 14 y 15)
      seriales.put("numPalletEmpaque", result[14] != null ? result[14].toString() : "N/A");
      seriales.put("numCajaEmpaque", result[15] != null ? result[15].toString() : "N/A");

      serialesBOx.add(seriales);
    }
    return serialesBOx;
  }


  public void create(String numero, Integer palletId, Integer estadoId,
      Integer usuarioId, LocalDateTime fecha) {
    cajaEmpaqueRepository.create(numero, palletId, estadoId, usuarioId, fecha);
  }

  public int eliminarCaja(Integer cajaEmpaqueId) {
    return cajaEmpaqueRepository.eliminarCaja(cajaEmpaqueId);
  }

  public void createEmpaque(EmpaqueModel empaque) {
    try {
      // ✅ Si el loteId es 0, lo mando como null
      Integer loteIdParam = (empaque.getLoteId() != 0) ? empaque.getLoteId() : null;
      empaqueRepository.createInsert(
          empaque.getSerialId(),
          empaque.getSerial(),
          empaque.getMac(),
          empaque.getCodigoSapId(),
          empaque.getPalletId(),
          empaque.getCajaEmpaqueId(),
          empaque.getNivelId(),
          empaque.getUsuarioId(),
          empaque.getFecha(),
          loteIdParam,
          empaque.getSmartCardId(),
          empaque.getSmartCard());
      logger.info("Guardado correctamente");
    } catch (Exception e) {
      logger.error("Error al insertar empaque: {}", e.getMessage(), e);
    }
  }

  public Integer getCountBoxPacking(Integer cajaEmpaqueId) {
    return cajaEmpaqueRepository.getCountBoxPacking(cajaEmpaqueId);
  }

  public Integer updateStatusAllBoxPacking(Integer palletId, Integer estadoId) {
    Integer filas = 4;
    try {
      Integer result = cajaEmpaqueRepository.updateStatusAllBoxPacking(palletId, estadoId, filas);
      return result != null && result > 0 ? 1 : 0;
    } catch (Exception e) {
      return 0;
    }
  }

  public Integer getLastBoxPacking(Integer palletId) {
    return cajaEmpaqueRepository.getLastBoxPacking(palletId);
  }

}
