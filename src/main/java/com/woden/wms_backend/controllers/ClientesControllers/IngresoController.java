package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.Date;
import java.text.SimpleDateFormat;
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
import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.dto.RegularizarLoteSerialDTO;
import com.woden.wms_backend.dto.RegularizarSapDTO;
import com.woden.wms_backend.dto.RegularizarSapSerialIngresoDTO;
import com.woden.wms_backend.dto.clientDTO.SendIngresoDTO;
import com.woden.wms_backend.dto.clientDTO.SendStorageEntryDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.ZplService;

@RestController
@RequestMapping("/client/ingresos")
public class IngresoController extends BaseController<IngresoModel, Integer> {

    public IngresoController(IngresoService service) {
        super(service);
    }

    @Autowired
    private IngresoService ingresoService;
    private ZplService zplService;

    // @PostMapping("/create")
    // public ResponseEntity<Map<String, String>> createIngreso(@RequestBody
    // IngresoModel ingreso) {
    // try {
    // ingresoService.createIngreso(ingreso);

    // Map<String, String> response = new HashMap<>();
    // response.put("message", "Ingreso registrado correctamente.");

    // return ResponseEntity.ok(response); // ✅ Devuelve application/json
    // } catch (Exception e) {
    // Map<String, String> response = new HashMap<>();
    // response.put("error", "Error al registrar el ingreso: " + e.getMessage());

