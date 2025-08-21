package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.Entity.PqrsModel;
import com.woden.wms_backend.models.WmsWdGeneral.ConexionOdooModel;
import com.woden.wms_backend.services.WmsWdGeneral.ConexionOdooService;
import com.woden.wms_backend.services.WmsWdGeneral.OdooService;

@RestController
@RequestMapping("/general/odoo")
public class OdooController {
  @Autowired
  private OdooService odooService;

  @Autowired
  private ConexionOdooService conexionOdooService; // Asumiendo que este lo usas como repositorio

  @PostMapping("/actualizarTickets")
  public ResponseEntity<Map<String, Object>> actualizarTickets(@RequestBody List<PqrsModel> tickets,
      @RequestParam Integer clienteId, @RequestParam Integer odooPqrsOn) {

    // ConexionOdooModel conexion = conexionOdooService.getConexionOdoo(clienteId);
    ConexionOdooModel conexionesOdoo = conexionOdooService.getConexionOdoo(clienteId);
    int result = odooService.actualizarTicketsOdoo(tickets, odooPqrsOn, conexionesOdoo);

    return ResponseEntity.ok(Map.of("result", result));
  }
}
