package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.WmsWdGeneral.PerfilPermisoModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.PerfilPermisoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PerfilPermisoService extends BaseService<PerfilPermisoModel, Integer> {

  private final PerfilPermisoRepository perfilPermisoRepository;

  public PerfilPermisoService(PerfilPermisoRepository perfilPermisoRepository) {
    this.perfilPermisoRepository = perfilPermisoRepository;
  }

  public List<String> obtenerPerfilPermiso(String modulo, String descripcion, int usuarioClientePerfilId) {
    return perfilPermisoRepository.listPerfilPermiso(modulo, descripcion, usuarioClientePerfilId);
  }
}
