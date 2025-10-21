package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.SendPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AccesorioSeparateDTO;
import com.woden.wms_backend.dto.clientDTO.SendAccesoryDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.services.ClienteServices.AccesorioService;

@RestController
@RequestMapping("/client/accesorio")
public class AccesorioController extends BaseController<AccesorioModel, Integer> {

    public AccesorioController(AccesorioService service) {
        super(service);
    }

    @Autowired
    private AccesorioService accesorioService;

    @PostMapping("/createAccesory")
    public ResponseEntity<Map<String, String>> guardarAccesorios(@RequestBody List<AccesorioModel> accesorios) {
        accesorioService.guardarAccesorios(accesorios);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registros guardados exitosamente.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchAccesory/{palletId}")
    public ResponseEntity<List<AccesorioSearchDTO>> buscarAccesorios(@PathVariable int palletId) {
        List<AccesorioSearchDTO> lista = accesorioService.buscarAccesoriosPorPallet(palletId);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarAccesorio")
    public ResponseEntity<Integer> eliminarAccesorio(
            @RequestParam Integer palletId,
            @RequestParam Integer cantidad,
            @RequestParam Integer codigoSapId) {
        int status = accesorioService.eliminarAccesorio(palletId, cantidad, codigoSapId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/cerrar-accesorio")
    public ResponseEntity<Map<String, String>> cerrarPalletAccesorio(@RequestBody SendPalletDTO dto) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Pallet cerrado correctamente");
        try {
            accesorioService.cerrarPalletAccesorio(dto);

           // System.out.println("Cerrar accesorio - PalletId: " + dto);
            response.put("message", "Pallet cerrado correctamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error al cerrar el pallet: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/sendAccesory")
    public ResponseEntity<?> sendAccesory(@RequestBody SendAccesoryDTO dto) {
        try {
            accesorioService.sendAccesory(dto);
            return ResponseEntity.ok(Map.of("message", "Pallet almacenado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al almacenar pallet: " + e.getMessage()));
        }
    }

    @PostMapping("/unifyAccesory")
    public ResponseEntity<?> unifyAccesory(@RequestBody Map<String, Object> dto) {
        try {
            Integer palletIdDestino = (Integer) dto.get("palletIdDestino");
            List<Integer> palletIds = ((List<?>) dto.get("palletIds")).stream()
                    .map(obj -> (obj instanceof Integer) ? (Integer) obj : Integer.parseInt(obj.toString()))
                    .toList();
            accesorioService.unificarAccesorio(palletIdDestino, palletIds);
            return ResponseEntity.ok(Map.of("message", "Accesorio unificado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al unificar accesorio: " + e.getMessage()));
        }
    }

    @PostMapping("/updatePalletAccesory")
    public ResponseEntity<?> updatePalletAccesory(@RequestBody Map<String, Object> requestBody) {
        Integer palletId = (Integer) requestBody.get("palletId");
        List<?> accesoriosRaw = (List<?>) requestBody.get("accesoriosId");
        List<Integer> accesoriosId = accesoriosRaw.stream()
                .map(obj -> (obj instanceof Integer) ? (Integer) obj : Integer.parseInt(obj.toString()))
                .toList();

        accesorioService.updatePalletAccesory(accesoriosId, palletId);
        return ResponseEntity.ok(Map.of("message", "Accesorio actualizado."));
    }

    @GetMapping("/searchSeparatePalletAccesory")
    public ResponseEntity<List<AccesorioSeparateDTO>> searchSeparatePalletAccesory(@RequestParam Integer cantidad,
            @RequestParam Integer palletId) {
        List<AccesorioSeparateDTO> lista = accesorioService.searchSeparatePalletAccesory(cantidad, palletId);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/updateSerialAccesory")
    public ResponseEntity<Map<String, String>> updateStatusBoxPacking(@RequestBody Map<String, String> requestBody) {
        String serialNuevo = requestBody.get("serialNuevo");
        String serialAnterior = requestBody.get("serialAnterior");

        accesorioService.UpdateSerialAccesory(serialNuevo, serialAnterior);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente."));
    }

    @GetMapping("/searchPackingCodigoSapAccesory")
    public ResponseEntity<List<Map<String, Object>>> searchPackingCodigoSapAccesory(
            @RequestParam String estado,
            @RequestParam Integer palletId,
            @RequestParam Integer codigoSapId) {
        List<Map<String, Object>> lista = accesorioService.searchPackingCodigoSapAccesory(estado, palletId, codigoSapId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/searchPackedAccesory")
    public ResponseEntity<List<Map<String, Object>>> searchPackedAccesory(
            @RequestParam String estado,
            @RequestParam String serial) {
        List<Map<String, Object>> lista = accesorioService.searchPackedAccesory(estado, serial);
        return ResponseEntity.ok(lista);
    }


    @PostMapping("/updatePackingBatch")
    public ResponseEntity<Integer> updatePackingBatch(@RequestBody List<String[]> packingDataList) {
        int result = accesorioService.updatePackingBatch(packingDataList);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getPackedAccesoriesSerials")
    public ResponseEntity<Integer> getPackedAccesoriesSerials(
            @RequestParam String codigoSap,
            @RequestParam String tipoAccesorio,
            @RequestParam Integer palletId) {

        int totalSeriales = accesorioService.getPackedAccesoriesSerials(codigoSap, tipoAccesorio, palletId);
        return ResponseEntity.ok(totalSeriales);
    }

    @GetMapping("/searchAllPackedAccesory")
    public ResponseEntity<List<Map<String, Object>>> SearchAllPackedAccesory(
            @RequestParam String estado) {
        List<Map<String, Object>> lista = accesorioService.SearchAllPackedAccesory(estado);
        return ResponseEntity.ok(lista);
    }














}
