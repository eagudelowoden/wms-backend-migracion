package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.PqrsService;

@RestController
@RequestMapping("/client/pqrs")
public class PqrsController {

  @Autowired
  private PqrsService pqrsService;

  @PostMapping("/update")
  public ResponseEntity<?> updatePqrs(@RequestBody List<Map<String, Object>> request) {
    for (Map<String, Object> item : request) {
      Integer estadoDiagnosticoId = (Integer) item.get("estadoDiagnosticoId");
      Integer fallaDiagnosticoId = (Integer) item.get("fallaDiagnosticoId");
      String name = (String) item.get("name");
      String XStudioDiagnosticoTecnicoWoden = (String) item.get("XStudioDiagnosticoTecnicoWoden");

      pqrsService.updatePqrs(estadoDiagnosticoId, fallaDiagnosticoId, name, XStudioDiagnosticoTecnicoWoden);
    }
    return ResponseEntity.ok().build();
  }
}
