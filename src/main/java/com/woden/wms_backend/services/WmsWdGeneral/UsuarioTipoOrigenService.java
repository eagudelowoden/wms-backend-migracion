package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioTipoOrigenRepository;

@Service
public class UsuarioTipoOrigenService {

  @Autowired
  private UsuarioTipoOrigenRepository usuarioTipoOrigenRepository;

  public UsuarioTipoOrigenService(UsuarioTipoOrigenRepository usuarioTipoOrigenRepository) {
    this.usuarioTipoOrigenRepository = usuarioTipoOrigenRepository;
  }


  public List<String> getListAssigned(int usuarioId, int clienteId) {
    return usuarioTipoOrigenRepository.getListAssigned(usuarioId, clienteId);
  }
}
