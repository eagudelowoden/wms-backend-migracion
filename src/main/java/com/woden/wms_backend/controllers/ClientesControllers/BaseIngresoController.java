package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.BaseDTO;
import com.woden.wms_backend.models.Entity.BaseIngresoModel;
import com.woden.wms_backend.services.ClienteServices.BaseIngresoService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/client/baseIngreso")
public class BaseIngresoController extends BaseController<BaseIngresoModel, Integer> {
  public BaseIngresoController(BaseIngresoService service) {
    super(service);
  }

  @Autowired
  private BaseIngresoService baseIngresoService;

  @GetMapping("/getModelBaseIngreso")
  public ResponseEntity<?> getModelBaseIngreso(@RequestParam String base,
      @RequestParam String serial) {
    BaseDTO model = baseIngresoService.getModel(base, serial);
    // if (model == null) {
    //   return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Codigo Sap no encontrado");
    // }

    // and the baseIngreso object
    Map<String, Object> response = new HashMap<>();
    // response.put("model", model.getCodigoSap()); // Or any appropriate identifier
    response.put("baseIngreso", model);

    return ResponseEntity.ok(response);
  }
}
