package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.PerfilModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.PerfilRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PerfilService extends BaseService<PerfilModel, Integer> {

  private PerfilRepository perfilRepository;

  public PerfilService(PerfilRepository perfilRepository) {
    this.perfilRepository = perfilRepository;
  }

  public String getNameProfile(Integer usuarioClienteId) {
    return perfilRepository.getNameProfile(usuarioClienteId);
  }
}
