package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.CerrarCompletoDTO;
import com.woden.wms_backend.dto.CountPalletDTO;
import com.woden.wms_backend.dto.PalletDTO;
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
    @PostMapping("/create")
    public ResponseEntity<String> createPallet(@RequestBody PalletModel pallet) {
        try {
            palletService.createPallet(pallet);
            return ResponseEntity.ok("Pallet creado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el pallet: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PalletDTO>> searchEntry(
            @RequestParam String numero,
            @RequestParam String destino,
            @RequestParam int usuarioId) {
        List<PalletDTO> pallets = palletService.searchEntry(numero, destino, usuarioId);
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
    public ResponseEntity<String> cerrarPallet(@RequestBody CerrarCompletoDTO dto) {
        try {
            ingresoService.cerrarIngreso(dto.getPalletId(), dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
                    dto.getOpcion());
            palletService.cerrarPallet(dto.getPalletId(), dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(),
                    dto.getEstadoId());
            return ResponseEntity.ok("✅ Pallet cerrado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al cerrar el pallet: " + e.getMessage());
        }
    }
}
