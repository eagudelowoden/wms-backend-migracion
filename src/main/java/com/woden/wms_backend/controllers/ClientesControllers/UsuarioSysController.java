package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.Map;

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

  @PostMapping("/sync")
  public ResponseEntity<Map<String, String>> syncUsuarioSys(@RequestBody Map<String, Object> body) {
    int id = ((Number) body.get("id")).intValue();
    String nombreUsuario = (String) body.get("nombreUsuario");
    String nombres = (String) body.get("nombres");
    int perfilId = ((Number) body.get("perfilId")).intValue();
    usuarioSysService.syncUsuarioSys(id, nombreUsuario, nombres, perfilId);
    return ResponseEntity.ok(Map.of("message", "Usuario sincronizado."));
  }
}
