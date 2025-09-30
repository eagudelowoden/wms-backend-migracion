package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.controllers.ClientesControllers.JasperReportController;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EmpaqueRepository;
import com.woden.wms_backend.services.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpaqueService extends BaseService<EmpaqueModel, Integer> {

  @Autowired
  private EmpaqueRepository empaqueRepository;

  public EmpaqueService(EmpaqueRepository repository) {
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

  public void createEmpaque(Integer serialId, String serial, String mac, Integer codigoSapId, Integer palletId,
      Integer cajaEmpaqueId, Integer nivelId,
      Integer usuarioId, LocalDateTime fecha, Integer loteId, Integer smartCardId, String smartCard) {
    try {

      Integer loteIdParam = (loteId != 0) ? loteId : null;
      empaqueRepository.createInsert(serialId, serial, mac, codigoSapId, palletId, cajaEmpaqueId, nivelId,
          usuarioId, fecha, loteIdParam, smartCardId, smartCard);
    } catch (Exception e) {
      logger.error("Error al insertar empaque: {}", e.getMessage(), e);
    }
  }

  public int eliminarSeriesEmpaque(List<String> seriales) {
    int count = 0;
    for (String serial : seriales) {
      empaqueRepository.eliminarSeriesEmpaque(serial);
      count++;
    }
    return count > 0 ? 1 : 0;
  }

  public void updateSmartCard(Integer smartCardId, String smartCardNuevo,
      String serial) {
    Integer filas = 4;
    empaqueRepository.updateSmartCard(smartCardId, smartCardNuevo, serial, filas);
  }

  public void UpdatePacking(Integer serialId, String serialNuevo, String mac,
      String serialAnterior) {
    Integer filas = 4;
    empaqueRepository.UpdatePacking(serialId, serialNuevo, mac, serialAnterior, filas);
  }

  public void UpdatePackingWeb(Integer serialId, String serialNuevo, String mac, Integer nivelNuevo,
      String serialAnterior) {
    Integer filas = 0;
    empaqueRepository.UpdatePackingWeb(serialId, serialNuevo, nivelNuevo, mac, serialAnterior, filas);
  }

}
