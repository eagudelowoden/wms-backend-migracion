package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.AutogestionService;

/**
 * Endpoints PÚBLICOS (sin JWT, ver SecurityConfig) para autorizar o rechazar una
 * solicitud desde el enlace del correo. La credencial es el token de un solo uso:
 * 256 bits aleatorios, solo se guarda su hash y se consume al resolver.
 *
 * El GET solo muestra el detalle y NO consume el token (los escáneres de correo
 * abren los enlaces). Autorizar/rechazar es un POST explícito.
 */
@RestController
@RequestMapping("/public/autogestion")
public class AutogestionPublicController {
  @Autowired
  private AutogestionService service;

  @GetMapping("/aprobacion/{clienteId}/{token}")
  public ResponseEntity<?> detalle(@PathVariable Integer clienteId, @PathVariable String token) {
    try {
      Map<String, Object> result = service.detalleSolicitudPublica(clienteId, token);
      return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/aprobacion/{clienteId}/{token}/aprobar")
  public ResponseEntity<?> aprobar(@PathVariable Integer clienteId, @PathVariable String token) {
    return resolver(clienteId, token, true);
  }

  @PostMapping("/aprobacion/{clienteId}/{token}/rechazar")
  public ResponseEntity<?> rechazar(@PathVariable Integer clienteId, @PathVariable String token) {
    return resolver(clienteId, token, false);
  }

  private ResponseEntity<?> resolver(Integer clienteId, String token, boolean aprobar) {
    try {
      return ResponseEntity.ok(service.resolverSolicitudPublica(clienteId, token, aprobar));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.ok(Map.of("resultado", "INVALIDA", "mensaje", "El enlace no es válido."));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("resultado", "ERROR", "mensaje", AutogestionController.mensajeRaiz(e)));
    }
  }
}
