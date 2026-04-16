package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.models.Entity.CalidadModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.CalidadService;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woden.wms_backend.controllers.BaseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/client/calidad")
public class CalidadController extends  BaseController<CalidadModel, Integer> {

    public CalidadController (CalidadService service) {super(service);}

    @Autowired
    private CalidadService calidadService;

    @PostMapping("/updateFinalStateQuality")
    public ResponseEntity<Map<String, Object>> updateFinalStateQuality(@RequestBody Map<String, Object> calidad) {
        Integer palletId = ((Number) calidad.get("palletId")).intValue();
        Integer estadoFinalId = ((Number) calidad.get("estadoFinalId")).intValue();
        calidadService.updateFinalStateQuality(palletId, estadoFinalId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ Estado final actualizado correctamente");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/insertCalidad")
    public ResponseEntity<Integer> createEntity(@RequestBody CalidadModel requestBody) {
        try {
            calidadService.createCalidad(
                    requestBody.getSerialId(),
                    requestBody.getSerial(),
                    requestBody.getMac(),
                    requestBody.getCodigoSapId(),
                    requestBody.getPalletId(),
                    requestBody.getCajaEmpaqueId(),
                    requestBody.getUsuarioId(),
                    requestBody.getFecha()
            );
            return ResponseEntity.ok(1);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(0);
        }
    }

    @DeleteMapping("/eliminarSeriesCalidad")
    public ResponseEntity<Integer> eliminarSeriesCalidad(@RequestBody List<String> seriales) {
        if (seriales == null || seriales.isEmpty()) {
            // logger.warn("⚠️ Se llamó a eliminarSeriesEmpaque pero la lista de seriales
            // estaba vacía o nula");
        } else {
            // logger.info("🗑️ Se van a eliminar los seriales: {}", seriales);
        }

        int status = calidadService.eliminarSeriesCalidad(seriales);

        // logger.info("✅ Resultado de eliminarSeriesEmpaque: {}", status);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/updateQualityEntry")
    public ResponseEntity<Integer> updateQualityEntry(
            @RequestParam Integer estadoId,
            @RequestParam Integer usuarioIdMovimiento,
            @RequestParam(value = "seriales") List<String> seriales) {
        try {
            int result = calidadService.updateQualityEntry(estadoId, usuarioIdMovimiento, seriales);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(0);
        }
    }

    @PostMapping("/updateQualityPalletEntry")
    public ResponseEntity<Integer> updateQualityPalletEntry(
            @RequestParam Integer estadoId,
            @RequestParam Integer usuarioIdMovimiento,
            @RequestBody List<Integer> palletIds) {

        if (palletIds == null || palletIds.isEmpty()) {
            return ResponseEntity.ok(0); // nada que actualizar
        }

        try {
            int result = calidadService.updateQualityPalletEntry(estadoId, usuarioIdMovimiento, palletIds);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 se mantiene simple sin log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1);
        }
    }


    @PostMapping("/updateQuality")
    public ResponseEntity<Integer> updateQuality(
            @RequestParam Integer fallaFuncionalId,
            @RequestParam Integer fallaComesticaId,
            @RequestParam("seriales") String serial) {
        try {
            int result = calidadService.updateQuality(fallaFuncionalId, fallaComesticaId, serial);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(0);
        }
    }

    @GetMapping("/validateQualityByPallet")
    public ResponseEntity<Integer> validateQualityByPallet(
            @RequestParam Integer palletId) {
        try {
            int result = calidadService.validateQualityByPallet(palletId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(0); // si falla, deja pasar
        }
    }
















}
