package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.BaseInventarioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseIngresoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class BaseInventarioService extends BaseService<BaseInventarioModel, Integer> {
  @Autowired
  private BaseIngresoRepository baseIngresoRepository;

  public BaseInventarioModel getModel(String base, String serial) {
    List<Object[]> results = baseIngresoRepository.getModel(base, serial);
    if (results.isEmpty()) {
      return null;
    }
    Object[] row = results.get(0);
    BaseInventarioModel model = new BaseInventarioModel();
    model.setSerial(row[0].toString());
    model.setCodigoSap(row[1].toString());
    model.setEstadoSap(row[2].toString());
    model.setEstadoRR(row[3].toString());
    return model;
  }
}
