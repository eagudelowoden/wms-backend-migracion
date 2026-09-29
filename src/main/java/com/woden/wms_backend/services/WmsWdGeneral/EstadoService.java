package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.WmsWdGeneral.EstadoModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.EstadoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class EstadoService extends BaseService<EstadoModel, Integer> {
  private final EstadoRepository estadoRepository;

  public EstadoService(EstadoRepository estadoRepository) {
    this.estadoRepository = estadoRepository;
  }

  public Integer getIdByNombre(String nombre) {
    return estadoRepository.getIdByNombre(nombre);
  }

  public String getNameById(int id) {
    return estadoRepository.getNameById(id);
  }
}