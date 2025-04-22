package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woden.wms_backend.models.Entity.UsuarioSysModel;
import com.woden.wms_backend.services.ClienteServices.UsuarioSysService;

@RestController
@RequestMapping("/client/usuariosys")
public class UsuarioSysController {

  @Autowired
  private UsuarioSysService usuarioSysService;

  @GetMapping("/{id}")
  public ResponseEntity<UsuarioSysModel> getUsuarioById(@PathVariable int id) {
    UsuarioSysModel usuario = usuarioSysService.getUsuarioById(id);
    if (usuario != null) {
      return ResponseEntity.ok(usuario);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
