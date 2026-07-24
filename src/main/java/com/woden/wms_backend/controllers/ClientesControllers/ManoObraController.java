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
import com.woden.wms_backend.models.Entity.ManoObraModel;
import com.woden.wms_backend.services.ClienteServices.ManoObraService;

@RestController
@RequestMapping("/client/mano-obra")
public class ManoObraController extends BaseController<ManoObraModel, Integer> {

    private final ManoObraService manoObraService;

    public ManoObraController(ManoObraService manoObraService) {
        super(manoObraService);
        this.manoObraService = manoObraService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search() {
        return ResponseEntity.ok(manoObraService.search());
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ManoObraModel model) {
        manoObraService.create(model);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Mano de obra creada.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editar(@PathVariable Integer id, @RequestBody ManoObraModel model) {
        model.setId(id);
        Integer filas = manoObraService.update(model);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Mano de obra actualizada.");
        } else {
            response.put("message", "No se pudo actualizar.");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        manoObraService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Mano de obra eliminada.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Integer id, @RequestParam Integer estado) {
        Integer filas = manoObraService.toggle(id, estado);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Estado actualizado.");
        } else {
            response.put("message", "No se pudo actualizar el estado.");
        }
        return ResponseEntity.ok(response);
    }
}
