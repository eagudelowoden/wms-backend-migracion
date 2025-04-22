package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.dto.RegularizarLoteSerialDTO;
import com.woden.wms_backend.dto.RegularizarSapDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;

@RestController
@RequestMapping("/client/ingresos")
public class IngresoController extends BaseController<IngresoModel, Integer> {

    public IngresoController(IngresoService service) {
        super(service);
    }

    @Autowired
    private IngresoService ingresoService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createIngreso(@RequestBody IngresoModel ingreso) {
        try {
            ingresoService.createIngreso(ingreso);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Ingreso registrado correctamente.");

            return ResponseEntity.ok(response); // ✅ Devuelve application/json
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al registrar el ingreso: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/searchIngreso/{palletId}")
    public ResponseEntity<List<IngresoDTO>> searchEntryReingreso(@PathVariable Integer palletId) {
        List<IngresoDTO> results = ingresoService.searchEntryReingreso(palletId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/transito/{palletId}")
    public List<IngresoTransitoDTO> getIngresosTransitoByPallet(@PathVariable Integer palletId) {
        return ingresoService.getIngresoTransitByPalletId(palletId);
    }

    @PostMapping("/regularizacion/sap")
    public ResponseEntity<Map<String, String>> regularizarSap(@RequestBody RegularizarSapDTO dto) {
        ingresoService.regularizarSap(dto.getSerial(), dto.getCodigoSapId(), dto.getUsuarioIdMovimiento());
        Map<String, String> response = new HashMap<>();
        response.put("message", "SAP actualizado correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/regularizacion-lote-serial")
    public ResponseEntity<Map<String, String>> regularizarLoteSerial(@RequestBody RegularizarLoteSerialDTO dto) {
        ingresoService.regularizarLoteSerial(dto.getSerial(), dto.getLoteId(), dto.getUsuarioIdMovimiento());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Lote del serial actualizado correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generar-ilegible")
    public ResponseEntity<?> generarIngresoIlegible(@RequestBody IngresoIlegibleDTO ingresoDTO,
            @RequestParam String cliente,
            @RequestParam Integer usuarioId) {
        String serialGenerado = ingresoService.generarIngresoIlegible(ingresoDTO, cliente, usuarioId);
        return (serialGenerado != null)
                ? ResponseEntity.ok().body(Map.of("serial", serialGenerado))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar ingreso");
    }
}
