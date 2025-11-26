package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.SendPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AccesorioSeparateDTO;
import com.woden.wms_backend.dto.clientDTO.SendAccesoryDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.services.ClienteServices.AccesorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Integer> eliminarAccesorio(@RequestParam Integer palletId, @RequestParam Integer cantidad, @RequestParam Integer codigoSapId) {
        int status = accesorioService.eliminarAccesorio(palletId, cantidad, codigoSapId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/cerrar-accesorio")
    public ResponseEntity<Map<String, String>> cerrarPalletAccesorio(@RequestBody SendPalletDTO dto) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Pallet cerrado correctamente");
        try {
            accesorioService.cerrarPalletAccesorio(dto);

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al almacenar pallet: " + e.getMessage()));
        }
    }

    @PostMapping("/unifyAccesory")
    public ResponseEntity<?> unifyAccesory(@RequestBody Map<String, Object> dto) {
        try {
            Integer palletIdDestino = (Integer) dto.get("palletIdDestino");
            List<Integer> palletIds = ((List<?>) dto.get("palletIds")).stream().map(obj -> (obj instanceof Integer) ? (Integer) obj : Integer.parseInt(obj.toString())).toList();
            accesorioService.unificarAccesorio(palletIdDestino, palletIds);
            return ResponseEntity.ok(Map.of("message", "Accesorio unificado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al unificar accesorio: " + e.getMessage()));
        }
    }

    @PostMapping("/updatePalletAccesory")
    public ResponseEntity<?> updatePalletAccesory(@RequestBody Map<String, Object> requestBody) {
        Integer palletId = (Integer) requestBody.get("palletId");
        List<?> accesoriosRaw = (List<?>) requestBody.get("accesoriosId");
        List<Integer> accesoriosId = accesoriosRaw.stream().map(obj -> (obj instanceof Integer) ? (Integer) obj : Integer.parseInt(obj.toString())).toList();

        accesorioService.updatePalletAccesory(accesoriosId, palletId);
        return ResponseEntity.ok(Map.of("message", "Accesorio actualizado."));
    }

    @GetMapping("/searchSeparatePalletAccesory")
    public ResponseEntity<List<AccesorioSeparateDTO>> searchSeparatePalletAccesory(@RequestParam Integer cantidad, @RequestParam Integer palletId) {
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
    public ResponseEntity<List<Map<String, Object>>> searchPackingCodigoSapAccesory(@RequestParam String estado, @RequestParam Integer palletId, @RequestParam Integer codigoSapId) {
        List<Map<String, Object>> lista = accesorioService.searchPackingCodigoSapAccesory(estado, palletId, codigoSapId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/searchPackedAccesory")
    public ResponseEntity<List<Map<String, Object>>> searchPackedAccesory(@RequestParam String estado, @RequestParam String serial) {
        List<Map<String, Object>> lista = accesorioService.searchPackedAccesory(estado, serial);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/updatePackingBatch")
    public ResponseEntity<Integer> updatePackingBatch(@RequestBody List<String[]> packingDataList) {
        int result = accesorioService.updatePackingBatch(packingDataList);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getPackedAccesoriesSerials")
    public ResponseEntity<Integer> getPackedAccesoriesSerials(@RequestParam String codigoSap, @RequestParam String tipoAccesorio, @RequestParam Integer palletId) {

        int totalSeriales = accesorioService.getPackedAccesoriesSerials(codigoSap, tipoAccesorio, palletId);
        return ResponseEntity.ok(totalSeriales);
    }

    @GetMapping("/searchAllPackedAccesory")
    public ResponseEntity<List<Map<String, Object>>> SearchAllPackedAccesory(@RequestParam String estado) {
        List<Map<String, Object>> lista = accesorioService.SearchAllPackedAccesory(estado);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/getModelDispatchAccesory")
    public ResponseEntity<List<AccesorioModel>> getModelDispatchAccesory(@RequestParam Integer palletId) {
        try {
            List<AccesorioModel> acceesorio = accesorioService.getModelDispatchAccesory(palletId);
            return ResponseEntity.ok(acceesorio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/packOffPalletAccesory")
    public ResponseEntity<Integer> packOffPalletAccesory(@RequestParam Integer palletId) {
        int status = accesorioService.packOffPalletAccesory(palletId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/getModelDispatchBoxAccesory")
    public ResponseEntity<List<AccesorioModel>> getModelDispatchBoxAccesory(@RequestParam Integer palletId, @RequestBody List<Integer> cajaDespachoId) {
        List<AccesorioModel> acceesorio = accesorioService.getModelDispatchBoxAccesory(palletId, cajaDespachoId);
        return ResponseEntity.ok(acceesorio);
    }

    @DeleteMapping("/packOffPalletAccesoryBox")
    public ResponseEntity<Integer> packOffPalletAccesoryBox(@RequestParam Integer palletId, @RequestBody List<Integer> cajaDespachoId) {
        try {
            Integer status = 1;
            for (Integer caja : cajaDespachoId) {
                status = accesorioService.packOffPalletAccesoryBox(palletId, caja);
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }

    @GetMapping("/grupo")
    public ResponseEntity<List<Map<String, Object>>> getGroupAccesory(
            @RequestParam String codigoSap,
            @RequestParam String estado,
            @RequestParam String tipo) {

        // Ejecuta el servicio
        List<Map<String, Object>> grupo = accesorioService.searchGroupAccesory(
                codigoSap.trim(),
                estado.trim(),
                tipo.trim()
        );

        // Si no hay resultados
        if (grupo == null || grupo.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        // Devuelve lista JSON
        return ResponseEntity.ok(grupo);
    }


    @GetMapping("/buscarlimpiezaAccesorios")
    public ResponseEntity<List<Map<String, Object>>> buscarAccesoriosLimpieza(
            @RequestParam String codigoSap,
            @RequestParam String tipoAccesorio) {
        try {
            List<Map<String, Object>> accesorios = accesorioService.searchCleaningAccesory(codigoSap, tipoAccesorio);

            if (accesorios.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>()); // 200 []
            }
            return ResponseEntity.ok(accesorios); // 200 OK con lista
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // 500 error interno
        }
    }
    @PostMapping("/updateAccesory")
    public ResponseEntity<?> updateAccesory(@RequestBody Map<String, Object> requestBody) {
        try {
            Integer cantidad = (Integer) requestBody.get("cantidad");
            Integer destinoId = (Integer) requestBody.get("destinoId");
            Integer estadoLimpiezaId = (Integer) requestBody.get("estadoLimpiezaId");
            String codigo = (String) requestBody.get("codigo");
            String estado = (String) requestBody.get("estado");
            String tipoAccesorio = (String) requestBody.get("tipoAccesorio");
            Integer usuarioLimpiezaId = (Integer) requestBody.get("usuarioLimpiezaId");

            accesorioService.updateAccesory(
                    cantidad,
                    destinoId,
                    estadoLimpiezaId,
                    codigo,
                    estado,
                    tipoAccesorio,
                    usuarioLimpiezaId
            );
            return ResponseEntity.ok(Map.of("message", "✅ Accesorio actualizado correctamente."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Error al actualizar el accesorio: " + e.getMessage()));
        }
    }

    @GetMapping("/searchEntrega")
    public ResponseEntity<List<Map<String, Object>>> searchProcessAccesory(
            @RequestParam String estadoLimpieza) {
        // Ejecuta el servicio
        List<Map<String, Object>> searchEntrega = accesorioService.searchProcessAccesory(
                estadoLimpieza.trim());
        // Si no hay resultados
        if (searchEntrega == null || searchEntrega.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        // Devuelve lista JSON
        return ResponseEntity.ok(searchEntrega);
    }
}
