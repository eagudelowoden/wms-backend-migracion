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

import com.woden.wms_backend.dto.clientDTO.AutogestionSolicitudDTO;
import com.woden.wms_backend.services.ClienteServices.AutogestionService;

@RestController
@RequestMapping("/client/autogestion")
public class AutogestionController {
  @Autowired
  private AutogestionService service;

  @GetMapping("/estados")
  public ResponseEntity<?> estados(@RequestParam(defaultValue = "SERIAL") String ambito) {
    return ResponseEntity.ok(service.estados(ambito));
  }

  @GetMapping("/serial")
  public ResponseEntity<?> buscarSerial(@RequestParam String serial) {
    Map<String, Object> result = service.buscarSerial(serial);
    return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
  }

  @GetMapping("/pallet")
  public ResponseEntity<?> buscarPallet(@RequestParam String pallet) {
    Map<String, Object> result = service.buscarPallet(pallet);
    return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
  }

  /** Crea una solicitud de movimiento y envía los enlaces de aprobación. No mueve nada. */
  @PostMapping("/solicitud")
  public ResponseEntity<?> crearSolicitud(@RequestBody AutogestionSolicitudDTO request) {
    try {
      Map<String, Object> result = service.crearSolicitud(request);
      return ResponseEntity.ok(Map.of("success", true, "message", "Solicitud creada.", "data", result));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("success", false, "message", mensajeRaiz(e)));
    }
  }

  @GetMapping("/solicitudes")
  public ResponseEntity<?> solicitudes(@RequestParam(required = false) Integer limite) {
    return ResponseEntity.ok(service.solicitudes(limite));
  }

  @GetMapping("/logs")
  public ResponseEntity<?> logs(@RequestParam(required = false) String serial,
      @RequestParam(required = false) Integer limite) {
    return ResponseEntity.ok(service.logs(serial, limite));
  }

  private static String mensajeRaiz(Throwable e) {
    Throwable root = e;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    return String.valueOf(root.getMessage());
  }
}
