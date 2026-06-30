package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.ForecastModel;
import com.woden.wms_backend.services.ClienteServices.ForecastService;

@RestController
@RequestMapping("/client/forecast")
public class ForecastController extends BaseController<ForecastModel, Integer> {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        super(forecastService);
        this.forecastService = forecastService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(defaultValue = "") String forecast) {
        return ResponseEntity.ok(forecastService.search(forecast));
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ForecastModel model) {
        forecastService.create(model);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Forecast creado.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editar(@PathVariable Integer id, @RequestBody ForecastModel model) {
        model.setId(id);
        Integer filas = forecastService.update(model);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Forecast actualizado.");
        } else {
            response.put("message", "No se pudo actualizar.");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        forecastService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Forecast eliminado.");
        return ResponseEntity.ok(response);
    }
}
