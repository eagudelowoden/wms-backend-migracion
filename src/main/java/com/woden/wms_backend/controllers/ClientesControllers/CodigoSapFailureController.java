package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
  public Boolean getSapCodeFailureAsig(@RequestParam String nombre) {
    return service.getSapCodeFailureAsig(nombre);
  }

  @GetMapping("/getFallasFailure")
  public List<Map<String, String>> getFallas(@RequestParam String nombre) {
    return service.getFallas(nombre);
  }
}
