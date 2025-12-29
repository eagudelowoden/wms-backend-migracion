package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.BaseDTO;
import com.woden.wms_backend.models.Entity.BaseIngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseIngresoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class BaseIngresoService extends BaseService<BaseIngresoModel, Integer> {
  @Autowired
  private BaseIngresoRepository baseIngresoRepository;

  public BaseDTO getModel(String base, String serial) {
    List<Object[]> results = baseIngresoRepository.getModel(base, serial);
    if (results.isEmpty()) {
      return null; // O manejar el caso de no encontrar resultados
    }
    Object[] row = results.get(0);
    BaseDTO model = new BaseDTO();
    model.setSerial(row[0].toString());
    model.setCodigoSap(row[1].toString());
    model.setEstadoSap(row[2].toString());
    model.setEstadoRR(row[3].toString());
    model.setLote(row[4] != null ? row[4].toString() : null);
    return model;
  }
}
