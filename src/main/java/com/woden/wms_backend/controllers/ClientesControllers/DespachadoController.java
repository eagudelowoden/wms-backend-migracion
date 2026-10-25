package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.DespachadoService;

/** Despacho > Despachado: equipos y accesorios que el cliente rechaza vuelven a Novedades. */
@RestController
@RequestMapping("/client/despachado")
public class DespachadoController {

  @Autowired
  private DespachadoService despachadoService;

  public record EquiposRequest(String salida, List<String> series, Integer usuarioId, String usuarioNombre,
      Boolean simular) {}

  public record AccesoriosRequest(String salida, List<Map<String, Object>> items, Integer usuarioId,
      String usuarioNombre, String zonaHoraria, Boolean simular) {}

  @PostMapping("/equipos/previa")
  public ResponseEntity<List<Map<String, Object>>> previa(@RequestBody EquiposRequest r) {
    return ResponseEntity.ok(despachadoService.previa(r.series(), r.salida()));
  }

  @PostMapping("/equipos/regresar")
  public ResponseEntity<List<Map<String, Object>>> regresar(@RequestBody EquiposRequest r) {
    return ResponseEntity.ok(despachadoService.regresar(r.series(), r.salida(), r.usuarioId(), r.usuarioNombre(),
        Boolean.TRUE.equals(r.simular())));
  }

  @GetMapping("/accesorios/previa")
  public ResponseEntity<List<Map<String, Object>>> accPrevia(@RequestParam String salida) {
    return ResponseEntity.ok(despachadoService.accPrevia(salida));
  }

  @PostMapping("/accesorios/regresar")
  public ResponseEntity<List<Map<String, Object>>> accRegresar(@RequestBody AccesoriosRequest r) {
    return ResponseEntity.ok(despachadoService.accRegresar(r.salida(), r.items(), r.usuarioId(), r.usuarioNombre(),
        r.zonaHoraria(), Boolean.TRUE.equals(r.simular())));
  }

  @GetMapping("/historial")
  public ResponseEntity<List<Map<String, Object>>> historial() {
    return ResponseEntity.ok(despachadoService.historial());
  }
}
