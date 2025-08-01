package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.WorkFlowDestinoDTO;
import com.woden.wms_backend.services.ClienteServices.WorkFlowDestinoService;

@RestController
@RequestMapping("/client/workflowDestino")
public class WorkFlowDestinoController {

  private final WorkFlowDestinoService service;

  public WorkFlowDestinoController(WorkFlowDestinoService service) {
    this.service = service;
  }

  @PostMapping("/destino")
  public ResponseEntity<List<Integer>> obtenerDestinos(@RequestBody WorkFlowDestinoDTO dto) {
    List<Integer> destinos = service.obtenerDestinoId(
        dto.getOpcion(),
        dto.getOrigen(),
        dto.getDescripcion(),
        dto.getTipologiaId());
    return ResponseEntity.ok(destinos);
  }

  @GetMapping("/listar-destinos")
  public ResponseEntity<List<String>> getDestinosWorkflow(
      @RequestParam String opcion,
      @RequestParam String origen,
      @RequestParam String descripcion,
      @RequestParam int tipologiaId) {
    List<String> destinos = service.getNombresDestinos(opcion, origen, descripcion, tipologiaId);
    return ResponseEntity.ok(destinos);
  }
}
