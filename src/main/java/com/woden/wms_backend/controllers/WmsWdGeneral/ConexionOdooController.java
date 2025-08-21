package com.woden.wms_backend.controllers.WmsWdGeneral;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.ConexionOdooService;

@RestController
@RequestMapping("/general/conexionOdoo")
public class ConexionOdooController {
  @Autowired
  private ConexionOdooService conexionOdooService;

  @GetMapping("/getConexionOdoo/{id}")
  public ResponseEntity<?> getConexionOdoo(@PathVariable Integer id) {
    return ResponseEntity.ok(conexionOdooService.getConexionOdoo(id));
  }
}
