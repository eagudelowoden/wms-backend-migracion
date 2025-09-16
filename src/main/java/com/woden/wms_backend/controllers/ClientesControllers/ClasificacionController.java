package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.ClasificacionModel;
import com.woden.wms_backend.services.ClienteServices.ClasificacionService;

@RestController
@RequestMapping("/client/clasificacion")
public class ClasificacionController extends BaseController<ClasificacionModel, Integer> {
  public ClasificacionController(ClasificacionService service) {
    super(service);
  }

  @Autowired
  private ClasificacionService service;

  @PostMapping("/insertClasificacion")
  public ResponseEntity<?> insertClasificacion(
      @RequestBody Map<String, Object> payload) {

    Integer usuarioId = (Integer) payload.get("usuarioId");
    List<?> rawList = (List<?>) payload.get("data");

    List<List<String>> lista = new ArrayList<>();

    for (Object o : rawList) {
      if (o instanceof List<?>) {
        List<?> inner = (List<?>) o;
        List<String> row = new ArrayList<>();
        for (Object val : inner) {
          row.add(val.toString()); // convierte todo a String de forma segura
        }
        lista.add(row);
      }
    }
    service.create(lista, usuarioId);

    return ResponseEntity.ok(1);
  }

  // @PostMapping("/insertClasificacionWeb")
  // public ResponseEntity<?> insertClasificacionWeb(@RequestBody Map<String, Object> payload) {
  //   Integer usuarioId = (Integer) payload.get("usuarioId");
  //   Integer usuarioAsignadoId = (Integer) payload.get("usuarioAsignadoId");
  //   List<?> rawList = (List<?>) payload.get("data");

  //   // Convierte a lista de String (seriales)
  //   for (Object o : rawList) {
  //     if (o instanceof List<?>) {
  //       List<?> inner = (List<?>) o;
  //       List<String> row = new ArrayList<>();
  //       for (Object val : inner) {
  //         row.add(val.toString()); // convierte todo a String de forma segura
  //       }
  //       lista.add(row);
  //     }
  //   }

  //   service.insertClasificacionWeb(seriales, usuarioId);

  //   return ResponseEntity.ok(1);
  // }

  @PostMapping("/updateClasificacion")
  public ResponseEntity<?> updateClasificacion(@RequestParam String serial,
      @RequestParam String estadoEnviado, @RequestParam Integer nivelId) {
    service.updateClasificacion(serial, estadoEnviado, nivelId);
    return ResponseEntity.ok(1);
  }

  @PostMapping("/deleteClasificacion")
  public ResponseEntity<?> deleteClasificacion(
      @RequestBody Map<String, Object> payload) {
    String serial = (String) payload.get("serial");
    service.deleteClasificacion(serial);
    return ResponseEntity.ok(1);
  }
}
