package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import com.woden.wms_backend.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.woden.wms_backend.dto.clientDTO.pallet.InactivatePalletRequest;
import com.woden.wms_backend.dto.clientDTO.pallet.ReservePalletRequest;
import com.woden.wms_backend.dto.clientDTO.pallet.UpdatePalletDataRequest;
import com.woden.wms_backend.dto.clientDTO.pallet.UpdatePositionRequest;
import com.woden.wms_backend.dto.clientDTO.pallet.UpdateTipologiaPalletRequest;
import com.woden.wms_backend.dto.response.ApiSuccess;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.exception.EntryNotFoundException;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.ClienteServices.PalletService;
import com.woden.wms_backend.services.ClienteServices.IngresoService;

@RestController
@RequestMapping("/client/pallets")
public class PalletController extends BaseController<PalletModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(PalletController.class);

  @Autowired
  private PalletService palletService;
  @Autowired
  private IngresoService ingresoService;

  public PalletController(PalletService service) {
    super(service);
  }

  // ── Creación ──────────────────────────────────────────────────────────────────

  @PostMapping("/create/{kitEntryOn}")
  public ResponseEntity<Map<String, String>> createPallet(
      @RequestBody PalletModel pallet,
      @PathVariable Boolean kitEntryOn) {
    String numeroPallet = palletService.createPallet(pallet, kitEntryOn);
    return ResponseEntity.ok(Map.of("message", "Pallet creado exitosamente.", "numero", numeroPallet));
  }

  @PostMapping("/reservePallet")
  public ResponseEntity<Map<String, Object>> reservePallet(@RequestBody ReservePalletRequest request) {
    Map<String, Object> result = palletService.reservePallet(
        request.origenId(), request.destinoId(), request.usuarioId(), request.zonaHoraria());
    return ResponseEntity.ok(result);
  }

  // ── Consulta ──────────────────────────────────────────────────────────────────

  @GetMapping("/getModel/{id}")
  public ResponseEntity<PalletModel> getPalletById(@PathVariable int id) {
    PalletModel pallet = palletService.getModel(id);
    if (pallet == null) {
      throw new EntryNotFoundException("Pallet no encontrado con id: " + id);
    }
    return ResponseEntity.ok(pallet);
  }

  @GetMapping("/transito")
  public ResponseEntity<List<Map<String, Object>>> searchTransitPallet(
      @RequestParam(required = false, defaultValue = "") String numero) {
    return ResponseEntity.ok(palletService.searchTransitPallet(numero));
  }

  @GetMapping("/search")
  public ResponseEntity<List<PalletDTO>> searchEntry(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam int usuarioId) {
    return ResponseEntity.ok(palletService.searchEntry(numero, destino, usuarioId));
  }

  @GetMapping("/searchAccesory")
  public ResponseEntity<List<PalletDTO>> searchAccesory(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam int usuarioId) {
    return ResponseEntity.ok(palletService.searchAccesory(numero, destino, usuarioId));
  }

  @PostMapping("/cantidad-seriales")
  public ResponseEntity<Integer> getCount(@RequestBody CountPalletDTO dto) {
    return ResponseEntity.ok(palletService.getCount(dto.getPalletId(), dto.getTabla()));
  }

  @GetMapping("/getListPallets")
  public ResponseEntity<List<String>> getListPallets(@RequestParam String destino) {
    return ResponseEntity.ok(palletService.getListPallets(destino));
  }

  @GetMapping("/getIdPallet")
  public ResponseEntity<Integer> getIdPallet(@RequestParam String numero) {
    Integer idPallet = palletService.getIdPallet(numero);
    if (idPallet == null) {
      throw new EntryNotFoundException("Pallet no encontrado con número: " + numero);
    }
    return ResponseEntity.ok(idPallet);
  }

  @GetMapping("/getBoxPallet")
  public ResponseEntity<Integer> getBoxPallet(@RequestParam Integer palletId, @RequestParam String tabla) {
    return ResponseEntity.ok(palletService.getBoxPallet(palletId, tabla));
  }

  @GetMapping("/getPalletNumero")
  public ResponseEntity<Boolean> getPalletNumero(@RequestParam String palletNumero) {
    return ResponseEntity.ok(palletService.getPalletNumero(palletNumero));
  }

  // ── Envío / tránsito ──────────────────────────────────────────────────────────

  @PostMapping("/enviarPallet")
  public ResponseEntity<ApiSuccess> enviarPallet(@RequestBody EnviarPalletDTO dto) {
    palletService.enviarPallet(
        dto.getPalletId(), dto.getDestinoId(), dto.getEstadoId(),
        dto.getTipologiaId(), dto.getPosicionId(),
        dto.getUsuarioId(), dto.getOpcion(), dto.getEstado());
    return ResponseEntity.ok(ApiSuccess.of("Pallet enviado correctamente."));
  }

  @PostMapping("/sendPallet")
  public ResponseEntity<ApiSuccess> sendPallet(@RequestBody SendPalletDTO dto) {
    palletService.sendPallet(dto);
    return ResponseEntity.ok(ApiSuccess.of("Pallet enviado correctamente."));
  }

  @PostMapping("/sendAllPallet")
  public ResponseEntity<ApiSuccess> sendAllPallet(
      @RequestParam Integer destinoId,
      @RequestParam Integer palletId) {
    palletService.sendAllPallet(destinoId, palletId);
    return ResponseEntity.ok(ApiSuccess.of("Pallet enviado correctamente."));
  }

  @PostMapping("/confirmar-transito")
  public ResponseEntity<ApiSuccess> confirmarPallet(@RequestBody ConfirmarPalletDTO dto) {
    palletService.confirmarPalletTransito(dto);
    return ResponseEntity.ok(ApiSuccess.of("Pallet confirmado correctamente."));
  }

  @PostMapping("/abrir-transito")
  public ResponseEntity<ApiSuccess> abrirPallet(@RequestBody AbrirPalletDTO dto) {
    palletService.abrirPalletTransito(dto);
    return ResponseEntity.ok(ApiSuccess.of("Pallet abierto correctamente."));
  }

  // ── Eliminación ───────────────────────────────────────────────────────────────

  @DeleteMapping("/eliminar/{palletId}")
  public ResponseEntity<ApiSuccess> eliminarPallet(
      @PathVariable Integer palletId,
      @RequestParam(required = false, defaultValue = "") String tipoEquipo,
      @RequestParam(required = false, defaultValue = "") String tipo) {
    boolean eliminado = palletService.deletePallet1(palletId, tipoEquipo, tipo);
    if (!eliminado) {
      throw new BusinessRuleException("No se puede eliminar: el pallet tiene cajas o ingresos asociados.");
    }
    return ResponseEntity.ok(ApiSuccess.of("Pallet eliminado correctamente."));
  }

  @DeleteMapping("/deletePallet/{palletId}")
  public ResponseEntity<ApiSuccess> deletePallet(@PathVariable Integer palletId) {
    palletService.deletePallet(palletId);
    return ResponseEntity.ok(ApiSuccess.of("Pallet eliminado correctamente."));
  }

  @DeleteMapping("/deleteAccesory/{palletId}/{cantidad}/{codigoSapId}")
  public ResponseEntity<ApiSuccess> eliminarAccesorio(
      @PathVariable Integer palletId,
      @PathVariable Integer cantidad,
      @PathVariable Integer codigoSapId,
      @RequestParam Integer usuarioId) {
    boolean eliminado = palletService.eliminarAccesorio(palletId, cantidad, codigoSapId, usuarioId);
    if (!eliminado) {
      throw new EntryNotFoundException("No se encontró el accesorio para eliminar.");
    }
    return ResponseEntity.ok(ApiSuccess.of("Accesorio eliminado correctamente."));
  }

  // ── Actualización ─────────────────────────────────────────────────────────────

  @PostMapping("/updatePalletData")
  public ResponseEntity<ApiSuccess> updatePalletData(@RequestBody UpdatePalletDataRequest request) {
    palletService.updatePalletData(
        request.palletId(), request.codigoSapId(), request.tipologiaId(),
        request.posicionId(), request.loteId(), request.usuarioId());
    return ResponseEntity.ok(ApiSuccess.of("Pallet actualizado correctamente."));
  }

  @PostMapping("/updatePosition")
  public ResponseEntity<ApiSuccess> updatePosition(@RequestBody UpdatePositionRequest request) {
    palletService.updatePosition(request.palletId(), request.posicionId());
    return ResponseEntity.ok(ApiSuccess.of("Posición actualizada correctamente."));
  }

  @PostMapping("/updateTipologia")
  public ResponseEntity<ApiSuccess> updateTipologia(@RequestBody UpdateTipologiaPalletRequest request) {
    palletService.updateTipologia(request.palletId(), request.tipologiaId());
    return ResponseEntity.ok(ApiSuccess.of("Tipología actualizada correctamente."));
  }

  @PostMapping("/updateSapCodePallet")
  public ResponseEntity<Integer> updateSapCodePallet(
      @RequestParam Integer palletId,
      @RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(palletService.updateSapCodePallet(palletId, codigoSapId, 0));
  }

  @PostMapping("/regularizacion/lote-pallet")
  public ResponseEntity<ApiSuccess> regularizarLotePallet(@RequestBody RegularizarLotePalletDTO dto) {
    palletService.regularizarLotePallet(dto.getPalletId(), dto.getLoteId(), dto.getUsuarioIdMovimiento());
    return ResponseEntity.ok(ApiSuccess.of("Lote del pallet actualizado correctamente."));
  }

  @PostMapping("/unifyPallets")
  public ResponseEntity<ApiSuccess> unifyPallet(@RequestBody List<Integer> palletIds) {
    palletService.unifyPallet(palletIds);
    return ResponseEntity.ok(ApiSuccess.of("Pallets unificados correctamente."));
  }

  // ── Inactivación ─────────────────────────────────────────────────────────────

  @PostMapping("/inactivate")
  public ResponseEntity<ApiSuccess> inactivate(@RequestBody InactivatePalletRequest request) {
    palletService.innactivatePallet(request.palletId());
    return ResponseEntity.ok(ApiSuccess.of("Pallet inactivado correctamente."));
  }

  @PostMapping("/innactivatePallet")
  public ResponseEntity<ApiSuccess> innactivatePallet(@RequestParam Integer palletId) {
    palletService.innactivatePallet(palletId);
    return ResponseEntity.ok(ApiSuccess.of("Pallet inactivado correctamente."));
  }

  @PostMapping("/innactivatePalletInventory")
  public ResponseEntity<Integer> innactivatePalletInventory(@RequestParam Integer palletId) {
    return ResponseEntity.ok(palletService.innactivatePalletInventory(palletId));
  }

  @PostMapping("/updateStateInventoryPallet")
  public ResponseEntity<ApiSuccess> updateStateInventoryPallet(
      @RequestParam Integer palletId,
      @RequestParam Integer estadoInventario) {
    palletService.updateStateInventoryPallet(palletId, estadoInventario);
    return ResponseEntity.ok(ApiSuccess.of("Estado de inventario actualizado correctamente."));
  }

  // ── Búsqueda por tipo ─────────────────────────────────────────────────────────

  @GetMapping("/search-receive")
  public ResponseEntity<List<PalletDTO>> searchReceivePallet(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam String tipo) {
    return ResponseEntity.ok(palletService.searchReceivePallet(numero, destino, tipo));
  }

  @GetMapping("/searchEntrega")
  public ResponseEntity<List<PalletDTO>> searchPackingDeliveryPallet(
      @RequestParam String numero,
      @RequestParam String tipologia,
      @RequestParam String tipo) {
    return ResponseEntity.ok(palletService.searchPackingDeliveryPallet(numero, tipologia, tipo));
  }

  @GetMapping("/searchPalletsBoxes")
  public ResponseEntity<List<Map<String, String>>> searchPalletBoxPallet(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    return ResponseEntity.ok(palletService.SearchPalletBoxPallet(numero, destino, page, size));
  }

  @GetMapping("/searchPalletsBoxesAll")
  public ResponseEntity<List<Map<String, String>>> searchPalletsBoxesAll(
      @RequestParam String numero,
      @RequestParam String destino) {
    return ResponseEntity.ok(palletService.searchPalletsBoxesAll(numero, destino));
  }

  @GetMapping("/searchPalletsBoxesWeb")
  public ResponseEntity<List<Map<String, String>>> searchPalletBoxPalletWeb(
      @RequestParam String numero,
      @RequestParam String destino,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    return ResponseEntity.ok(palletService.SearchPalletBoxPalletWeb(numero, destino, page, size));
  }

  @GetMapping("/searchStoragePallet")
  public ResponseEntity<List<PalletStorageDTO>> searchStoragePallet(
      @RequestParam String numero,
      @RequestParam String tipo,
      @RequestParam String tipoAccesorio) {
    return ResponseEntity.ok(palletService.searchStoragePallet(numero, tipo, tipoAccesorio));
  }

  @GetMapping("/searchStorageGroupPalletAccesory")
  public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPalletAccesory(
      @RequestParam String numero,
      @RequestParam String tipoAccesorio) {
    return ResponseEntity.ok(palletService.searchStorageGroupPalletAccesory(numero, tipoAccesorio));
  }

  @GetMapping("/searchStorageGroupPalletAccesoryCreated")
  public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPalletAccesoryCreated(
      @RequestParam String numero,
      @RequestParam String tipoAccesorio) {
    return ResponseEntity.ok(palletService.searchStorageGroupPalletAccesoryCreated(numero, tipoAccesorio));
  }

  @GetMapping("/searchStorageGroupPallet")
  public ResponseEntity<List<PalletStorageDTO>> searchStorageGroupPallet(
      @RequestParam String numero,
      @RequestParam String tipoEquipo) {
    return ResponseEntity.ok(palletService.searchStorageGroupPallet(numero, tipoEquipo));
  }

  @GetMapping("/searchQualityDeliveryPallet")
  public ResponseEntity<List<Map<String, String>>> searchQualityDeliveryPallet() {
    return ResponseEntity.ok(palletService.searchQualityDeliveryPallet());
  }

  @GetMapping("/searchPalletBoxDispatchPallet")
  public ResponseEntity<List<Map<String, String>>> searchPalletBoxDispatchPallet(@RequestParam String numero) {
    return ResponseEntity.ok(palletService.searchPalletBoxDispatchPallet(numero));
  }

  // ── Inventario ────────────────────────────────────────────────────────────────

  @GetMapping("/getPalletsInventory")
  public ResponseEntity<List<Map<String, Object>>> getPalletsInventory(@RequestParam Integer usuarioId) {
    return ResponseEntity.ok(palletService.getPalletsInventory(usuarioId));
  }

  @GetMapping("/searchReceivePartsPallet")
  public ResponseEntity<List<Map<String, Object>>> searchReceivePartsPallet(
      @RequestParam String destino,
      @RequestParam String numero) {
    return ResponseEntity.ok(palletService.searchReceivePartsPallet(destino, numero));
  }

  @GetMapping("/searchGeneralInventoryPallet")
  public ResponseEntity<List<Map<String, Object>>> searchGeneralInventoryPallet(
      @RequestParam String numero,
      @RequestParam String estado,
      @RequestParam Integer usuarioId) {
    return ResponseEntity.ok(palletService.searchGeneralInventoryPallet(numero, estado, usuarioId));
  }

  @GetMapping("/searchGeneralSettingsPallet")
  public ResponseEntity<List<Map<String, Object>>> searchGeneralSettingsPallet(
      @RequestParam String numero,
      @RequestParam String estado) {
    return ResponseEntity.ok(palletService.searchGeneralSettingsPallet(numero, estado));
  }

  @PostMapping("/cerrar")
  public ResponseEntity<Map<String, String>> cerrarPallet(@RequestBody CerrarIngresoDTO dto) {
    Map<String, String> response = new HashMap<>();
    try {
      palletService.cerrarPallet(
          dto.getPalletId(), dto.getDestinoId(), dto.getEstadoId(),
          dto.getTipologiaId(), dto.getPosicionId(),
          dto.getUsuarioId(), dto.getOpcion());

      response.put("message", "Pallet cerrado correctamente");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("message", "Error al cerrar el pallet: " + e.getMessage());
      return ResponseEntity.status(500).body(response);
    }
  }


  @GetMapping("/searchModels")
  public ResponseEntity<List<ModeloPalletDto>> searchModels(
          @RequestParam Integer palletId) {
    try {
      List<ModeloPalletDto> modelos = palletService.searchModels(palletId);
      return ResponseEntity.ok(modelos);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/sendModels")
  public ResponseEntity<Integer> sendModels(
          @RequestBody SendModelsDto dto) {
    try {
      int result = palletService.sendModels(dto);
        System.out.println(dto);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(dto);
      return ResponseEntity.badRequest().body(0);
    }
  }
}
