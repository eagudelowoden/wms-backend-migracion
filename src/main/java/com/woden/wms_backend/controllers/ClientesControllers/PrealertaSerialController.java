package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.Entity.PrealertaSerialModel;
import com.woden.wms_backend.services.ClienteServices.PrealertaSerialService;

@RestController
@RequestMapping("/client/prealertaSerial")
public class PrealertaSerialController {
  @Autowired
  private PrealertaSerialService prealertaSerialService;



  @GetMapping("/getModelPrealertaSerial/{id}/{serial}")
  public ResponseEntity<PrealertaSerialModel> getPrealertaSerial(@PathVariable Integer id,
      @PathVariable String serial) {
    PrealertaSerialModel prealerta = prealertaSerialService.getModelPreAlerta(id, serial);
    if (prealerta == null) {
      return ResponseEntity.noContent().build(); // 204 No Content
    }
    return ResponseEntity.ok(prealerta);
  }

  @GetMapping("/count-register/{prealertaId}/{recogidaOn}/{tipo}")
  public ResponseEntity<Integer> countRegister(
      @PathVariable int prealertaId,
      @PathVariable int recogidaOn,
      @PathVariable String tipo) {
    int total = prealertaSerialService.countRegister(prealertaId, recogidaOn, tipo);
    return ResponseEntity.ok(total);
  }
}
