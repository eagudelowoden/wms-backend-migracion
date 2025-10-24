package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.BaseDespachoModel;
import com.woden.wms_backend.services.ClienteServices.BaseDespachoService;

@RestController
@RequestMapping("/client/baseDespacho")
public class BaseDespachoController extends BaseController<BaseDespachoModel, Integer> {

  public BaseDespachoController(BaseDespachoService service) {
    super(service);
  }

  @Autowired
  private BaseDespachoService baseDespachoService;

  @GetMapping("/getModelBaseDespacho")
  public ResponseEntity<BaseDespachoModel> getBaseDespacho(@RequestParam String base, @RequestParam String serial) {
    List<BaseDespachoModel> results = baseDespachoService.getModelBase(base, serial);
    if (results.isEmpty()) {
      return ResponseEntity.ok(null);
    }
    BaseDespachoModel baseDespachoModel = results.get(0);
    return ResponseEntity.ok(baseDespachoModel);
  }
}
