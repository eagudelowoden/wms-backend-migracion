package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.woden.wms_backend.dto.*;
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
import com.woden.wms_backend.dto.clientDTO.PalletStorageDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.PalletService;

@RestController
@RequestMapping("/client/pallets")
public class PalletController extends BaseController<PalletModel, Integer> {
    public PalletController(PalletService service) {
        super(service);
    }

    @Autowired
    private PalletService palletService;
    @Autowired
    private IngresoService ingresoService;

    @PostMapping("/create/{kitEntryOn}")
    public ResponseEntity<Map<String, String>> createPallet(@RequestBody PalletModel pallet,
            @PathVariable Boolean kitEntryOn) {
        Map<String, String> response = new HashMap<>();
        try {
            palletService.createPallet(pallet, kitEntryOn);
            response.put("message", "Pallet creado exitosamente.");
            return ResponseEntity.ok(response); // Devuelve un JSON en lugar de un String
        } catch (Exception e) {
            response.put("error", "Error al crear el pallet: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/getModel/{id}")
    public ResponseEntity<?> getPalletById(@PathVariable int id) {
        PalletModel pallet = palletService.getModel(id);
        if (pallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pallet no encontrado");
        }
        return ResponseEntity.ok(pallet);
    }

    @GetMapping("/transito")
    public ResponseEntity<List<Map<String, Object>>> searchTransitPallet(
            @RequestParam(required = false, defaultValue = "") String numero) {
        List<Map<String, Object>> data = palletService.searchTransitPallet(numero);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PalletDTO>> searchEntry(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam int usuarioId) {
        List<PalletDTO> pallets = palletService.searchEntry(numero, destino, usuarioId);
        return ResponseEntity.ok(pallets);
    }

    @GetMapping("/searchAccesory")
    public ResponseEntity<List<PalletDTO>> searchAccesory(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam int usuarioId) {
        List<PalletDTO> pallets = palletService.searchAccesory(numero, destino, usuarioId);
        return ResponseEntity.ok(pallets);
    }

    @PostMapping("/cantidad-seriales")
    public ResponseEntity<Map<String, Integer>> getCount(@RequestBody CountPalletDTO dto) {
        Integer cantidad = palletService.getCount(dto.getPalletId(), dto.getTabla());
        Map<String, Integer> response = new HashMap<>();
        response.put("cantidad", cantidad);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Map<String, String>> cerrarPallet(@RequestBody CerrarIngresoDTO dto) {
        Map<String, String> response = new HashMap<>();
        try {
            ingresoService.sendEntry(dto.getPalletId(), dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
                    dto.getOpcion());
            palletService.cerrarPallet(dto.getPalletId(), dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(),
                    dto.getEstadoId());

            response.put("message", "Pallet cerrado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error al cerrar el pallet: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/eliminar/{palletId}")
    public ResponseEntity<Map<String, String>> eliminarPallet(
            @PathVariable Integer palletId,
            @RequestParam(required = false) String tipoEquipo,
            @RequestParam(required = false) String tipo) {

        boolean eliminado = palletService.deletePallet(palletId, tipoEquipo, tipo);
        Map<String, String> response = new HashMap<>();

        if (eliminado) {
            response.put("message", "Pallet eliminado exitosamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se puede eliminar: El pallet tiene cajas o ingresos.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/confirmar-transito")
    public ResponseEntity<Map<String, String>> confirmarPallet(@RequestBody ConfirmarPalletDTO dto) {
        boolean success = palletService.confirmarPalletTransito(dto);
        Map<String, String> response = new HashMap<>();

        if (success) {
            response.put("message", "Pallet confirmado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se pudo confirmar el pallet.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/abrir-transito")
    public ResponseEntity<Map<String, String>> abrirPallet(@RequestBody AbrirPalletDTO dto) {
        boolean success = palletService.abrirPalletTransito(dto);
        Map<String, String> response = new HashMap<>();

        if (success) {
            response.put("message", "Pallet abierto correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se pudo abrir el pallet.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/regularizacion/lote-pallet")
    public ResponseEntity<Map<String, String>> regularizarLotePallet(@RequestBody RegularizarLotePalletDTO dto) {
        palletService.regularizarLotePallet(dto.getPalletId(), dto.getLoteId(), dto.getUsuarioIdMovimiento());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Lote del pallet actualizado correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-receive")
    public ResponseEntity<List<PalletDTO>> searchReceivePallet(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam String tipo) {

        List<PalletDTO> pallets = palletService.searchReceivePallet(numero, destino, tipo);
        return ResponseEntity.ok(pallets);
    }


    @GetMapping("/searchPalletsBoxes")
    public ResponseEntity<?> SearchPalletBoxPallet(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<Map<String, String>> pallets = palletService.SearchPalletBoxPallet(numero, destino, page, size);
        return ResponseEntity.ok(pallets);
    }

    @DeleteMapping("/deleteAccesory/{palletId}/{cantidad}/{codigoSapId}")
    public ResponseEntity<String> eliminarAccesorio(
            @PathVariable Integer palletId,
            @PathVariable Integer cantidad,
            @PathVariable Integer codigoSapId) {
        boolean eliminado = palletService.eliminarAccesorio(palletId, cantidad, codigoSapId);
        return eliminado ? ResponseEntity.ok("Accesorio eliminado correctamente.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo eliminar el accesorio.");
    }

    @PostMapping("/sendPallet")
    public ResponseEntity<Map<String, Object>> sendPallet(@RequestBody SendPalletDTO dto) {
        try {
            // Inicializar el parámetro de salida
            Integer[] filasOut = new Integer[] { 0 };

            // Ejecutar el procedimiento almacenado
            palletService.sendPallet(dto);

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("affectedRows", filasOut[0]);
            response.put("message", "Pallet enviado correctamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al enviar el pallet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/searchStoragePallet")
    public ResponseEntity<List<PalletStorageDTO>> searchStoragePallet(@RequestParam String numero,
            @RequestParam String tipo,
            @RequestParam String tipoAccesorio) {
        List<PalletStorageDTO> pallets = palletService.searchStoragePallet(numero, tipo,
                tipoAccesorio);
        return ResponseEntity.ok(pallets);
    }

    @PostMapping("/updatePosition")
    public void updatePosition(@RequestBody Map<String, Integer> requestBody) {
        Integer palletId = requestBody.get("palletId");
        Integer posicionId = requestBody.get("posicionId");
        palletService.updatePosition(palletId, posicionId);
    }

    @PostMapping("/updateTipologia")
    public void updateTipologia(@RequestBody Map<String, Integer> requestBody) {
        Integer palletId = requestBody.get("palletId");
        Integer tipologiaId = requestBody.get("tipologiaId");
        palletService.updateTipologia(palletId, tipologiaId);
    }

    @PostMapping("/inactivate")
    public ResponseEntity<String> inactivate(@RequestBody Map<String, Integer> body) {
        Integer palletId = body.get("palletId");
        palletService.inactivatePallet(palletId);
        return ResponseEntity.ok("Pallet inactivado correctamente");
    }


    @GetMapping("/searchStorageGroupPalletAccesory")
    public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPalletAccesory(
            @RequestParam String numero,
            @RequestParam String tipoAccesorio) {
        List<PalletStorageDTO> pallets = palletService.searchStorageGroupPalletAccesory(numero, tipoAccesorio);
        return ResponseEntity.ok(pallets);
    }

    @GetMapping("/searchStorageGroupPalletAccesoryCreated")
    public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPalletAccesoryCreated(
            @RequestParam String numero,
            @RequestParam String tipoAccesorio) {
        List<PalletStorageDTO> pallets = palletService.searchStorageGroupPalletAccesoryCreated(numero, tipoAccesorio);
        return ResponseEntity.ok(pallets);
    }

    @GetMapping("/searchStorageGroupPallet")
    public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPallet(@RequestParam String numero,
            @RequestParam String tipoEquipo) {
        List<PalletStorageDTO> pallets = palletService.searchStorageGroupPallet(numero, tipoEquipo);
        return ResponseEntity.ok(pallets);
    }

    @PostMapping("/unifyPallets")
    public ResponseEntity<Map<String, String>> unifyPallet(@RequestBody List<Integer> palletIds) {
        palletService.unifyPallet(palletIds);
        return ResponseEntity.ok(Map.of("message", "Pallets unificados correctamente"));
    }

    @GetMapping("/getListPallets")
    public ResponseEntity<?> getListPallets(@RequestParam String destino) {
        List<String> pallets = palletService.getListPallets(destino);
        return ResponseEntity.ok(pallets);
    }

    @GetMapping("/getIdPallet")
    public ResponseEntity<Integer> getIdPallet(@RequestParam String numero) {
        Integer idPallet = palletService.getIdPallet(numero);
        if (idPallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(idPallet);
    }

    @PostMapping("/updateSapCodePallet")
    public ResponseEntity<?> updateSapCodePallet(@RequestParam Integer palletId, @RequestParam Integer codigoSapId) {
        return ResponseEntity.ok(palletService.updateSapCodePallet(palletId, codigoSapId, 0));
    }

    @GetMapping("/searchEntrega")
    public ResponseEntity<List<PalletDTO>> searchPackingDeliveryPallet(
            @RequestParam String numero,
            @RequestParam String tipologia,
            @RequestParam String tipo) {

        List<PalletDTO> pallets = palletService.searchPackingDeliveryPallet(numero, tipologia, tipo);
        return ResponseEntity.ok(pallets);
    }



}
