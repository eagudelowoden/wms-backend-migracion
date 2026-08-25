package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.BaseSeparacion;
import com.woden.wms_backend.services.ClienteServices.BaseSeparacionService;

@RestController
@RequestMapping("/client/baseSeparacion")
public class BaseSeparacionController extends BaseController<BaseSeparacion, Integer> {

  public BaseSeparacionController(BaseSeparacionService service) {
    super(service);
  }

  @Autowired
  private BaseSeparacionService baseSeparacionService;

  @GetMapping("/getModelBaseSeparacion")
  public ResponseEntity<BaseSeparacion> getBaseSeparacion(@RequestParam String base, @RequestParam String serial) {
    List<BaseSeparacion> results = baseSeparacionService.getModelBase(base, serial);
    if (results.isEmpty()) {
      return ResponseEntity.ok(null);
    }
    BaseSeparacion baseSeparacion = results.get(0);
    return ResponseEntity.ok(baseSeparacion);
  }
}
