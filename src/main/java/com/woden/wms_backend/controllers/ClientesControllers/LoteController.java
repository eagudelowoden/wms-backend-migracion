package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.LoteDTO;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.services.ClienteServices.LoteService;

@RestController
@RequestMapping("/client/lote")
public class LoteController extends BaseController<LoteModel, Integer> {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        super(loteService);
        this.loteService = loteService;
    }

    @GetMapping("/getLotes")
    public ResponseEntity<List<LoteDTO>> getLotes() {
        return ResponseEntity.ok(loteService.getLotes());
    }

    @GetMapping("/getIdByLote")
    public Integer getIdByLote(@RequestParam String lote) {
        return loteService.getIdByLote(lote);
    }

    @GetMapping("/getBatchName/{id}")
    public String getBatchName(@PathVariable Integer id) {
        return loteService.getBatchName(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(defaultValue = "") String lote) {
        return ResponseEntity.ok(loteService.search(lote));
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crear(@RequestBody LoteModel model) {
        loteService.create(model);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lote creado.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editar(@PathVariable Integer id, @RequestBody LoteModel model) {
        model.setId(id);
        Integer filas = loteService.update(model);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Lote actualizado.");
        } else {
            response.put("message", "No se pudo actualizar.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/has-movements")
    public ResponseEntity<Boolean> hasMovements(@PathVariable Integer id) {
        return ResponseEntity.ok(loteService.hasMovements(id));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer count = loteService.getCount(id);
            if (count != null && count > 0) {
                response.put("hasMovements", true);
                response.put("message", "El Lote tiene registros asociados.");
            } else {
                loteService.delete(id);
                response.put("hasMovements", false);
                response.put("message", "Lote eliminado.");
            }
        } catch (BusinessRuleException e) {
            response.put("hasMovements", true);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Integer id, @RequestParam Integer estado) {
        loteService.toggle(id, estado);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Estado actualizado.");
        return ResponseEntity.ok(response);
    }
}
