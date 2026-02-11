package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.InventarioModel;
import com.woden.wms_backend.models.Entity.InventarioTerminadoModel;
import com.woden.wms_backend.services.ClienteServices.InventarioTerminadoService;

@RestController
@RequestMapping("/client/inventarioTerminado")
public class InventarioTerminadoController extends BaseController<InventarioTerminadoModel, Integer> {
  public InventarioTerminadoController(InventarioTerminadoService inventarioTerminadoService) {
    super(inventarioTerminadoService);
  }

  @Autowired
  private InventarioTerminadoService inventarioTerminadoService;

  @PostMapping("/createInventoryTerminado")
  public ResponseEntity<Integer> create(@RequestBody List<InventarioModel> inventarios,
      @RequestParam Integer usuarioIdTerminado, @RequestParam String fechaTerminado, @RequestParam String documento) {
    try {
      Integer count = inventarioTerminadoService.create(inventarios, usuarioIdTerminado, fechaTerminado, documento);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(0);
    }
  }

  @PostMapping("/insertInventarioFaltante")
  public ResponseEntity<Integer> insertInventarioFaltante(
      @RequestParam Integer usuarioIdTerminado, @RequestParam String documento) {
    try {
      Integer count = inventarioTerminadoService.insertInventarioFaltante(usuarioIdTerminado, documento);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(0);
    }
  }
}
