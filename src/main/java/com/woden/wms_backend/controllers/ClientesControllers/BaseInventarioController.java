package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.BaseInventarioModel;
import com.woden.wms_backend.services.ClienteServices.BaseInventarioService;

@RestController
@RequestMapping("/client/baseInventario")
public class BaseInventarioController extends BaseController<BaseInventarioModel, Integer> {
  @Autowired
  private BaseInventarioService baseInventarioService;

  public BaseInventarioController(BaseInventarioService service) {
    super(service);
  }

  @GetMapping("/getModelBaseInventario")
  public ResponseEntity<?> getModelBaseInventario(@RequestParam String base,
      @RequestParam String serial) {
    try {
      BaseInventarioModel model = baseInventarioService.getModel(base, serial);
      return ResponseEntity.ok(model);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Error al obtener el modelo de base de inventario");
    }
  }
}
