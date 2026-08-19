package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.PresupuestoModel;
import com.woden.wms_backend.services.ClienteServices.PresupuestoService;

@RestController
@RequestMapping("/client/presupuestos")
public class PresupuestoController extends BaseController<PresupuestoModel, Integer> {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        super(presupuestoService);
        this.presupuestoService = presupuestoService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam(defaultValue = "") String presupuesto) {
        return ResponseEntity.ok(presupuestoService.search(presupuesto));
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> crear(@RequestBody PresupuestoModel model) {
        presupuestoService.create(model);
        return ResponseEntity.ok(Map.of("message", "Presupuesto creado."));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editar(
            @PathVariable Integer id, @RequestBody PresupuestoModel model) {
        model.setId(id);
        Integer filas = presupuestoService.update(model);
        String msg = (filas != null && filas > 0) ? "Presupuesto actualizado." : "No se pudo actualizar.";
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        presupuestoService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Presupuesto eliminado."));
    }
}
