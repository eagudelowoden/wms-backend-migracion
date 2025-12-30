package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.dto.CajaEmpaqueDTO;
import com.woden.wms_backend.services.ClienteServices.CajaEmpaqueService;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
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

    @GetMapping("/searchProcessCajas")
    public ResponseEntity<List<CajaEmpaqueDTO>> SearchProcessBoxPacking(
            @RequestParam Integer palletId,
            @RequestParam Integer cajaEmpaqueId,
            @RequestParam String estado) {
        List<CajaEmpaqueDTO> cajas = cajaEmpaqueService.SearchProcessBoxPacking(palletId, cajaEmpaqueId, estado);
        return ResponseEntity.ok(cajas);
    }

    @GetMapping("/SearchPackingBoxesProcess")
    public ResponseEntity<List<CajaEmpaqueDTO>> SearchPacking(
            @RequestParam Integer palletId) {
        List<CajaEmpaqueDTO> cajas = cajaEmpaqueService.SearchPacking(palletId);
        return ResponseEntity.ok(cajas);
    }

    @GetMapping("/searchPalletBoxEntry")
    public ResponseEntity<?> searchPalletBoxEntry(
            @RequestParam String estado,
            @RequestParam Integer palletId,
            @RequestParam Integer cajaEmpaqueId) {
        List<Map<String, String>> seriales = cajaEmpaqueService.searchPalletBoxEntry(estado, palletId, cajaEmpaqueId);
        return ResponseEntity.ok(seriales);
    }

    @PostMapping("/createBoxPacking")
    public ResponseEntity<?> createEntity(@RequestBody CajaEmpaqueModel requestBody) {
        cajaEmpaqueService.create(requestBody.getNumero(), requestBody.getPalletId(), requestBody.getEstadoId(),
                requestBody.getUsuarioId(), requestBody.getFecha());
        return ResponseEntity.ok(1);
    }

    @DeleteMapping("/eliminarCajas")
    public ResponseEntity<Integer> eliminarCaja(
            @RequestParam Integer cajaEmpaqueId) {
        int status = cajaEmpaqueService.eliminarCaja(cajaEmpaqueId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/pallet/{palletId}")
    public ResponseEntity<List<String>> getSerialesByPallet(@PathVariable Integer palletId) {
        List<String> seriales = cajaEmpaqueService.getSerialesByPallet(palletId);
        return ResponseEntity.ok(seriales);
    }

    @GetMapping("/getCountBoxPacking")
    public Integer getCountBoxDispatch(@RequestParam Integer cajaEmpaqueId) {
        return cajaEmpaqueService.getCountBoxPacking(cajaEmpaqueId);
    }

    @PutMapping("/updateStatusAllBoxPacking")
    public ResponseEntity<Integer> updateStatusAllBoxPacking(@RequestParam Integer palletId,
            @RequestParam Integer estadoId) {
        return ResponseEntity.ok(cajaEmpaqueService.updateStatusAllBoxPacking(palletId, estadoId));
    }

    @GetMapping("/getLastBoxPacking")
    public Integer getLastBoxPacking(@RequestParam Integer palletId) {
        return cajaEmpaqueService.getLastBoxPacking(palletId);
    }


}
