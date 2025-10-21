package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioClientePerfilRepository;

@Service
public class UsuarioClientePerfilService {

  private UsuarioClientePerfilRepository usuarioClientePerfilRepository;

  public UsuarioClientePerfilService(UsuarioClientePerfilRepository usuarioClientePerfilRepository) {
    this.usuarioClientePerfilRepository = usuarioClientePerfilRepository;
  }

  public Integer getIdUsuarioClientePerfil(Integer usuarioClienteId) {
    Integer id = usuarioClientePerfilRepository.getIdUsuarioClientePerfil(usuarioClienteId);
    return id;
  }
}