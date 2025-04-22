package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.EstadoService;

@RestController
@RequestMapping("/general/estado")
public class EstadoController {

  private final EstadoService estadoService;

  public EstadoController(EstadoService estadoService) {
    this.estadoService = estadoService;
  }

  @GetMapping("/id/{nombre}")
  public ResponseEntity<Map<String, Integer>> getIdEstado(@PathVariable String nombre) {
    int id = estadoService.getIdByNombre(nombre);
    Map<String, Integer> response = new HashMap<>();
    response.put("id", id);
    return ResponseEntity.ok(response);
  }
}
