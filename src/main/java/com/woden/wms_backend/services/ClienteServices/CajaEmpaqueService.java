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
          loteIdParam, // 👈 agregado
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

}
