package com.woden.wms_backend.controllers.WmsWdGeneral;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.PerfilService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/search")
  public ResponseEntity<List<Map<String, Object>>> search(
      @RequestParam(defaultValue = "") String nombre,
      @RequestParam(required = false) String cliente) {
    return ResponseEntity.ok(perfilService.search(nombre, cliente));
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body) {
    return ResponseEntity.ok(perfilService.create(body.get("nombre"), body.get("cliente")));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Map<String, Object>> update(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(perfilService.update(id, body.get("nombre")));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
    return ResponseEntity.ok(perfilService.delete(id));
  }

  @GetMapping("/{id}/secciones/disponibles")
  public ResponseEntity<List<Map<String, Object>>> getAvailableSections(@PathVariable int id) {
    return ResponseEntity.ok(perfilService.getAvailableSections(id));
  }

  @GetMapping("/{id}/secciones/asignadas")
  public ResponseEntity<List<Map<String, Object>>> getAggregatedSections(@PathVariable int id) {
    return ResponseEntity.ok(perfilService.getAggregatedSections(id));
  }

  @PostMapping("/{id}/secciones")
  public ResponseEntity<Map<String, String>> assignSections(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.assignSections(id, body.get("seccionIds"));
    return ResponseEntity.ok(Map.of("message", "Secciones asignadas."));
  }

  @DeleteMapping("/{id}/secciones")
  public ResponseEntity<Map<String, String>> removeSections(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.removeSections(id, body.get("seccionIds"));
    return ResponseEntity.ok(Map.of("message", "Secciones removidas."));
  }

  @GetMapping("/{id}/secciones/{seccionId}/modulos/disponibles")
  public ResponseEntity<List<Map<String, Object>>> getAvailableModules(@PathVariable int id, @PathVariable int seccionId) {
    return ResponseEntity.ok(perfilService.getAvailableModules(id, seccionId));
  }

  @GetMapping("/{id}/secciones/{seccionId}/modulos/asignados")
  public ResponseEntity<List<Map<String, Object>>> getAggregatedModules(@PathVariable int id, @PathVariable int seccionId) {
    return ResponseEntity.ok(perfilService.getAggregatedModules(id, seccionId));
  }

  @PostMapping("/{id}/modulos")
  public ResponseEntity<Map<String, String>> assignModules(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.assignModules(id, body.get("moduloIds"));
    return ResponseEntity.ok(Map.of("message", "Modulos asignados."));
  }

  @DeleteMapping("/{id}/modulos")
  public ResponseEntity<Map<String, String>> removeModules(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.removeModules(id, body.get("moduloIds"));
    return ResponseEntity.ok(Map.of("message", "Modulos removidos."));
  }

  @GetMapping("/{id}/modulos/{moduloId}/permisos/disponibles")
  public ResponseEntity<List<Map<String, Object>>> getAvailablePermisos(@PathVariable int id, @PathVariable int moduloId) {
    return ResponseEntity.ok(perfilService.getAvailablePermisos(id, moduloId));
  }

  @GetMapping("/{id}/modulos/{moduloId}/permisos/asignados")
  public ResponseEntity<List<Map<String, Object>>> getAggregatedPermisos(@PathVariable int id, @PathVariable int moduloId) {
    return ResponseEntity.ok(perfilService.getAggregatedPermisos(id, moduloId));
  }

  @PostMapping("/{id}/permisos")
  public ResponseEntity<Map<String, String>> assignPermisos(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.assignPermisos(id, body.get("permisoIds"));
    return ResponseEntity.ok(Map.of("message", "Permisos asignados."));
  }

  @DeleteMapping("/{id}/permisos")
  public ResponseEntity<Map<String, String>> removePermisos(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.removePermisos(id, body.get("permisoIds"));
    return ResponseEntity.ok(Map.of("message", "Permisos removidos."));
  }

  @GetMapping("/{id}/tipos-perfil/disponibles")
  public ResponseEntity<List<Map<String, Object>>> getAvailableTipoPerfiles(@PathVariable int id) {
    return ResponseEntity.ok(perfilService.getAvailableTipoPerfiles(id));
  }

  @GetMapping("/{id}/tipos-perfil/asignados")
  public ResponseEntity<List<Map<String, Object>>> getAggregatedTipoPerfiles(@PathVariable int id) {
    return ResponseEntity.ok(perfilService.getAggregatedTipoPerfiles(id));
  }

  @PostMapping("/{id}/tipos-perfil")
  public ResponseEntity<Map<String, String>> assignTipoPerfiles(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.assignTipoPerfiles(id, body.get("tipoPerfilIds"));
    return ResponseEntity.ok(Map.of("message", "Tipo Perfil asignados."));
  }

  @DeleteMapping("/{id}/tipos-perfil")
  public ResponseEntity<Map<String, String>> removeTipoPerfiles(@PathVariable int id, @RequestBody Map<String, List<Integer>> body) {
    perfilService.removeTipoPerfiles(id, body.get("tipoPerfilIds"));
    return ResponseEntity.ok(Map.of("message", "Tipo Perfil removidos."));
  }

  @GetMapping("/tipos-perfil")
  public ResponseEntity<List<Map<String, Object>>> listTipoPerfil() {
    return ResponseEntity.ok(perfilService.listTipoPerfil());
  }
}
