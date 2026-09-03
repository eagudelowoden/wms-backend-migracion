package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.InsertCosmeticaDTO;
import com.woden.wms_backend.services.ClienteServices.CosmeticaService;

import java.util.Map;

@RestController
@RequestMapping("/client/cosmetica")
public class CosmeticaController {
  @Autowired
  private CosmeticaService service;

  @PostMapping("/insertCosmetica")
  public ResponseEntity<?> insertCosmetica(@RequestBody InsertCosmeticaDTO request) {
    try {
      service.insertCosmetica(request.getSeriales(), request.getUsuarioId());
      return ResponseEntity.ok(Map.of("message", "Registros insertados correctamente.", "success", true));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("message", "Error al insertar: " + e.getMessage(), "success", false));
    }
  }

  @GetMapping("/searchCosmeticaEntry")
  public ResponseEntity<?> searchCosmeticaEntry(@RequestParam String perfil, @RequestParam Integer usuarioId) {
    try {
      return ResponseEntity.ok(service.searchCosmeticaEntry(perfil, usuarioId));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }
}
