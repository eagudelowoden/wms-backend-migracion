package com.woden.wms_backend.services.ClienteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.UsuarioSysModel;
import com.woden.wms_backend.repositories.ClienteRepositories.UsuarioSysRepository;

@Service
public class UsuarioSysService {

  @Autowired
  private UsuarioSysRepository usuarioSysRepository;

  public UsuarioSysModel getUsuarioById(int id) {
    return usuarioSysRepository.findByIdUsuarioSys(id);
  }

  @Transactional
  public void syncUsuarioSys(int id, String nombreUsuario, String nombres, int perfilId) {
    usuarioSysRepository.syncUsuarioSys(id, nombres, nombreUsuario, perfilId);
  }
}
