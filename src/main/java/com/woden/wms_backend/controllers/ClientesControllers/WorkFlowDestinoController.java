package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.WorkFlowDestinoDTO;
import com.woden.wms_backend.services.ClienteServices.WorkFlowDestinoService;

@RestController
@RequestMapping("/api/workflow")
public class WorkFlowDestinoController {

  private final WorkFlowDestinoService service;

  public WorkFlowDestinoController(WorkFlowDestinoService service) {
    this.service = service;
  }

  @PostMapping("/destino")
  public ResponseEntity<Integer> obtenerDestino(@RequestBody WorkFlowDestinoDTO dto) {
    Integer destinoId = service.obtenerDestinoId(
        dto.getOrigen(),
        dto.getOpcion(),
        dto.getDescripcion(),
        dto.getTipologiaId());
    return ResponseEntity.ok(destinoId != null ? destinoId : 0);
  }
}
