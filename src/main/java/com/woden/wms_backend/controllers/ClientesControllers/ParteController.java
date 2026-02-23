package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.ParteModel;
import com.woden.wms_backend.services.ClienteServices.ParteService;

@RestController
@RequestMapping("/client/partes")
public class ParteController extends BaseController<ParteModel, Integer> {

  @Autowired
  private ParteService parteService;

  public ParteController(ParteService parteService) {
    super(parteService);
  }

  @PostMapping("/sendPart")
  public ResponseEntity<?> sendPart(@RequestParam Integer opcion, @RequestParam Integer estadoId,
      @RequestParam Integer usuarioId, @RequestParam Integer palletId) {
    try {
      Integer filasAfectadas = parteService.sendPart(
        opcion,
        estadoId,
        usuarioId,
        palletId);
    return ResponseEntity.ok(filasAfectadas);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(null);
    }
  }
}
