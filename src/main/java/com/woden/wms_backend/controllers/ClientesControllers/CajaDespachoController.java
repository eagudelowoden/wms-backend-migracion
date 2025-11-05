package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.CajaDespachoService;

@RestController
@RequestMapping("/client/cajaDespacho")
public class CajaDespachoController {
  @Autowired
  private CajaDespachoService service;

  @PostMapping("/insertBoxDispatch")
  public ResponseEntity<Integer> insertBoxDispatch(@RequestParam String numero, @RequestParam Integer palletId,
      @RequestParam Integer usuarioId, @RequestParam String fecha) {
    try {
      service.insertBoxDispatch(numero, palletId, usuarioId, fecha);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @DeleteMapping("/deleteBoxDispatch")
  public ResponseEntity<Integer> deleteBoxDispatch(@RequestParam Integer Id) {
    try {
      service.deleteBoxDispatch(Id);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/searchDispatch")
  public List<Map<String, Object>> searchDispatch(@RequestParam Integer palletId) {
    return service.searchDispatch(palletId);
  }

  @GetMapping("/getLastBoxDispatch")
  public Integer getLastBoxDispatch(@RequestParam Integer palletId) {
    return service.getLastBoxDispatch(palletId);
  }

  @GetMapping("/getCountBoxDispatch")
  public Integer getCountBoxDispatch(@RequestParam Integer cajaId) {
    return service.getCountBoxDispatch(cajaId);
  }

  @PutMapping("/inactivateAllBoxDispatch")
  public ResponseEntity<Integer> inactivateAllBoxDispatch(@RequestParam Integer palletId) {
    try {
      service.inactivateAllBoxDispatch(palletId);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @PutMapping("/inactivateBoxDispatch")
  public ResponseEntity<Integer> inactivateBoxDispatch(@RequestBody List<Integer> cajaDespachoId) {
    try {
      Integer status = 0;
      for (Integer id : cajaDespachoId) {
        status = service.inactivateBoxDispatch(id);
      }
      return ResponseEntity.ok(status);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }
}