    // return ResponseEntity.status(500).body(response);
    // }
    // }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createIngreso(@RequestBody IngresoModel ingreso) {
        Map<String, String> response = new HashMap<>();
        try {
            String resultado = ingresoService.createIngresoCallable(ingreso);

            if (resultado == null) {
                response.put("message", "Ingreso registrado correctamente.");
                return ResponseEntity.ok(response);
            }

            // Analizar mensaje de error para identificar el campo específico
            String mensaje = switch (resultado) {
                case "1001" -> "Serial duplicado.";
                case "1002" -> "MAC duplicada.";
                default -> "Error al registrar ingreso: " + resultado;
            };

            response.put("error", mensaje);
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("error", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/eliminarIngresos")
    public ResponseEntity<Integer> eliminarIngresos(@RequestBody List<String> seriales) {
        int status = ingresoService.eliminarIngresos(seriales);
        return ResponseEntity.ok(status);
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

    @PostMapping("/regularizacion/lote")
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

    @GetMapping("/getModelSerial/{serial}")
    public ResponseEntity<IngresoModel> getModelSerial(@PathVariable String serial) {
        IngresoModel ingreso = ingresoService.getModelIngreso(serial);
        if (ingreso != null) {
            return ResponseEntity.ok(ingreso);
        } else {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
    }

    @GetMapping("/getSerialByMac/{mac}")
    public String getSerialByMac(@PathVariable String mac) {
        String serial = ingresoService.getSerialByMac(mac);
        return serial;
    }

    @GetMapping("/getReingresos/{serial}")
    public Integer getReingresos(@PathVariable String serial) {
        Integer reingresos = ingresoService.getReingresos(serial);
        return reingresos;
    }

    @GetMapping("/getProactiveRepair/{serial}")
    public ResponseEntity<Integer> getProactiveRepair(@PathVariable String serial) {
        Integer proactiveRepair = ingresoService.getProactiveRepair(serial);
        return ResponseEntity.ok(proactiveRepair);
    }

    @GetMapping("/getProactiveRepairAll")
    public ResponseEntity<List<String>> getProactiveRepairAll() {
        List<String> proactiveRepair = ingresoService.getProactiveRepairAll();
        return ResponseEntity.ok(proactiveRepair);
    }

    @PostMapping("/updateSapCodeEntry")
    public ResponseEntity<Integer> updateSapCodeEntry(@RequestBody RegularizarSapSerialIngresoDTO dto) {
        return ResponseEntity
                .ok(ingresoService.UpdateSapCode(dto.getCodigoSapId(), dto.getUsuarioIdMovimiento(), dto.getSerial()));
    }

    @PostMapping("/imprimirHabladores")
    public ResponseEntity<String> imprimirHabladores(@RequestBody List<IngresoModel> serialesSeleccionados) {
        try {
            String zplGenerado = zplService.generarZpl(serialesSeleccionados);
            return ResponseEntity.ok(zplGenerado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar ZPL: " + e.getMessage());
        }
    }

    @PostMapping("/sendStorageEntry")
    public ResponseEntity<?> sendStorageEntry(@RequestBody SendStorageEntryDTO request) {
        try {
            ingresoService.SendStorageEntry(request.getPalletId(), request.getEstadoId(), request.getTipologiaId(),
                    request.getUsuarioId());
            return ResponseEntity.ok(Map.of("message", "Pallet almacenado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al almacenar pallet: " + e.getMessage()));
        }
    }

    @PostMapping("/sendEntry")
    public ResponseEntity<?> sendEntry(@RequestBody SendIngresoDTO dto) {
        ingresoService.sendEntry(
                dto.getEstadoId(),
                dto.getTipologiaId(),
                dto.getUsuarioId(),
                dto.getPalletId(),
                dto.getOpcion());
        return ResponseEntity.ok(Map.of("message", "Ingreso enviado correctamente."));
    }

    @PostMapping("/updateTipologiaEntry")
    public void updateTipologia(@RequestBody Map<String, Integer> requestBody) {
        Integer palletId = requestBody.get("palletId");
        Integer tipologiaId = requestBody.get("tipologiaId");
        ingresoService.updateTipologia(palletId, tipologiaId);
    }

    @PostMapping("/unifyEntry")
    public ResponseEntity<?> unifyEntry(@RequestBody Map<String, Object> requestBody) {
        try {
            Integer palletIdDestino = (Integer) requestBody.get("palletIdDestino");
            Integer tipologiaId = (Integer) requestBody.get("tipologiaId");
            Integer usuarioId = (Integer) requestBody.get("usuarioId");
            List<?> palletIdsRaw = (List<?>) requestBody.get("palletIds");
            List<Integer> palletIds = palletIdsRaw.stream()
                    .map(obj -> (obj instanceof Integer) ? (Integer) obj : Integer.parseInt(obj.toString()))
                    .toList();
            ingresoService.unifyEntry(palletIdDestino, tipologiaId, usuarioId, palletIds);
            return ResponseEntity.ok(Map.of("message", "Ingreso unificado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al unificar ingreso: " + e.getMessage()));
        }
    }

    @PostMapping("/updatePalletEntry")
    public ResponseEntity<?> updatePalletEntry(@RequestBody Map<String, Object> requestBody) {
        Integer palletId = (Integer) requestBody.get("palletId");
        Integer usuarioIdMovimiento = (Integer) requestBody.get("usuarioIdMovimiento");
        List<?> serialesRaw = (List<?>) requestBody.get("seriales");
        List<String> seriales = serialesRaw.stream()
                .map(Object::toString)
                .toList();

        ingresoService.updatePalletEntry(palletId, usuarioIdMovimiento, seriales);
        return ResponseEntity.ok(1);
    }

    @PostMapping("/updatePalletAndTipologyEntry")
    public ResponseEntity<?> updatePalletAndTipologyEntry(@RequestBody Map<String, Object> requestBody) {
        Integer palletId = (Integer) requestBody.get("palletId");
        Integer tipologiaId = (Integer) requestBody.get("tipologiaId");
        Integer usuarioIdMovimiento = (Integer) requestBody.get("usuarioIdMovimiento");
        Integer estadoId = (Integer) requestBody.get("estadoId");
        List<?> serialesRaw = (List<?>) requestBody.get("seriales");
        List<String> seriales = serialesRaw.stream()
                .map(Object::toString)
                .toList();

        ingresoService.updatePalletAndTipologyEntry(palletId, tipologiaId, usuarioIdMovimiento, estadoId, seriales);
        return ResponseEntity.ok(1);
    }

    @GetMapping("/getLevelEntry")
    public ResponseEntity<List<String>> getLevelEntry(@RequestParam String serial) {
        List<String> nivel = ingresoService.getLevelEntry(serial);
        return ResponseEntity.ok(nivel);
    }

    @PostMapping("/updateStateAllEntry")
    public ResponseEntity<?> updateStateAllEntry(@RequestBody List<Map<String, Object>> requestList) {
        try {
            for (Map<String, Object> requestBody : requestList) {
                Integer estadoId = (Integer) requestBody.get("estadoId");
                Integer usuarioId = (Integer) requestBody.get("usuarioIdMovimiento");
                String serial = (String) requestBody.get("serial");

                ingresoService.updateStateAllEntry(estadoId, usuarioId, serial);
            }
            return ResponseEntity.ok(1);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }

    @GetMapping("/searchDiagnosticEntry")
    public ResponseEntity<?> searchDiagnosticEntry(
            @RequestParam String estadoFinal,
            @RequestParam String perfil,
            @RequestParam Integer usuarioId) {
        List<Map<String, String>> diagnosticEntries = ingresoService.searchDiagnosticEntry(estadoFinal, perfil,
                usuarioId);
        return ResponseEntity.ok(diagnosticEntries);
    }

    @GetMapping("/searchDelivery")
    public ResponseEntity<?> searchDeliveryEntry(@RequestParam String serial) {
        List<String> deliveryEntries = ingresoService.searchDeliveryEntry(serial);
        if (deliveryEntries != null) {
            return ResponseEntity.ok(deliveryEntries);
        } else {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
    }

    @PostMapping("/updateLevel")
    public ResponseEntity<?> updateLevel(@RequestBody Map<String, Integer> requestBody) {
        Integer levelId = requestBody.get("levelId");
        Integer palletId = requestBody.get("palletId");

        ingresoService.updateLevel(levelId, palletId);
        return ResponseEntity.ok(Map.of("message", 1));
    }

    @PostMapping("/updateStateEntryNotUsuario")
    public ResponseEntity<?> updateStateEntryNotUsuario(@RequestBody Map<String, Object> requestBody) {
        Integer estadoId = (Integer) requestBody.get("estadoId");
        Integer palletId = (Integer) requestBody.get("palletId");
        Integer fecha = (Integer) requestBody.get("fecha");
        List<?> serialRaw = (List<?>) requestBody.get("serial");
        List<String> serial = serialRaw.stream().map(Object::toString).toList();

        for (String s : serial) {
            ingresoService.updateStateEntryNotUsuario(estadoId, palletId, fecha, s);
        }
        return ResponseEntity.ok(Map.of("message", 1));
    }

    @PostMapping("/updateStateEntry")
    public ResponseEntity<?> updateStateEntry(
            @RequestBody Map<String, Object> requestBody) {
        try {
            Integer estadoId = (Integer) requestBody.get("estadoId");
            Integer palletId = (Integer) requestBody.get("palletId");
            Integer usuarioId = (Integer) requestBody.get("usuarioId");
            Integer fecha = (Integer) requestBody.get("fecha");
            List<?> serialRaw = (List<?>) requestBody.get("seriales");
            List<String> serial = serialRaw.stream().map(Object::toString).toList();

            for (String s : serial) {

                ingresoService.updateStateEntry(estadoId, palletId, usuarioId, fecha, s);
            }

            return ResponseEntity.ok(1);

        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }

    @GetMapping("/updateChangeStateEntry")
    public ResponseEntity<?> updateChangeStateEntry(@RequestBody Map<String, Object> requestBody) {
        String serial1 = (String) requestBody.get("serial1");
        String serial2 = (String) requestBody.get("serial2");
        String mac = (String) requestBody.get("mac");
        Integer estadoId = (Integer) requestBody.get("estadoId");

        ingresoService.updateChangedEntry(serial1, serial2, mac, estadoId);
        return ResponseEntity.ok(Map.of("message", "1"));
    }

    @PostMapping("/updateClasificationEntry")
    public ResponseEntity<?> updateClasificationEntry(@RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        List<Map<String, Object>> ingresos = requestBody.get("ingresos");
        for (Map<String, Object> ingreso : ingresos) {
            Integer estadoId = (Integer) ingreso.get("estadoId");
            Integer nivelId = (Integer) ingreso.get("nivelId");
            Integer usuarioId = (Integer) ingreso.get("usuarioIdMovimiento");
            String serial = (String) ingreso.get("serial");

            ingresoService.updateClasificationEntry(serial, estadoId, nivelId, usuarioId);
        }

        return ResponseEntity.ok(Map.of("message", 1));
    }

    @GetMapping("/searchClasificationEntry")
    public ResponseEntity<?> searchClasificationEntry() {
        List<Map<String, String>> clasificationEntries = ingresoService.searchClasificationEntry();
        return ResponseEntity.ok(clasificationEntries);
    }

    @PostMapping("/updateStateOneEntry")
    public ResponseEntity<?> updateStateOneEntry(@RequestBody Map<String, Object> requestBody) {
        try {
            Integer estadoId = (Integer) requestBody.get("estadoId");
            Integer nivelId = (Integer) requestBody.get("nivelId");
            Integer usuarioIdMovimiento = (Integer) requestBody.get("usuarioIdMovimiento");
            String fechaStr = (String) requestBody.get("fecha");
            String serial = (String) requestBody.get("serial");

            // Parse ISO date string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date fecha = dateFormat.parse(fechaStr);

            ingresoService.updateStateOneEntry(estadoId, nivelId, usuarioIdMovimiento, fecha, serial);
            return ResponseEntity.ok(1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al procesar la fecha: " + e.getMessage()));
        }
    }

    @PostMapping("/UpdatePackingEntry")
    public ResponseEntity<?> UpdatePackingEntry(@RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        List<Map<String, Object>> ingresos = requestBody.get("ingresos");
        for (Map<String, Object> ingreso : ingresos) {
            Integer estadoId = (Integer) ingreso.get("estadoId");
            Integer palletId = (Integer) ingreso.get("palletId");
            Integer cajaEmpaqueId = (Integer) ingreso.get("cajaEmpaqueId");
            Integer usuarioId = (Integer) ingreso.get("usuarioIdMovimiento");
            String serial = (String) ingreso.get("serial");
            Integer loteId = (Integer) ingreso.get("loteId");
            ingresoService.UpdatePackingEntry(estadoId, palletId, cajaEmpaqueId, usuarioId, serial, loteId);
        }
        return ResponseEntity.ok(1);
    }

    @PostMapping("/updatePackingEntrySmartCard")
    public ResponseEntity<Integer> updatePackingEntrySmartCard(
            @RequestParam Integer estadoId,
            @RequestParam Integer palletId,
            @RequestParam Integer cajaEmpaqueId,
            @RequestParam Integer usuarioId,
            @RequestParam String serial,
            @RequestParam Integer loteId,
            @RequestParam Integer SmardCardId,
            @RequestParam String SmartCard) {
        try {
            ingresoService.UpdatePackingEntrySmartCard(
                    estadoId, palletId, cajaEmpaqueId, usuarioId, serial, loteId, SmardCardId, SmartCard);
            return ResponseEntity.ok(1); // ✅ éxito
        } catch (Exception e) {
            // Puedes loggear el error
            e.printStackTrace();
            return ResponseEntity.badRequest().body(0); // ❌ error
        }
    }

    @PostMapping("/updatePackingAllEntry")
    public ResponseEntity<Integer> UpdatePackingAllEntry(@RequestParam Integer estadoId,
            @RequestParam Integer usuarioIdMovimiento, @RequestParam List<String> seriales) {
        try {
            int result = ingresoService.UpdatePackingAllEntry(estadoId, usuarioIdMovimiento, seriales);
            // System.out.println("updatePackingAllEntry");
            return ResponseEntity.ok(result); // ✅ devuelve 1 si se procesó al menos un serial
        } catch (Exception e) {
            e.printStackTrace(); // log del error
            return ResponseEntity.badRequest().body(0); // ❌ error
        }
    }

    @PostMapping("/updateChangedPacking")
    public ResponseEntity<?> updateChangedPacking(@RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        List<Map<String, Object>> ingresos = requestBody.get("ingresos");
        for (Map<String, Object> ingreso : ingresos) {
            Integer estadoId = (Integer) ingreso.get("estadoId");
            Integer palletId = (Integer) ingreso.get("palletId");
            Integer cajaEmpaqueId = (Integer) ingreso.get("cajaEmpaqueId");
            String serial = (String) ingreso.get("serial");
            Integer usuarioId = (Integer) ingreso.get("usuarioIdMovimiento");
            Integer tipologiaId = (Integer) ingreso.get("tipologiaId");
            ingresoService.updateChangedPacking(estadoId, palletId, cajaEmpaqueId, serial, usuarioId, tipologiaId);
        }
        return ResponseEntity.ok(1);
    }

    @PostMapping("/updateSmartCardEntry")
    public ResponseEntity<?> updateSmartCardEntry(@RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        List<Map<String, Object>> ingresos = requestBody.get("ingresos");
        for (Map<String, Object> ingreso : ingresos) {
            Integer smartCardId = (Integer) ingreso.get("smartCardId");
            String smartCardNuevo = (String) ingreso.get("smartCardNuevo");
            String serial = (String) ingreso.get("serial");
            ingresoService.updateSmartCardEntry(smartCardId, smartCardNuevo, serial);
        }
        return ResponseEntity.ok(1);
    }

    @PostMapping("/backRepairedEntry")
    public ResponseEntity<?> backRepairedEntry(@RequestBody Map<String, Object> requestBody) {
        List<?> serialesRaw = (List<?>) requestBody.get("seriales");
        List<String> seriales = serialesRaw.stream().map(Object::toString).toList();
        try {
            for (String serial : seriales) {
                ingresoService.backRepairedEntry(serial);
            }
            return ResponseEntity.ok(1);
        } catch (Exception e) {
            e.printStackTrace(); // log del error
            return ResponseEntity.badRequest().body(0); // ❌ error
        }
    }

    @GetMapping("/searchRepairEntry")
    public ResponseEntity<?> searchRepairEntry(
            @RequestParam String estadoFinal,
            @RequestParam String perfil,
            @RequestParam Integer usuarioId) {
        List<Map<String, String>> repairEntries = ingresoService.searchRepairEntry(estadoFinal, perfil,
                usuarioId);
        return ResponseEntity.ok(repairEntries);
    }
}
