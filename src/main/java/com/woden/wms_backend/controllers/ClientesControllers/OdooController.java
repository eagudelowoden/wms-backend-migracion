package com.woden.wms_backend.controllers.ClientesControllers;

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
import com.woden.wms_backend.services.ClienteServices.OdooService;
import com.woden.wms_backend.services.WmsWdGeneral.ConexionOdooService;

@RestController
@RequestMapping("/client/odoo")
public class OdooController {
  @Autowired
  private OdooService odooService;

  @Autowired
  private ConexionOdooService conexionOdooService;

  @PostMapping("/actualizarTickets")
  public ResponseEntity<Map<String, Object>> actualizarTickets(@RequestBody List<PqrsModel> tickets,
      @RequestParam Integer clienteId, @RequestParam Integer odooPqrsOn) {
    try {
      ConexionOdooModel conexion = conexionOdooService.getConexionOdoo(clienteId);
      int result = odooService.actualizarTicketsOdoo(tickets, odooPqrsOn,
          conexion);
      return ResponseEntity.ok(Map.of("result", result));
    } catch (Exception e) {
      System.err.println("Error en actualizarTickets: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.internalServerError()
          .body(Map.of("error", 0 + e.getMessage()));
    }
  }
}

// @Repository
// class DebugRepository {

//   @PersistenceContext
//   private EntityManager entityManager;

//   public String getCurrentDatabase() {
//     return (String) entityManager.createNativeQuery("SELECT DB_NAME()").getSingleResult();
//   }
// }
