package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.BaseDTO;
import com.woden.wms_backend.models.Entity.BaseEtiquetadoModel;
import com.woden.wms_backend.services.ClienteServices.BaseEtiquetadoService;

@RestController
@RequestMapping("/client/baseEtiquetado")
public class BaseEtiquetadoController extends BaseController<BaseEtiquetadoModel, Integer> {
  public BaseEtiquetadoController(BaseEtiquetadoService service) {
    super(service);
  }

  @Autowired
  private BaseEtiquetadoService baseEtiquetadoService;

  @GetMapping("/getModelBaseEtiquetado")
  public ResponseEntity<BaseDTO> getModelBaseEtqieutado(@RequestParam String base, @RequestParam String serial) {
    BaseDTO model = baseEtiquetadoService.getModelBase(base, serial);
    return ResponseEntity.ok(model);
  }

  @GetMapping("/getCountBase")
  public ResponseEntity<Integer> getCountBase(@RequestParam String base) {
    Integer count = baseEtiquetadoService.getCountBase(base);
    return ResponseEntity.ok(count);
  }
}
