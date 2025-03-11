package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.ClienteServices.PalletService;

@RestController
@RequestMapping("/api/pallets")
public class PalletController extends BaseController<PalletModel, Integer> {
    public PalletController(PalletService service) {
        super(service);
    }

    @Autowired
    private PalletService palletService;

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
}
