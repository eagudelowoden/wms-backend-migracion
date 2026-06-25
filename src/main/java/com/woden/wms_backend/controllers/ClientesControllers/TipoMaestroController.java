package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.services.ClienteServices.TipoMaestroService;

@RestController
@RequestMapping("/client/tipoMaestro")
public class TipoMaestroController {

    @Autowired
    private TipoMaestroService tipoMaestroService;

    @GetMapping
    public ResponseEntity<List<TipoMaestroModel>> getAll() {
        return ResponseEntity.ok(tipoMaestroService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoMaestroModel> getById(@PathVariable Integer id) {
        TipoMaestroModel model = tipoMaestroService.getById(id);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(model);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search() {
        return ResponseEntity.ok(tipoMaestroService.search());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TipoMaestroModel model) {
        tipoMaestroService.create(model);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tipo Maestro creado.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @RequestBody TipoMaestroModel model) {
        model.setId(id);
        Integer filas = tipoMaestroService.update(model);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Tipo Maestro actualizado.");
        } else {
            response.put("message", "No se pudo actualizar.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/has-movements")
    public ResponseEntity<Boolean> hasMovements(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoMaestroService.hasMovements(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer count = tipoMaestroService.getCount(id);
            if (count != null && count > 0) {
                response.put("hasMovements", true);
                response.put("message", "El Tipo Maestro tiene registros asociados.");
            } else {
                tipoMaestroService.delete(id);
                response.put("hasMovements", false);
                response.put("message", "Tipo Maestro eliminado.");
            }
        } catch (BusinessRuleException e) {
            response.put("hasMovements", true);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Integer id, @RequestParam Integer estado) {
        Integer filas = tipoMaestroService.toggle(id, estado);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Estado actualizado.");
        } else {
            response.put("message", "No se pudo actualizar el estado.");
        }
        return ResponseEntity.ok(response);
    }
}
