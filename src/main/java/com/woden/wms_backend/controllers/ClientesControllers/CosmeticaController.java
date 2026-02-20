package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.CosmeticaService;

@RestController
@RequestMapping("/client/cosmetica")
public class CosmeticaController {
  @Autowired
  private CosmeticaService service;

  @GetMapping("/searchCosmeticaEntry")
  public ResponseEntity<?> searchCosmeticaEntry() {
    try {
      return ResponseEntity.ok(service.searchCosmeticaEntry());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }
}
