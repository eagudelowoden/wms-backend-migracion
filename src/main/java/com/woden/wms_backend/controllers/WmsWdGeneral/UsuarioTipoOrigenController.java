package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.UsuarioTipoOrigenDTO;
import com.woden.wms_backend.services.WmsWdGeneral.UsuarioTipoOrigenService;

@RestController
@RequestMapping("/general/usuario-tipo-origen")
public class UsuarioTipoOrigenController {

  @Autowired
  private UsuarioTipoOrigenService usuarioTipoOrigenService;

  @GetMapping("/{usuarioId}/{clienteId}")
  public ResponseEntity<List<UsuarioTipoOrigenDTO>> getListAssigned(@PathVariable int usuarioId, @PathVariable int clienteId) {
    List<UsuarioTipoOrigenDTO> tipoOrigenes = usuarioTipoOrigenService.getListAssigned(usuarioId, clienteId);
    System.out.println("tipoOrigenes: " + tipoOrigenes);
    return ResponseEntity.ok(tipoOrigenes);
  }
}
