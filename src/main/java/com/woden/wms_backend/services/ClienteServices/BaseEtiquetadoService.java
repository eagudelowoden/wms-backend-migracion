package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.BaseDTO;
import com.woden.wms_backend.models.Entity.BaseEtiquetadoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseEtiquetadoRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BaseEtiquetadoService extends BaseService<BaseEtiquetadoModel, Integer> {
  @Autowired
  private BaseEtiquetadoRepository baseEtiquetadoRepository;

  public BaseDTO getModelBase(String base, String serial) {
    List<Object[]> results = baseEtiquetadoRepository.getModelBase(base, serial);

    // Validar que hay resultados
    if (results == null || results.isEmpty()) {
      return null;
    }

    Object[] etiqueta = results.get(0);

    BaseDTO model = new BaseDTO();
    model.setSerial(etiqueta[0] != null ? etiqueta[0].toString() : null);
    model.setCodigoSap(etiqueta[1] != null ? etiqueta[1].toString() : null);
    model.setEstadoSap(etiqueta[2] != null ? etiqueta[2].toString() : null);
    model.setEstadoRR(etiqueta[3] != null ? etiqueta[3].toString() : null);
    if (etiqueta.length > 4 && etiqueta[4] != null) {
      model.setLote(etiqueta[4].toString());
    }

    return model;
  }

  public Integer getCountBase(String base) {
    return baseEtiquetadoRepository.getCountBase(base);
  }
}
