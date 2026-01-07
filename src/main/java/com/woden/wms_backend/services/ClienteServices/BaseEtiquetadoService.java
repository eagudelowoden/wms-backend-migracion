package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.BaseDTO;
import com.woden.wms_backend.models.Entity.BaseEtiquetadoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseEtiquetadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class BaseEtiquetadoService extends BaseService<BaseEtiquetadoModel, Integer> {
  @Autowired
  private BaseEtiquetadoRepository baseEtiquetadoRepository;

  public BaseDTO getModelBase(String base, String serial) {
    List<Object[]> results = baseEtiquetadoRepository.getModelBase(base, serial);
    Object[] etiqueta = results.get(0);
    BaseDTO model = new BaseDTO();
    model.setSerial(etiqueta[0].toString());
    model.setCodigoSap(etiqueta[1].toString());
    model.setEstadoSap(etiqueta[2].toString());
    model.setEstadoRR(etiqueta[3].toString());
    return model;
  }

  public Integer getCountBase(String base) {
    return baseEtiquetadoRepository.getCountBase(base);
  }
}
