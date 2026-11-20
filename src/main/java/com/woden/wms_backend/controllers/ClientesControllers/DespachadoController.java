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

@RestController
@RequestMapping("/client/despachado")
public class DespachadoController {

  @Autowired
  private DespachadoService despachadoService;

  public record CapturaRequest(String salida, String tipo, String valor, String pallet) {}

  public record EquiposRequest(String salida, List<String> series, String destino, Integer usuarioId,
      String usuarioNombre, Boolean simular) {}

  public record AccesoriosRequest(String salida, List<Map<String, Object>> items, String destino, Integer usuarioId,
      String usuarioNombre, String zonaHoraria, Boolean simular) {}

  @PostMapping("/equipos/capturar")
  public ResponseEntity<List<Map<String, Object>>> capturar(@RequestBody CapturaRequest r) {
    return ResponseEntity.ok(despachadoService.capturar(r.salida(), r.tipo(), r.valor(), r.pallet()));
  }

  @PostMapping("/equipos/regresar")
  public ResponseEntity<List<Map<String, Object>>> regresar(@RequestBody EquiposRequest r) {
    return ResponseEntity.ok(despachadoService.regresar(r.series(), r.salida(), r.destino(), r.usuarioId(),
        r.usuarioNombre(), Boolean.TRUE.equals(r.simular())));
  }

  @GetMapping("/accesorios/previa")
  public ResponseEntity<List<Map<String, Object>>> accPrevia(@RequestParam String salida) {
    return ResponseEntity.ok(despachadoService.accPrevia(salida));
  }

  @PostMapping("/accesorios/regresar")
  public ResponseEntity<List<Map<String, Object>>> accRegresar(@RequestBody AccesoriosRequest r) {
    return ResponseEntity.ok(despachadoService.accRegresar(r.salida(), r.items(), r.destino(), r.usuarioId(),
        r.usuarioNombre(), r.zonaHoraria(), Boolean.TRUE.equals(r.simular())));
  }

  @GetMapping("/destinos")
  public ResponseEntity<List<String>> destinos(@RequestParam(defaultValue = "equipos") String tipo) {
    return ResponseEntity.ok(despachadoService.destinos(
        "accesorios".equalsIgnoreCase(tipo) ? DespachadoService.WF_ACCESORIOS : DespachadoService.WF_EQUIPOS));
  }

  @GetMapping("/historial")
  public ResponseEntity<List<Map<String, Object>>> historial() {
    return ResponseEntity.ok(despachadoService.historial());
  }
}
