package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.Entity.WorkFlowModel;
import com.woden.wms_backend.services.ClienteServices.WorkFlowService;

@RestController
@RequestMapping("/client/workflow")
public class WorkFlowController {
  @Autowired
  private WorkFlowService service;

  @GetMapping("/getNivelId")
  public ResponseEntity<Integer> getLevelId(String opcion, String origen, String descripcion, int tipologiaId) {
    return ResponseEntity.ok(service.getLevelId(opcion, origen, descripcion, tipologiaId));
  }

  @GetMapping("/search")
  public ResponseEntity<List<Map<String, Object>>> search() {
    return ResponseEntity.ok(service.search());
  }

  @GetMapping("/search-con-destinos")
  public ResponseEntity<List<Map<String, Object>>> searchConDestinos() {
    return ResponseEntity.ok(service.searchConDestinos());
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@RequestBody WorkFlowModel model) {
    service.create(model.getModuloId(), model.getOrigenId(), model.getOpcionId(),
                   model.getTipologiaId(), model.getNivelId(), "admin", "WMS");
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Workflow creado.");
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Map<String, Object>> update(@PathVariable int id, @RequestBody WorkFlowModel model) {
    service.update(id, model.getModuloId(), model.getOrigenId(), model.getOpcionId(),
                   model.getTipologiaId(), model.getNivelId(), "admin", "WMS");
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Workflow actualizado.");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
    service.delete(id, "admin", "WMS");
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Workflow eliminado.");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/modulos")
  public ResponseEntity<List<String>> searchModulos() {
    return ResponseEntity.ok(service.searchModulos());
  }

  @GetMapping("/estados")
  public ResponseEntity<List<Map<String, Object>>> searchEstados() {
    return ResponseEntity.ok(service.searchEstados());
  }

  @GetMapping("/estados/{moduloNombre}")
  public ResponseEntity<List<String>> searchEstadosByModulo(@PathVariable String moduloNombre) {
    return ResponseEntity.ok(service.searchEstadosByModulo(moduloNombre));
  }

  @GetMapping("/descripciones/{moduloNombre}")
  public ResponseEntity<List<String>> getDescripciones(@PathVariable String moduloNombre) {
    return ResponseEntity.ok(service.getDescripciones(moduloNombre));
  }

  @GetMapping("/opciones/{moduloNombre}/{descripcion}")
  public ResponseEntity<List<String>> getOpciones(
      @PathVariable String moduloNombre, @PathVariable String descripcion) {
    return ResponseEntity.ok(service.getOpciones(moduloNombre, descripcion));
  }

  @GetMapping("/permiso-id")
  public ResponseEntity<Integer> getPermisoId(@RequestParam String modulo, @RequestParam String nombre) {
    return ResponseEntity.ok(service.getPermisoId(modulo, nombre));
  }

  @GetMapping("/modulo-id")
  public ResponseEntity<Integer> getModuloId(@RequestParam String nombre) {
    return ResponseEntity.ok(service.getModuloId(nombre));
  }

  @GetMapping("/estado-id")
  public ResponseEntity<Integer> getEstadoId(@RequestParam String nombre) {
    return ResponseEntity.ok(service.getEstadoId(nombre));
  }
}