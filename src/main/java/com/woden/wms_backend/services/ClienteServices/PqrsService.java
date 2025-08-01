package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PqrsRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PqrsService extends BaseService<PqrsModel, Integer>{

  @Autowired
  private PqrsRepository pqrsRepository;

  public void updatePqrs(
      Integer estadoDiagnosticoId,
      Integer fallaDiagnosticoId,
      String name,
      String XStudioDiagnosticoTecnicoWoden) {
    pqrsRepository.updatePqrs(estadoDiagnosticoId, fallaDiagnosticoId, name, XStudioDiagnosticoTecnicoWoden);
  }
}
