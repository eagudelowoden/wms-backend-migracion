package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.PreAlertaDTO;
import com.woden.wms_backend.models.Entity.PrealertaModel;
import com.woden.wms_backend.services.ClienteServices.PrealertaService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/client/prealerta")
public class PrealertaController {
  @Autowired
  private PrealertaService prealertaService;

  @GetMapping("/getModelPrealerta")
  public ResponseEntity<PrealertaModel> getPrealerta(@RequestParam String nombre) {
    PrealertaModel prealerta = prealertaService.getModelPreAlerta(nombre);
    if (prealerta == null) {
      return ResponseEntity.noContent().build(); // 204 No Content
    }
    return ResponseEntity.ok(prealerta);
  }

  @GetMapping("/getPrealertasByEstado/{estado}")
  public ResponseEntity<List<PreAlertaDTO>> getPrealertasByEstado(@PathVariable String estado) {
    return ResponseEntity.ok(prealertaService.getPrealertasByEstado(estado));
  }

  @GetMapping("/getDifferencePrealerta/{prealertaId}")
  public ResponseEntity<Integer> getDifferencePrealerta(@PathVariable int prealertaId) {
    return ResponseEntity.ok(prealertaService.getDifferencePrealerta(prealertaId));
  }
/*
  @PutMapping("/updatePrealerta/{prealertaId}")
  public ResponseEntity<Void> updatePrealerta(@PathVariable int prealertaId) {
    prealertaService.updatePrealerta(prealertaId);
    return ResponseEntity.ok().build(); // 200 OK
  }
  */
}
