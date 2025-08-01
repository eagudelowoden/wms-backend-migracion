package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.DiagnosticoService;

@RestController
@RequestMapping("/client/diagnostico")
public class DiagnosticoController {
  @Autowired
  private DiagnosticoService service;

  @GetMapping("/updateCodigoSapDiagnostico")
  public Integer updateSapCode(@RequestParam String serial) {
    return service.updateSapCode(serial);
  }

  @PostMapping("/createDiagnostico")
  public ResponseEntity<Integer> insertarDiagnostico(
      @RequestParam Integer serialId,
      @RequestParam String serial,
      @RequestParam String mac,
      @RequestParam Integer codigoSapId,
      @RequestParam Integer usuarioId) {
    try {
      service.create(serialId, serial, mac, codigoSapId, usuarioId);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(0);
    }
  }

  @PostMapping("/updateDiagnostico")
  public ResponseEntity<?> updateDiagnostico(@RequestBody List<Map<String, Object>> requestList) {
    try {
      for (Map<String, Object> request : requestList) {
        Integer estadoFinalId = (Integer) request.get("estadoFinalId");
        Integer fallaId = (Integer) request.get("fallaId");
        String serial = (String) request.get("serial");
        Integer estadoCalidadId = (Integer) request.get("estadoCalidadId");

        service.updateDiagnostico(estadoFinalId, fallaId, serial, estadoCalidadId);
      }
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

}
