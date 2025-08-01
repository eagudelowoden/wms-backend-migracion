package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.MovimientoModel;
import com.woden.wms_backend.services.ClienteServices.MovimientoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/client/movimiento")
public class MovimientoController extends BaseController<MovimientoModel, Integer> {
  public MovimientoController(MovimientoService movimientoService) {
        super(movimientoService);
        this.movimientoService = movimientoService;
    }
  private final MovimientoService movimientoService;

  @GetMapping("/getLast/{palletId}")
  public Integer getLast(@PathVariable Integer palletId) {
      return movimientoService.getLast(palletId);
  }
  
  @GetMapping("/userCount/{usuarioId}")
  public Integer userCount(@PathVariable Integer usuarioId) {
    return movimientoService.userCount(usuarioId);
  }
}
