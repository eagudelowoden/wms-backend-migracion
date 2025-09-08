package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.CodigoSapFailureService;

@RestController
@RequestMapping("/client/codigosapfailure")
public class CodigoSapFailureController {
  @Autowired
  private CodigoSapFailureService service;

  @GetMapping("/getSapCodeFailureAsig")
  public ResponseEntity<Boolean> getSapCodeFailureAsig(@RequestParam String nombre) {
    return ResponseEntity.ok(service.getSapCodeFailureAsig(nombre));
  }

  @GetMapping("/getFallasFailure")
  public ResponseEntity<List<Map<String, String>>> getFallas(@RequestParam String nombre) {
    return ResponseEntity.ok(service.getFallas(nombre));
  }

  @GetMapping("/searchMandatoryComponent")
  public ResponseEntity<Boolean> searchMandatoryComponent(@RequestParam Integer codigoSapId,
      @RequestParam Integer fallaId) {
    return ResponseEntity.ok(service.searchMandatoryComponent(codigoSapId, fallaId));
  }

  @GetMapping("/searchSapCodeFailureComponent")
  public ResponseEntity<List<Map<String, Object>>> searchSapCodeFailureComponent(@RequestParam Integer codigoSapId,
      @RequestParam Integer fallaId) {
    return ResponseEntity.ok(service.searchSapCodeFailureComponent(codigoSapId, fallaId));
  }
}
