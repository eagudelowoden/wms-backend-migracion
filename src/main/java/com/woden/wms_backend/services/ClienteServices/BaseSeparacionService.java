package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.BaseSeparacion;
import com.woden.wms_backend.repositories.ClienteRepositories.BaseSeparacionRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class BaseSeparacionService extends BaseService<BaseSeparacion, Integer>{
  @Autowired
  private BaseSeparacionRepository baseSeparacionRepository;

  public List<BaseSeparacion> getModelBase(String base, String serial){
    List<Object[]> results = baseSeparacionRepository.getModelBase(base, serial);

    return results.stream().map(obj -> {
      BaseSeparacion baseSeparacion = new BaseSeparacion();
      baseSeparacion.setSerial((String) obj[1]);
      baseSeparacion.setCodigoSap((String) obj[2]);
      baseSeparacion.setEstadoSap((String) obj[3]);
      baseSeparacion.setEstadoRR((String) obj[4]);
      return baseSeparacion;
    }).collect(Collectors.toList());
  }
}
