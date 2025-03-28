package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.UsuarioClienteService;

@RestController
@RequestMapping("/api/usuariocliente")
public class UsuarioClienteController {

  private final UsuarioClienteService usuarioClienteService;

  public UsuarioClienteController(UsuarioClienteService usuarioClienteService) {
    this.usuarioClienteService = usuarioClienteService;
  }

  @GetMapping("/id")
  public ResponseEntity<Map<String, Integer>> obtenerIdUsuarioCliente(
      @RequestParam int usuarioId,
      @RequestParam String cliente) {
    Integer id = usuarioClienteService.obtenerIdUsuarioCliente(usuarioId, cliente);
    Map<String, Integer> response = new HashMap<>();
    response.put("id", id);
    return ResponseEntity.ok(response);
  }
}
