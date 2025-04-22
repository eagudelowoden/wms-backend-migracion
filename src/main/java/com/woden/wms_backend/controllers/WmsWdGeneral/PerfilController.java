package com.woden.wms_backend.controllers.WmsWdGeneral;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.PerfilService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/general/perfil")
public class PerfilController {
  private final PerfilService perfilService;

  public PerfilController(PerfilService perfilService) {
    this.perfilService = perfilService;
  }

  @GetMapping("/nombrePerfil/{usuarioClienteId}")
  public ResponseEntity<Map<String, String>> getNombrePerfil(@PathVariable Integer usuarioClienteId) {
    String nombrePerfil = perfilService.getNameProfile(usuarioClienteId);
    Map<String, String> response = new HashMap<>();
    response.put("perfil", nombrePerfil);
    return ResponseEntity.ok(response);
  }
}
