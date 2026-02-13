package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.ParteModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ParteRepositoy;
import com.woden.wms_backend.services.BaseService;

@Service
public class ParteService extends BaseService<ParteModel, Integer> {
  @Autowired
  private final ParteRepositoy parteRepositoy;

  public ParteService(ParteRepositoy parteRepositoy) {
    this.parteRepositoy = parteRepositoy;
  }

  public Integer sendPart(Integer opcion, Integer estadoId, Integer usuarioId, Integer palletId) {
    try {
      Integer filas = 4;
      Integer filasAfectadas = parteRepositoy.sendPart(opcion, estadoId, usuarioId, palletId, filas);
      return filasAfectadas;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
