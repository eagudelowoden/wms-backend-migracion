package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.EtiquetaCampoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EtiquetaCampoService extends BaseService<EtiquetaCampoModel, Integer> {
  @Autowired
  private EtiquetaCampoRepository etiquetaCampoRepository;

  public List<EtiquetaCampoModel> getListLabelField(Integer etiquetaId) {
    List<Object[]> results = etiquetaCampoRepository.getListLabelField(etiquetaId);
    return results.stream().map(obj -> {
      EtiquetaCampoModel etiquetaCampo = new EtiquetaCampoModel();
      etiquetaCampo.setNombre((String) obj[0]);
      etiquetaCampo.setValor((String) obj[1]);
      return etiquetaCampo;
    }).toList();
  }
}
