package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.WorkFlowService;

@RestController
@RequestMapping("/client/workflow")
public class WorkFlowController {
  @Autowired
  private WorkFlowService service;

  @GetMapping("/getNivelId")
  public ResponseEntity<Integer> getLevelId(String opcion, String origen, String descripcion, int tipologiaId) {
    return ResponseEntity.ok(service.getLevelId(opcion, origen, descripcion, tipologiaId));
  }
}
