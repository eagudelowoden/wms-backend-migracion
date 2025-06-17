package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.clientDTO.PedidoDTO;
import com.woden.wms_backend.models.Entity.PedidoModel;
import com.woden.wms_backend.services.ClienteServices.PedidoService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/client/pedido")
public class PedidoController extends BaseController<PedidoModel, Integer> {
  public PedidoController(PedidoService pedidoService) {
    super(pedidoService);
    this.pedidoService = pedidoService;
  }

  private final PedidoService pedidoService;

  @GetMapping("/search")
  public ResponseEntity<List<PedidoDTO>> searchPedidos(
      @RequestParam String cliente,
      @RequestParam(defaultValue = "0") Integer id) {
    List<PedidoDTO> pedidos = pedidoService.buscarPedidos(cliente, id);
    return ResponseEntity.ok(pedidos);
  }

}
