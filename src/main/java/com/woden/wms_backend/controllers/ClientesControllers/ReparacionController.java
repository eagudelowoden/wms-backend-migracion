package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.ReparacionModel;
import com.woden.wms_backend.services.ClienteServices.ReparacionService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/client/reparacion")
public class ReparacionController extends BaseController<ReparacionModel, Integer> {

  public ReparacionController(ReparacionService service) {
    super(service);
  }

  @Autowired
  private ReparacionService service;

  @PostMapping("/createRepair")
  public ResponseEntity<?> createEntity(@RequestBody ReparacionModel requestBody) {
    service.create(requestBody.getSerialId(), requestBody.getSerial(), requestBody.getMac(),
        requestBody.getCodigoSapId(), requestBody.getEstadoFinalId(), requestBody.getFallaDxId(),
        requestBody.getTecnicoAsignacionId(), requestBody.getFechaAsignacion(), requestBody.getUsuarioId());
    return ResponseEntity.ok(1);
  }

  @DeleteMapping("/deleteRepair")
  public ResponseEntity<?> deleteEntity(@RequestBody List<String> seriales) {
    try {
      service.deleteRepair(seriales);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/getHistoricRepair")
  public ResponseEntity<?> getHistoricRepair(@RequestParam String serial) {
    return ResponseEntity.ok(service.getHistoricRepair(serial));
  }

  @GetMapping("/getLastRepair")
  public ResponseEntity<?> getLastRepair(@RequestParam String serial) {
    return ResponseEntity.ok(service.getLastRepair(serial));
  }

  @GetMapping("/getRepairUser")
  public ResponseEntity<?> getRepairUser(@RequestParam Integer usuarioId) {
    return ResponseEntity.ok(service.getRepairUser(usuarioId));
  }

  @GetMapping("/searchAssignedRepair")
  public ResponseEntity<?> searchAssignedRepair(@RequestParam Integer tecnicoAsignacionId, @RequestParam String tipo) {
    return ResponseEntity.ok(service.searchAssignedRepair(tecnicoAsignacionId, tipo));
  }

  @GetMapping("/searchRepairedRepair")
  public ResponseEntity<?> searchRepairedRepair(@RequestParam Integer tecnicoReparacionId, @RequestParam String estado) {
    return ResponseEntity.ok(service.searchRepairedRepair(tecnicoReparacionId, estado));
  }

  @GetMapping("/getAssignedTechnicianRepair")
  public ResponseEntity<?> getAssignedTechnicianRepair(@RequestParam String serial) {
    return ResponseEntity.ok(service.getAssignedTechnicianRepair(serial));
  }
}
