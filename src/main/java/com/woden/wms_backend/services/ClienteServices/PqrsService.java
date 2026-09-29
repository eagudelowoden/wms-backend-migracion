package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PqrsDetalleModel;
import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PqrsDetalleRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PqrsRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PqrsService extends BaseService<PqrsModel, Integer> {

  @Autowired
  private PqrsRepository pqrsRepository;

  @Autowired
  private PqrsDetalleRepository pqrsDetalleRepository;

  public void updatePqrs(
      Integer estadoDiagnosticoId,
      Integer fallaDiagnosticoId,
      String name,
      String XStudioDiagnosticoTecnicoWoden) {
    pqrsRepository.updatePqrs(estadoDiagnosticoId, fallaDiagnosticoId, name, XStudioDiagnosticoTecnicoWoden);
  }

  public PqrsModel getModelPqrs(String serial, Integer stage) {
    return pqrsRepository.getModelPqrs(serial, stage);
  }

  public void updateSerialIdAndSapCodeIdPqrs(Integer serialId, Integer codigoSapId, String serial) {
    Integer filas = 4;
    pqrsRepository.updateSerialIdAndSapCodeIdPqrs(serialId, codigoSapId, serial, filas);
  }

  public void updateObservationPqrs(Integer serialId, String serial, String observacion) {
    Integer filas = 4;
    pqrsRepository.updateObservationPqrs(serialId, serial, observacion, filas);
  }

  /**
   * Detalle de PQRS — reemplazo de getModelPqrs() sobre la tabla PQRS obsoleta.
   * Busca primero en App_PQRS_Tickets y, si no hay match, en App_PQRS_Truckrolls.
   * Devuelve null si el serial no existe en ninguna de las dos.
   */
  public PqrsDetalleModel buscarDetallePqrs(String serial) {
    return pqrsDetalleRepository.buscarDetalle(serial);
  }
}
