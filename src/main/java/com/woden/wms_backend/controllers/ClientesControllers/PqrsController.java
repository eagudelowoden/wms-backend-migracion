package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.PqrsService;

@RestController
@RequestMapping("/client/pqrs")
public class PqrsController {

  private final PqrsService pqrsService;

  public PqrsController(PqrsService pqrsService) {
    this.pqrsService = pqrsService;
  }

  @PostMapping("/update")
  public ResponseEntity<?> updatePqrs(@RequestBody List<Map<String, Object>> request) {
    for (Map<String, Object> item : request) {
      Integer estadoDiagnosticoId = (Integer) item.get("estadoDiagnosticoId");
      Integer fallaDiagnosticoId = (Integer) item.get("fallaDiagnosticoId");
      String name = (String) item.get("name");
      String xStudioDiagnosticoTecnicoWoden = (String) item.get("xStudioDiagnosticoTecnicoWoden");

      pqrsService.updatePqrs(estadoDiagnosticoId, fallaDiagnosticoId, name, xStudioDiagnosticoTecnicoWoden);
    }
    return ResponseEntity.ok(1);
  }

  @GetMapping("/getModelPqrs")
  public ResponseEntity<?> getModelPqrs(@RequestParam String serial, @RequestParam Integer stage) {
    return ResponseEntity.ok(pqrsService.getModelPqrs(serial, stage));
  }

  /**
   * Detalle de PQRS — reemplazo de getModelPqrs() sobre App_PQRS_Tickets /
   * App_PQRS_Truckrolls (la tabla PQRS vieja va a eliminarse). Devuelve 200
   * con null en el body si el serial no tiene PQRS en ninguna de las dos.
   */
  @GetMapping("/detalle")
  public ResponseEntity<?> buscarDetallePqrs(@RequestParam String serial) {
    return ResponseEntity.ok(pqrsService.buscarDetallePqrs(serial));
  }

  @PostMapping("/updateSerialIdAndSapCodeIdPqrs")
  public ResponseEntity<?> updateSerialIdAndSapCodeIdPqrs(@RequestBody Map<String, Object> request) {
    Integer serialId = (Integer) request.get("serialId");
    Integer codigoSapId = (Integer) request.get("codigoSapId");
    String serial = (String) request.get("serial");
    pqrsService.updateSerialIdAndSapCodeIdPqrs(serialId, codigoSapId, serial);
    return ResponseEntity.ok(1);
  }

  @PostMapping("/updateObservationPqrs")
  public ResponseEntity<?> updateObservationPqrs(@RequestBody Map<String, Object> request) {
    Integer serialId = (Integer) request.get("serialId");
    String serial = (String) request.get("serial");
    String observacion = (String) request.get("observacion");
    pqrsService.updateObservationPqrs(serialId, serial, observacion);
    return ResponseEntity.ok(1);
  }
}
