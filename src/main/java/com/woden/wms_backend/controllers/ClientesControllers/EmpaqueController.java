package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
import com.woden.wms_backend.dto.PackingTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woden.wms_backend.controllers.BaseController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/empaque")
public class EmpaqueController extends BaseController<EmpaqueModel, Integer> {

    public EmpaqueController(EmpaqueService service) {
        super(service);
    }

    @Autowired
    private EmpaqueService empaqueService;


    @PostMapping("/insertPacking")
    public ResponseEntity<?> createEntity(@RequestBody EmpaqueModel requestBody) {
        Integer loteId = (requestBody.getLoteId() != null) ? requestBody.getLoteId() : 0;
        empaqueService.createEmpaque(
                requestBody.getSerialId(),
                requestBody.getSerial(),
                requestBody.getMac(),
                requestBody.getCodigoSapId(),
                requestBody.getPalletId(),
                requestBody.getCajaEmpaqueId(),
                requestBody.getNivelId(),
                requestBody.getUsuarioId(),
                requestBody.getFecha(),
                loteId,
                requestBody.getSmartCardId(),
                requestBody.getSmartCard());
        return ResponseEntity.ok(1);
    }

    /*
    @DeleteMapping("/eliminarSeriesEmpaque")
    public ResponseEntity<Integer> eliminarSeriesEmpaque(@RequestBody List<String> seriales) {
        if (seriales == null || seriales.isEmpty()) {
            // logger.warn("⚠️ Se llamó a eliminarSeriesEmpaque pero la lista de seriales
            // estaba vacía o nula");
        } else {
            // logger.info("🗑️ Se van a eliminar los seriales: {}", seriales);
        }

        int status = empaqueService.eliminarSeriesEmpaque(seriales);

        // logger.info("✅ Resultado de eliminarSeriesEmpaque: {}", status);
        return ResponseEntity.ok(status);
    }*/
    @PostMapping("/eliminar-transaccional")
    public ResponseEntity<Integer> eliminarTransaccional(
            @RequestParam Integer estadoId,
            @RequestParam Integer usuarioId,
            @RequestBody List<String> seriales) {

        if (seriales == null || seriales.isEmpty()) {
            return ResponseEntity.badRequest().body(0);
        }

        // Llamamos al método que orquestra ambas tablas bajo una sola transacción
        int status = empaqueService.procesarEliminacionCompleta(estadoId, usuarioId, seriales);

        return ResponseEntity.ok(status);
    }



    @PostMapping("/updateSmartCard")
    public ResponseEntity<?> updateSmartCard(@RequestBody Map<String, Object> empaque) {
        Integer smartCardId = ((Number) empaque.get("smartCardId")).intValue();
        String smartCardNuevo = (String) empaque.get("smartCardNuevo");
        String serial = (String) empaque.get("serial");

        empaqueService.updateSmartCard(smartCardId, smartCardNuevo, serial);

        return ResponseEntity.ok(1);
    }

    @PostMapping("/updatePackingWeb")
    public ResponseEntity<?> updatePackingWeb(@RequestBody Map<String, Object> empaque) {
        Integer serialId = ((Number) empaque.get("serialId")).intValue();
        String serialNuevo = (String) empaque.get("serialNuevo");
        String mac = (String) empaque.get("mac");
        Integer nivelNuevo = ((Number) empaque.get("nivelNuevo")).intValue();
        String serialAnterior = (String) empaque.get("serialAnterior");

        empaqueService.UpdatePackingWeb(serialId, serialNuevo, mac, nivelNuevo, serialAnterior);

        return ResponseEntity.ok(1);
    }

    @PostMapping("/updatePacking")
    public ResponseEntity<?> updatePacking(@RequestBody Map<String, Object> empaque) {
        Integer serialId = ((Number) empaque.get("serialId")).intValue();
        String serialNuevo = (String) empaque.get("serialNuevo");
        String mac = (String) empaque.get("mac");
        String serialAnterior = (String) empaque.get("serialAnterior");
        empaqueService.UpdatePacking(serialId, serialNuevo, mac, serialAnterior);
        return ResponseEntity.ok(1);
    }

    @PostMapping("/createFullPacking")
    public ResponseEntity<?> createFullPacking(@RequestBody PackingTransactionDTO request) {
        try {
            // Llamamos al método transaccional que creaste
            System.out.println("ID Recibido: " + request.getEstadoId());
            empaqueService.createPackingCompleto(request);
            return ResponseEntity.ok(1); // Retornamos éxito
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en consola si falla
            return ResponseEntity.badRequest().body("Error al crear packing: " + e.getMessage());
        }
    }

    @PostMapping("/insertPackingWeb")
    public ResponseEntity<?> insertPacking(@RequestBody EmpaqueModel empaque) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validamos que la fecha no sea nula antes de pasarla al servicio
            LocalDateTime fechaProceso = (empaque.getFecha() != null)
                    ? empaque.getFecha()
                    : LocalDateTime.now();

            // Llamada al servicio usando los campos de tu EmpaqueModel
            empaqueService.createEmpaqueWEB(
                    empaque.getSerialId(),
                    empaque.getSerial(),
                    empaque.getMac(),
                    empaque.getCodigoSapId(),
                    empaque.getPalletId(),
                    empaque.getCajaEmpaqueId(),
                    //empaque.getEstadoId(),
                    empaque.getNivelId(),
                    empaque.getUsuarioId(),
                    fechaProceso,
                    empaque.getLoteId(),
                    empaque.getSmartCardId(),
                    empaque.getSmartCard()
            );

            response.put("success", true);
            response.put("message", "Empaque registrado correctamente para el serial: " + empaque.getSerial());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Este error viene del throw new RuntimeException en tu servicio
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error inesperado: " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}
