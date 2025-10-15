package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.BaseDespachoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseDespachoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class BaseDespachoService extends BaseService<BaseDespachoModel, Integer>{
  @Autowired
  private BaseDespachoRepository baseDespachoRepository;

  public List<BaseDespachoModel> getModelBase(String base, String serial){
    List<Object[]> results = baseDespachoRepository.getModelBase(base, serial);

    return results.stream().map(obj -> {
      BaseDespachoModel baseDespacho = new BaseDespachoModel();
      baseDespacho.setSerial((String) obj[1]);
      baseDespacho.setCodigoSap((String) obj[2]);
      baseDespacho.setEstadoSap((String) obj[3]);
      baseDespacho.setEstadoRR((String) obj[4]);
      baseDespacho.setLote((String) obj[5]);
      return baseDespacho;
    }).collect(Collectors.toList());
  }
}
