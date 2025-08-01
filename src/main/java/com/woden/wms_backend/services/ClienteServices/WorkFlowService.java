package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.WorkFlowRepository;

@Service
public class WorkFlowService {
  @Autowired
  private WorkFlowRepository repository;

  public Integer getLevelId(String opcion, String origen, String descripcion, int tipologiaId) {
    return repository.getLevelId(opcion, origen, descripcion, tipologiaId);
  }
}
