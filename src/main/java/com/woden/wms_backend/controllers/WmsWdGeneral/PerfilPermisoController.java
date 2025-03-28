package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.PerfilPermisoService;

@RestController
@RequestMapping("/api/perfilpermiso")
public class PerfilPermisoController{
  private final PerfilPermisoService perfilPermisoService;

  public PerfilPermisoController(PerfilPermisoService perfilPermisoService) {
    this.perfilPermisoService = perfilPermisoService;
  }

  @GetMapping
  public ResponseEntity<List<String>> obtenerPerfilPermiso(
      @RequestParam String modulo,
      @RequestParam String descripcion,
      @RequestParam int usuarioClientePerfilId) {
    List<String> permisos = perfilPermisoService.obtenerPerfilPermiso(modulo, descripcion, usuarioClientePerfilId);
    return ResponseEntity.ok(permisos);
  }
}
