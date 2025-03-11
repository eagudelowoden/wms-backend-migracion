package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController extends BaseController<IngresoModel, Integer> {

    public IngresoController(IngresoService service) {
        super(service);
    }

    @Autowired
    private IngresoService ingresoService;

    @PostMapping("/create")
    public ResponseEntity<String> createIngreso(@RequestBody IngresoModel  ingreso) {
        try {
            ingresoService.createIngreso(ingreso);
            return ResponseEntity.ok("Ingreso registrado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el ingreso: " + e.getMessage());
        }
    }

    @GetMapping("/searchIngreso/{palletId}")
    public ResponseEntity<List<IngresoDTO>> searchEntryReingreso(@PathVariable Integer palletId) {
        List<IngresoDTO> results = ingresoService.searchEntryReingreso(palletId);
        return ResponseEntity.ok(results);
    }
}
