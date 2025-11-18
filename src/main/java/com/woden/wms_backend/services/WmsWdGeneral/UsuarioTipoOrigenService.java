package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.UsuarioTipoOrigenDTO;
import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioTipoOrigenRepository;

@Service
public class UsuarioTipoOrigenService {

  @Autowired
  private UsuarioTipoOrigenRepository usuarioTipoOrigenRepository;

  public UsuarioTipoOrigenService(UsuarioTipoOrigenRepository usuarioTipoOrigenRepository) {
    this.usuarioTipoOrigenRepository = usuarioTipoOrigenRepository;
  }

  public List<String> getListAssigned(int usuarioId, int clienteId) {
    List<Object[]> results = usuarioTipoOrigenRepository.getListAssigned(usuarioId, clienteId);

    return results.stream()
        .map(obj -> (String) obj[1]) // Convertir cada fila a String
        .collect(Collectors.toList());
  }

}
