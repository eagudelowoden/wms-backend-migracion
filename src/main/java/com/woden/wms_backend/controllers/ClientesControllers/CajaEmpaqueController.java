package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.dto.CajaEmpaqueDTO;
import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.services.ClienteServices.CajaEmpaqueService;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.CajaEmpaqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/cajaEmpaque")
public class CajaEmpaqueController extends BaseController<CajaEmpaqueModel, Integer> {

    public CajaEmpaqueController(CajaEmpaqueService service) {
        super(service);
    }
    @Autowired
    private CajaEmpaqueService cajaEmpaqueService;

    @PostMapping("/updateStatusBoxPacking")
    public ResponseEntity<Map<String, String>> updateStatusBoxPacking(@RequestBody Map<String, Integer> requestBody) {
        Integer cajaEmpaqueId = requestBody.get("cajaEmpaqueId");
        Integer estadoId = requestBody.get("estadoId");

        System.out.println("Actualizando caja empaque ID: " + cajaEmpaqueId + " con estado ID: " + estadoId);

        // Aquí iría la lógica para actualizar el estado en la BD
        cajaEmpaqueService.updateStatusBoxPacking(cajaEmpaqueId, estadoId);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente."));
    }

    @GetMapping("/searchCajas")
    public ResponseEntity<List<CajaEmpaqueDTO>> SearchReceivePacking(
            @RequestParam String estado,
            @RequestParam String numero) {
        List<CajaEmpaqueDTO> cajas = cajaEmpaqueService.SearchReceivePacking(estado, numero);
        return ResponseEntity.ok(cajas);
    }







}
