package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.AutogestionPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AutogestionSerialDTO;
import com.woden.wms_backend.services.ClienteServices.AutogestionService;

@RestController
@RequestMapping("/client/autogestion")
public class AutogestionController {
  @Autowired
  private AutogestionService service;

  @GetMapping("/estados")
  public ResponseEntity<?> estados() {
    return ResponseEntity.ok(service.estados());
  }

  @GetMapping("/serial")
  public ResponseEntity<?> buscarSerial(@RequestParam String serial) {
    Map<String, Object> result = service.buscarSerial(serial);
    return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
  }

  @PostMapping("/serial/accion")
  public ResponseEntity<?> accionSerial(@RequestBody AutogestionSerialDTO request) {
    try {
      service.accionSerial(request);
      return ResponseEntity.ok(Map.of("message", "Acción registrada correctamente.", "success", true));
    } catch (Exception e) {
      Throwable root = e;
      while (root.getCause() != null) {
        root = root.getCause();
      }
      return ResponseEntity.ok(Map.of("message", String.valueOf(root.getMessage()), "success", false));
    }
  }

  @GetMapping("/pallet")
  public ResponseEntity<?> buscarPallet(@RequestParam String pallet) {
    Map<String, Object> result = service.buscarPallet(pallet);
    return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
  }

  @PostMapping("/pallet/accion")
  public ResponseEntity<?> accionPallet(@RequestBody AutogestionPalletDTO request) {
    try {
      service.accionPallet(request);
      return ResponseEntity.ok(Map.of("message", "Acción registrada correctamente.", "success", true));
    } catch (Exception e) {
      Throwable root = e;
      while (root.getCause() != null) {
        root = root.getCause();
      }
      return ResponseEntity.ok(Map.of("message", String.valueOf(root.getMessage()), "success", false));
    }
  }

  @GetMapping("/logs")
  public ResponseEntity<?> logs(@RequestParam(required = false) String serial,
      @RequestParam(required = false) Integer limite) {
    return ResponseEntity.ok(service.logs(serial, limite));
  }
}
