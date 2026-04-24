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
import com.woden.wms_backend.services.ClienteServices.PalletService;

@RestController
@RequestMapping("/client/pallets")
public class PalletController extends BaseController<PalletModel, Integer> {
  public PalletController(PalletService service) {
    super(service);
  }

  @Autowired
  private PalletService palletService;

  @PostMapping("/create/{kitEntryOn}")
  public ResponseEntity<Map<String, String>> createPallet(@RequestBody PalletModel pallet,
      @PathVariable Boolean kitEntryOn) {
    Map<String, String> response = new HashMap<>();
    try {
      String numeroPallet = palletService.createPallet(pallet, kitEntryOn);
      response.put("message", "Pallet creado exitosamente.");
      response.put("numero", numeroPallet);
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
  public ResponseEntity<Integer> getCount(@RequestBody CountPalletDTO dto) {
    Integer cantidad = palletService.getCount(dto.getPalletId(), dto.getTabla());
    return ResponseEntity.ok(cantidad);
  }

  @PostMapping("/enviarPallet")
  public ResponseEntity<Map<String, Object>> enviarPallet(@RequestBody EnviarPalletDTO dto) {
    Map<String, Object> response = new HashMap<>();
    try {
      palletService.enviarPallet(
          dto.getPalletId(), dto.getDestinoId(), dto.getEstadoId(),
          dto.getTipologiaId(), dto.getPosicionId(),
          dto.getUsuarioId(), dto.getOpcion(), dto.getEstado());

      response.put("success", true);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("error", "Error al enviar el pallet: " + e.getMessage());
      return ResponseEntity.status(500).body(response);
    }
  }

  @DeleteMapping("/eliminar/{palletId}")
  public ResponseEntity<Map<String, String>> eliminarPallet(
      @PathVariable Integer palletId,
      @RequestParam(required = false) String tipoEquipo,
      @RequestParam(required = false) String tipo) {

    boolean eliminado = palletService.deletePallet1(palletId, tipoEquipo, tipo);
    Map<String, String> response = new HashMap<>();

    if (eliminado) {
      response.put("message", "Pallet eliminado exitosamente.");
      return ResponseEntity.ok(response);
    } else {
      response.put("message", "No se puede eliminar: El pallet tiene cajas o ingresos.");
      return ResponseEntity.badRequest().body(response);
    }
  }

  @DeleteMapping("/deletePallet/{palletId}")
  public ResponseEntity<Integer> deletePallet(
      @PathVariable Integer palletId) {
    Integer result = palletService.deletePallet(palletId);
    return ResponseEntity.ok(result);
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

  @GetMapping("/searchPalletsBoxesAll")
  public ResponseEntity<?> searchPalletsBoxesAll(
      @RequestParam String numero,
      @RequestParam String destino) {
    List<Map<String, String>> pallets = palletService.searchPalletsBoxesAll(numero, destino);
    return ResponseEntity.ok(pallets);
  }

  @GetMapping("/searchPalletsBoxesWeb")
  public ResponseEntity<?> SearchPalletBoxPalletWeb(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {

    List<Map<String, String>> pallets = palletService.SearchPalletBoxPalletWeb(numero, destino, page, size);
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
  public ResponseEntity<Integer> inactivate(@RequestBody Map<String, Integer> body) {
    try {
      Integer palletId = body.get("palletId");
      palletService.innactivatePallet(palletId);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
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

  @GetMapping("/getBoxPallet")
  public ResponseEntity<?> getBoxPallet(@RequestParam Integer palletId, @RequestParam String tabla) {
    return ResponseEntity.ok(palletService.getBoxPallet(palletId, tabla));
  }

  @PostMapping("/innactivatePallet")
  public ResponseEntity<Integer> innactivatePallet(@RequestParam Integer palletId) {
    try {
      palletService.innactivatePallet(palletId);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/searchQualityDeliveryPallet")
  public ResponseEntity<List<Map<String, String>>> searchQualityDeliveryPallet() {
    List<Map<String, String>> pallets = palletService.searchQualityDeliveryPallet();
    return ResponseEntity.ok(pallets);
  }

  @GetMapping("/searchPalletBoxDispatchPallet")
  public ResponseEntity<List<Map<String, String>>> searchPalletBoxDispatchPallet(@RequestParam String numero) {
    List<Map<String, String>> pallets = palletService.searchPalletBoxDispatchPallet(numero);
    return ResponseEntity.ok(pallets);
  }

  @PostMapping("/sendAllPallet")
  public ResponseEntity<Integer> sendAllPallet(
      @RequestParam Integer destinoId,
      @RequestParam Integer palletId) {
    try {
      palletService.sendAllPallet(destinoId, palletId);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/getPalletsInventory")
  public ResponseEntity<?> getPalletsInventory(@RequestParam Integer usuarioId) {
    return ResponseEntity.ok(palletService.getPalletsInventory(usuarioId));
  }

  @PostMapping("/innactivatePalletInventory")
  public ResponseEntity<Integer> innactivatePalletInventory(@RequestParam Integer palletId) {
    try {
      Integer count = palletService.innactivatePalletInventory(palletId);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/searchReceivePartsPallet")
  public ResponseEntity<List<Map<String, Object>>> searchReceivePartsPallet(
      @RequestParam String destino,
      @RequestParam String numero) {
    try {
      List<Map<String, Object>> pallets = palletService.searchReceivePartsPallet(destino, numero);
      return ResponseEntity.ok(pallets);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping("/searchGeneralInventoryPallet")
  public ResponseEntity<List<Map<String, Object>>> searchGeneralInventoryPallet(
      @RequestParam String numero,
      @RequestParam String estado,
      @RequestParam Integer usuarioId) {
    try {
      List<Map<String, Object>> pallets = palletService.searchGeneralInventoryPallet(numero, estado, usuarioId);
      return ResponseEntity.ok(pallets);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/updateStateInventoryPallet")
  public ResponseEntity<Integer> updateStateInventoryPallet(
      @RequestParam Integer palletId,
      @RequestParam Integer estadoInventario) {
    try {
      Integer result = palletService.updateStateInventoryPallet(palletId, estadoInventario);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/searchGeneralSettingsPallet")
  public ResponseEntity<List<Map<String, Object>>> searchGeneralSettingsPallet(
      @RequestParam String numero,
      @RequestParam String estado) {
    try {
      List<Map<String, Object>> pallets = palletService.searchGeneralSettingsPallet(numero, estado);
      return ResponseEntity.ok(pallets);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/reservePallet")
  public ResponseEntity<Map<String, Object>> reservePallet(@RequestBody Map<String, Object> body) {
    Map<String, Object> result = palletService.reservePallet(
        (Integer) body.get("origenId"),
        (Integer) body.get("destinoId"),
        (Integer) body.get("usuarioId"),
        (String) body.get("zonaHoraria"));
    return ResponseEntity.ok(result);
  }

  @PostMapping("/updatePalletData")
  public ResponseEntity<Map<String, String>> updatePalletData(@RequestBody Map<String, Integer> body) {
    palletService.updatePalletData(
        body.get("palletId"),
        body.get("codigoSapId"),
        body.get("tipologiaId"),
        body.get("posicionId"),
        body.get("loteId"),
        body.get("usuarioId"));
    Map<String, String> response = new HashMap<>();
    response.put("message", "Pallet actualizado correctamente");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getPalletNumero")
  public ResponseEntity<Boolean> getPalletNumero(@RequestParam String palletNumero) {
    try {
      Boolean result = palletService.getPalletNumero(palletNumero);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }
}
