package com.woden.wms_backend.controllers.ClientesControllers;

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

import com.woden.wms_backend.services.ClienteServices.NotificacionCorreoService;

/** Administracion > Notificaciones: reglas de correo por evento y cliente, bitacora de envios y correo de prueba. */
@RestController
@RequestMapping("/client/notificaciones")
public class NotificacionCorreoController {

  @Autowired
  private NotificacionCorreoService notificacionCorreoService;

  @GetMapping("/estado")
  public ResponseEntity<Map<String, Object>> estado() {
    return ResponseEntity.ok(notificacionCorreoService.estado());
  }

  @GetMapping("/eventos")
  public ResponseEntity<List<Map<String, String>>> eventos() {
    return ResponseEntity.ok(notificacionCorreoService.eventos());
  }

  @GetMapping("/reglas")
  public ResponseEntity<List<Map<String, Object>>> reglas() {
    return ResponseEntity.ok(notificacionCorreoService.reglas());
  }

  @PostMapping("/reglas")
  public ResponseEntity<Map<String, Object>> guardar(@RequestBody Map<String, Object> regla,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(notificacionCorreoService.guardar(regla, usuario));
  }

  @DeleteMapping("/reglas/{id}")
  public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
    notificacionCorreoService.eliminar(id);
    return ResponseEntity.ok(Map.of("ok", true));
  }

  @PostMapping("/reglas/{id}/probar")
  public ResponseEntity<Map<String, Object>> probar(@PathVariable Integer id,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(notificacionCorreoService.probar(id, usuario));
  }

  /** Sonidos de los avisos de Ingreso del cliente (y si el interruptor esta encendido). */
  @GetMapping("/sonidos")
  public ResponseEntity<Map<String, Object>> sonidos() {
    return ResponseEntity.ok(notificacionCorreoService.sonidos());
  }

  @PutMapping("/sonidos")
  public ResponseEntity<Map<String, Object>> guardarSonidos(@RequestBody Map<String, Object> config,
      @RequestParam(defaultValue = "") String usuario) {
    return ResponseEntity.ok(notificacionCorreoService.guardarSonidos(config, usuario));
  }

  @PutMapping("/sonidos/activo")
  public ResponseEntity<Map<String, Object>> activarSonidos(@RequestParam boolean activo) {
    return ResponseEntity.ok(notificacionCorreoService.activarSonidos(activo));
  }

  @GetMapping("/envios")
  public ResponseEntity<List<Map<String, Object>>> envios() {
    return ResponseEntity.ok(notificacionCorreoService.envios());
  }
}
