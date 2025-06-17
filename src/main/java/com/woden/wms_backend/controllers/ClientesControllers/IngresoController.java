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
  public Integer getProactiveRepair(@PathVariable String serial) {
    Integer proactiveRepair = ingresoService.getProactiveRepair(serial);
    return proactiveRepair;
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
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar ZPL: " + e.getMessage());
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
    ingresoService.sendEntry(dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(), dto.getPalletId(), 0);
    return ResponseEntity.ok(Map.of("message", "Ingreso enviado correctamente."));
  }

  @PostMapping("/updateTipologiaEntry")
  public void updateTipologia(@RequestBody Map<String, Integer> requestBody) {
    Integer palletId = requestBody.get("palletId");
    Integer tipologiaId = requestBody.get("tipologiaId");
    ingresoService.updateTipologia(palletId, tipologiaId);
  }

}
