package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.codigosap.CodigoSapPermitidoRequestDTO;
import com.woden.wms_backend.dto.clientDTO.codigosap.MultimodeloRestringidoDTO;
import com.woden.wms_backend.services.ClienteServices.CodigoSapPermitidoService;

@RestController
@RequestMapping("/client/codigosappermitido")
public class CodigoSapPermitidoController {

  @Autowired
  private CodigoSapPermitidoService service;

  @GetMapping("/estado")
  public ResponseEntity<Map<String, Boolean>> getEstado() {
    return ResponseEntity.ok(Map.of("habilitado", service.estaHabilitado()));
  }

  @GetMapping("/lista")
  public ResponseEntity<List<Map<String, Object>>> getLista(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(service.getPool(codigoSapId));
  }

  @GetMapping("/config")
  public ResponseEntity<MultimodeloRestringidoDTO> getConfig(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(service.getConfig(codigoSapId));
  }

  @PostMapping("/insertar")
  public ResponseEntity<Map<String, Integer>> insertar(@RequestBody CodigoSapPermitidoRequestDTO body) {
    int inserted = service.insertar(body.getCodigoSapId(), body.getPermitidosIds());
    return ResponseEntity.ok(Map.of("inserted", inserted));
  }

  @DeleteMapping("/eliminar")
  public ResponseEntity<Map<String, Integer>> eliminar(@RequestBody CodigoSapPermitidoRequestDTO body) {
    int deleted = service.eliminar(body.getCodigoSapId(), body.getPermitidosIds());
    return ResponseEntity.ok(Map.of("deleted", deleted));
  }
}
