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
import com.woden.wms_backend.dto.AbrirPalletDTO;
import com.woden.wms_backend.dto.CerrarIngresoDTO;
import com.woden.wms_backend.dto.ConfirmarPalletDTO;
import com.woden.wms_backend.dto.CountPalletDTO;
import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.dto.RegularizarLotePalletDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.PalletService;

@RestController
@RequestMapping("/api/pallets")
public class PalletController extends BaseController<PalletModel, Integer> {
    public PalletController(PalletService service) {
        super(service);
    }

    @Autowired
    private PalletService palletService;
    @Autowired
    private IngresoService ingresoService;

    @PostMapping("/create/{clienteId}")
    public ResponseEntity<Map<String, String>> createPallet(@RequestBody PalletModel pallet,
            @PathVariable int clienteId) {
        Map<String, String> response = new HashMap<>();
        try {
            palletService.createPallet(pallet, clienteId);
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
            ingresoService.cerrarIngreso(dto.getPalletId(), dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
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
            @RequestParam String tipoEquipo,
            @RequestParam String tipo) {

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
    public ResponseEntity<String> confirmarPallet(@RequestBody ConfirmarPalletDTO dto) {
        palletService.confirmarPalletTransito(dto);
        return ResponseEntity.ok("✅ Pallet confirmado correctamente.");
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
    public ResponseEntity<List<Map<String, Object>>> searchReceivePallet(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam String tipo) {

        List<Map<String, Object>> pallets = palletService.searchReceivePallet(numero, destino, tipo);
        return ResponseEntity.ok(pallets);
    }

    @DeleteMapping("/deleteAccesory/{palletId}/{cantidad}/{codigoSapId}")
    public ResponseEntity<String> eliminarAccesorio(
            @PathVariable int palletId,
            @PathVariable int cantidad,
            @PathVariable int codigoSapId) {
        boolean eliminado = palletService.eliminarAccesorio(palletId, cantidad, codigoSapId);
        return eliminado ? ResponseEntity.ok("Accesorio eliminado correctamente.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo eliminar el accesorio.");
    }
}
