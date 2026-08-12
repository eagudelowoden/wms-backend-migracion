package com.woden.wms_backend.controllers.ClientesControllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.woden.wms_backend.dto.clientDTO.EntryProgressDTO;
import com.woden.wms_backend.dto.clientDTO.SendIngresoDTO;
import com.woden.wms_backend.dto.clientDTO.SendCosmeticEntryDTO;
import com.woden.wms_backend.dto.clientDTO.SendStorageEntryDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.BackRepairedRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.ChangedPackingRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.ClassificationItemRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.DispatchEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.EntryDispatchRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.InsertSerialInventoryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.NoveltyAllEntryItemRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.PackingAllRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.PackingEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.PackingSmartCardRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.StateNoUserRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UnifyEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.MassUploadConfirmRequestDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.MassUploadConfirmResponseDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.MassUploadPreviewRequestDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.MassUploadPreviewResponseDTO;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateChangeStateRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateLevelRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateNoveltyItemRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdatePalletEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdatePalletTypologyRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateSmartCardRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateStateAllItemRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateStateEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateStateOneEntryRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateStatusBatchRequest;
import com.woden.wms_backend.dto.clientDTO.ingreso.UpdateTipologiaRequest;
import com.woden.wms_backend.dto.response.ApiSuccess;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.ZplService;

@RestController
@RequestMapping("/client/ingresos")
public class IngresoController extends BaseController<IngresoModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(IngresoController.class);

  @Autowired
  private IngresoService ingresoService;

  @Autowired
  private ZplService zplService;

  public IngresoController(IngresoService service) {
    super(service);
  }

  // ── Creación / eliminación ───────────────────────────────────────────────────

  @PostMapping("/create")
  public ResponseEntity<ApiSuccess> createEntry(@RequestBody IngresoModel ingreso) {
    String result = ingresoService.createIngresoCallable(ingreso);
    if (result != null) {
      throw new com.woden.wms_backend.exception.DuplicateEntryException(result);
    }
    return ResponseEntity.ok(ApiSuccess.of("Ingreso registrado correctamente."));
  }

  @DeleteMapping("/deleteEntries")
  public ResponseEntity<ApiSuccess> deleteEntries(@RequestBody List<String> seriales) {
    ingresoService.deleteEntries(seriales);
    return ResponseEntity.ok(ApiSuccess.of("Ingresos eliminados correctamente."));
  }

  // ── Búsqueda / consulta ──────────────────────────────────────────────────────

  @GetMapping("/searchEntryReingreso/{palletId}")
  public ResponseEntity<List<IngresoDTO>> searchEntryReingreso(@PathVariable Integer palletId) {
    return ResponseEntity.ok(ingresoService.searchEntryReingreso(palletId));
  }

  @GetMapping("/transito/{palletId}")
  public ResponseEntity<List<IngresoTransitoDTO>> getEntryTransitByPallet(@PathVariable Integer palletId) {
    return ResponseEntity.ok(ingresoService.getIngresoTransitByPalletId(palletId));
  }

  @GetMapping("/getEntryBySerial")
  public ResponseEntity<IngresoModel> getEntryBySerial(@RequestParam String serial) {
    return ResponseEntity.ok(ingresoService.getModelIngreso(serial));
  }

  @GetMapping("/getSerialByMac")
  public ResponseEntity<String> getSerialByMac(@RequestParam String mac) {
    return ResponseEntity.ok(ingresoService.getSerialByMac(mac));
  }

  @GetMapping("/getReingresos")
  public ResponseEntity<Integer> getReingresos(@RequestParam String serial) {
    return ResponseEntity.ok(ingresoService.getReingresos(serial));
  }

  @GetMapping("/getProactiveRepair")
  public ResponseEntity<Integer> getProactiveRepair(@RequestParam String serial) {
    return ResponseEntity.ok(ingresoService.getProactiveRepair(serial));
  }

  @GetMapping("/getProactiveRepairAll")
  public ResponseEntity<List<String>> getProactiveRepairAll() {
    return ResponseEntity.ok(ingresoService.getProactiveRepairAll());
  }

  @GetMapping("/getLevelEntry")
  public ResponseEntity<List<String>> getLevelEntry(@RequestParam String serial) {
    return ResponseEntity.ok(ingresoService.getLevelEntry(serial));
  }

  @GetMapping("/getListModel")
  public ResponseEntity<List<IngresoModel>> getListModel(@RequestParam List<String> seriales) {
    return ResponseEntity.ok(ingresoService.getListModel(seriales));
  }

  @GetMapping("/getModelDispatch")
  public ResponseEntity<List<IngresoModel>> getModelDispatch(
      @RequestParam Integer palletId, @RequestParam Integer cajaId) {
    return ResponseEntity.ok(ingresoService.getModelDispatch(palletId, cajaId));
  }

  @GetMapping("/searchQualityEntry")
  public ResponseEntity<List<Map<String, String>>> searchQualityEntry() {
    return ResponseEntity.ok(ingresoService.searchQualityEntry());
  }

  @GetMapping("/searchDiagnosticEntry")
  public ResponseEntity<List<Map<String, Object>>> searchDiagnosticEntry(
      @RequestParam String estadoFinal,
      @RequestParam String perfil,
      @RequestParam Integer usuarioId) {
    return ResponseEntity.ok(ingresoService.searchDiagnosticEntry(estadoFinal, perfil, usuarioId));
  }

  @GetMapping("/searchDelivery")
  public ResponseEntity<List<String>> searchDeliveryEntry(@RequestParam String serial) {
    List<String> result = ingresoService.searchDeliveryEntry(serial);
    return result.isEmpty()
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(result);
  }

  @GetMapping("/searchClasificationEntry")
  public ResponseEntity<List<Map<String, String>>> searchClasificationEntry() {
    return ResponseEntity.ok(ingresoService.searchClasificationEntry());
  }

  @GetMapping("/searchPalletBoxEntry")
  public ResponseEntity<List<Map<String, String>>> searchPalletBoxEntry(
      @RequestParam String estado,
      @RequestParam Integer palletId,
      @RequestParam Integer cajaId) {
    return ResponseEntity.ok(ingresoService.searchPalletBoxEntry(estado, palletId, cajaId));
  }

  @GetMapping("/searchRepairEntry")
  public ResponseEntity<List<Map<String, Object>>> searchRepairEntry(
      @RequestParam String estadoFinal,
      @RequestParam String perfil,
      @RequestParam Integer usuarioId) {
    return ResponseEntity.ok(ingresoService.searchRepairEntry(estadoFinal, perfil, usuarioId));
  }

  @GetMapping("/searchNoveltyEntry")
  public ResponseEntity<List<Map<String, Object>>> searchNoveltyEntry(@RequestParam String tipoNovedad) {
    return ResponseEntity.ok(ingresoService.searchNoveltyEntry(tipoNovedad));
  }

  @GetMapping("/searchNoveltyEntryDelivery")
  public ResponseEntity<List<Map<String, String>>> searchNoveltyEntryDelivery(
      @RequestParam String perfil,
      @RequestParam Integer usuarioId,
      @RequestParam String tipoNovedad) {
    return ResponseEntity.ok(ingresoService.searchNoveltyEntryDelivery(perfil, usuarioId, tipoNovedad));
  }

  @GetMapping("/getSerialsByPalletInventory")
  public ResponseEntity<List<Map<String, Object>>> getSerialsByPalletInventory(@RequestParam String pallet) {
    return ResponseEntity.ok(ingresoService.getSerialsByPalletInventory(pallet));
  }

  @GetMapping("/getValidaStateInventory")
  public ResponseEntity<String> getValidaStateInventory(
      @RequestParam String estado, @RequestParam String estadoInventario) {
    return ResponseEntity.ok(ingresoService.getValidaStateInventory(estado, estadoInventario));
  }

  @GetMapping("/getEntryProgress")
  public ResponseEntity<EntryProgressDTO> getEntryProgress(
      @RequestParam Integer palletId, @RequestParam Integer estadoId) {
    return ResponseEntity.ok(ingresoService.getEntryProgress(palletId, estadoId));
  }

  @GetMapping("/searchScrapEntry")
  public ResponseEntity<List<Map<String, Object>>> searchScrapEntry(@RequestParam String estado) {
    return ResponseEntity.ok(ingresoService.searchScrapEntry(estado));
  }

  @GetMapping("/getScrapUser")
  public ResponseEntity<List<Map<String, Object>>> getScrapUser(@RequestParam Integer usuarioIdMovimiento) {
    return ResponseEntity.ok(ingresoService.getScrapUser(usuarioIdMovimiento));
  }

  @GetMapping("/getEtiquetadoUser")
  public ResponseEntity<List<Map<String, Object>>> getEtiquetadoUser(@RequestParam Integer usuarioIdMovimiento) {
    return ResponseEntity.ok(ingresoService.getEtiquetadoUser(usuarioIdMovimiento));
  }

  // ── Regularización SAP / lote ─────────────────────────────────────────────

  @PostMapping("/regularizacion/sap")
  public ResponseEntity<ApiSuccess> updateSapRegularization(@RequestBody RegularizarSapDTO dto) {
    ingresoService.regularizarSap(dto.getSerial(), dto.getCodigoSapId(), dto.getUsuarioIdMovimiento());
    return ResponseEntity.ok(ApiSuccess.of("SAP actualizado correctamente."));
  }

  @PostMapping("/regularizacion/lote")
  public ResponseEntity<ApiSuccess> updateBatchRegularization(@RequestBody RegularizarLoteSerialDTO dto) {
    ingresoService.regularizarLoteSerial(dto.getSerial(), dto.getLoteId(), dto.getUsuarioIdMovimiento());
    return ResponseEntity.ok(ApiSuccess.of("Lote del serial actualizado correctamente."));
  }

  @PostMapping("/updateSapCodeEntry")
  public ResponseEntity<Integer> updateSapCodeEntry(@RequestBody RegularizarSapSerialIngresoDTO dto) {
    return ResponseEntity.ok(
        ingresoService.updateSapCode(dto.getCodigoSapId(), dto.getUsuarioIdMovimiento(), dto.getSerial()));
  }

  // ── Ingreso ilegible ──────────────────────────────────────────────────────

  @PostMapping("/generateIlegibleEntry")
  public ResponseEntity<Map<String, String>> generateIlegibleEntry(
      @RequestBody IngresoIlegibleDTO ingresoDTO,
      @RequestParam String cliente,
      @RequestParam Integer usuarioId) {
    String serial = ingresoService.generarIngresoIlegible(ingresoDTO, cliente, usuarioId);
    return ResponseEntity.ok(Map.of("serial", serial));
  }

  // ── Envío / almacenamiento ────────────────────────────────────────────────

  @PostMapping("/sendCosmeticEntry")
  public ResponseEntity<?> sendCosmeticEntry(@RequestBody SendCosmeticEntryDTO request) {
    try {
      ingresoService.sendCosmeticEntry(request.getPalletId(), request.getEstadoId(),
          request.getTipologiaId(), request.getUsuarioId());
      return ResponseEntity.ok(Map.of("message", "Pallet cosmético enviado correctamente.", "success", true));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("message", "Error al enviar pallet cosmético: " + e.getMessage(), "success", false));
    }
  }

  @PostMapping("/sendStorageEntry")
  public ResponseEntity<ApiSuccess> sendStorageEntry(@RequestBody SendStorageEntryDTO request) {
    ingresoService.sendStorageEntry(
        request.getPalletId(), request.getEstadoId(),
        request.getTipologiaId(), request.getUsuarioId());
    return ResponseEntity.ok(ApiSuccess.of("Pallet almacenado correctamente."));
  }

  @PostMapping("/sendEntry")
  public ResponseEntity<ApiSuccess> sendEntry(@RequestBody SendIngresoDTO dto) {
    ingresoService.sendEntry(dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
        dto.getPalletId(), dto.getOpcion());
    return ResponseEntity.ok(ApiSuccess.of("Ingreso enviado correctamente."));
  }

  @PostMapping("/sendNoveltyEntry")
  public ResponseEntity<ApiSuccess> sendNoveltyEntry(@RequestBody SendIngresoDTO dto) {
    ingresoService.sendNoveltyEntry(dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
        dto.getPalletId(), dto.getOpcion());
    return ResponseEntity.ok(ApiSuccess.of("Novedad enviada correctamente."));
  }

  // ── Actualización de tipología / pallet / nivel ───────────────────────────

  @PostMapping("/updateTipologiaEntry")
  public ResponseEntity<ApiSuccess> updateTipologiaEntry(@RequestBody UpdateTipologiaRequest request) {
    ingresoService.updateTipologia(request.palletId(), request.tipologiaId());
    return ResponseEntity.ok(ApiSuccess.of("Tipología actualizada correctamente."));
  }

  @PostMapping("/unifyEntry")
  public ResponseEntity<ApiSuccess> unifyEntry(@RequestBody UnifyEntryRequest request) {
    ingresoService.unifyEntry(request.palletIdDestino(), request.tipologiaId(),
        request.usuarioId(), request.palletIds());
    return ResponseEntity.ok(ApiSuccess.of("Ingreso unificado correctamente."));
  }

  @PostMapping("/updatePalletEntry")
  public ResponseEntity<ApiSuccess> updatePalletEntry(@RequestBody UpdatePalletEntryRequest request) {
    ingresoService.updatePalletEntry(request.palletId(), request.usuarioIdMovimiento(), request.seriales());
    return ResponseEntity.ok(ApiSuccess.of("Pallet actualizado correctamente."));
  }

  @PostMapping("/updatePalletAndTipologyEntry")
  public ResponseEntity<ApiSuccess> updatePalletAndTipologyEntry(@RequestBody UpdatePalletTypologyRequest request) {
    ingresoService.updatePalletAndTipologyEntry(request.palletId(), request.tipologiaId(),
        request.usuarioIdMovimiento(), request.estadoId(), request.seriales());
    return ResponseEntity.ok(ApiSuccess.of("Pallet y tipología actualizados correctamente."));
  }

  @PostMapping("/updateLevel")
  public ResponseEntity<ApiSuccess> updateLevel(@RequestBody UpdateLevelRequest request) {
    ingresoService.updateLevel(request.levelId(), request.palletId());
    return ResponseEntity.ok(ApiSuccess.of("Nivel actualizado correctamente."));
  }

  @PostMapping("/updateLevelWeb")
  public ResponseEntity<Map<String, Object>> updateLevelWeb(@RequestBody UpdateLevelRequest request) {
    int result = ingresoService.updateLevelWeb(request.levelId(), request.palletId());
    return ResponseEntity.ok(Map.of("message", result));
  }

  // ── Actualización de estado ───────────────────────────────────────────────

  @PostMapping("/updateStateAllEntry")
  public ResponseEntity<ApiSuccess> updateStateAllEntry(@RequestBody List<UpdateStateAllItemRequest> requestList) {
    String primerSerial = (!requestList.isEmpty() && requestList.get(0).serial() != null)
        ? requestList.get(0).serial() : "N/A";
    logger.info("[updateStateAllEntry] Recibida solicitud para actualizar {} seriales, primer serial: {}", requestList.size(), primerSerial);
    ingresoService.updateStateAllEntries(requestList);
    return ResponseEntity.ok(ApiSuccess.of(requestList.size() + " serial(es) actualizado(s)."));
  }

  @PostMapping("/updateStateEntry")
  public ResponseEntity<ApiSuccess> updateStateEntry(@RequestBody UpdateStateEntryRequest request) {
    request.seriales().forEach(serial ->
        ingresoService.updateStateEntry(
            request.estadoId(), request.palletId(), request.usuarioId(), request.fecha(), serial));
    return ResponseEntity.ok(ApiSuccess.of("Estado actualizado correctamente."));
  }

  @PostMapping("/updateChangeStateEntry")
  public ResponseEntity<ApiSuccess> updateChangeStateEntry(@RequestBody UpdateChangeStateRequest request) {
    ingresoService.updateChangedEntry(request.serial1(), request.serial2(), request.mac(), request.estadoId());
    return ResponseEntity.ok(ApiSuccess.of("Estado cambiado correctamente."));
  }

  @PostMapping("/updateClasificationEntry")
  public ResponseEntity<ApiSuccess> updateClasificationEntry(@RequestBody List<ClassificationItemRequest> items) {
    items.forEach(item ->
        ingresoService.updateClasificationEntry(
            item.serial(), item.estadoId(), item.nivelId(), item.usuarioIdMovimiento()));
    return ResponseEntity.ok(ApiSuccess.of("Clasificación actualizada correctamente."));
  }

  @PostMapping("/updateStateOneEntry")
  public ResponseEntity<ApiSuccess> updateStateOneEntry(@RequestBody UpdateStateOneEntryRequest request)
      throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    Date fecha = dateFormat.parse(request.fecha());
    ingresoService.updateStateOneEntry(
        request.estadoId(), request.nivelId(), request.usuarioIdMovimiento(), fecha, request.serial());
    return ResponseEntity.ok(ApiSuccess.of("Estado del ingreso actualizado correctamente."));
  }

  @PostMapping("/updateStateEntryNotUsuario")
  public ResponseEntity<ApiSuccess> updateStateEntryNotUsuario(@RequestBody StateNoUserRequest request) {
    request.serial().forEach(s ->
        ingresoService.updateStateEntryNotUsuario(request.estadoId(), request.palletId(), request.fecha(), s));
    return ResponseEntity.ok(ApiSuccess.of("Estado actualizado correctamente."));
  }

  @PostMapping("/updateStateEntryNotUsuarioRepaired")
  public ResponseEntity<ApiSuccess> updateStateEntryNotUsuarioRepaired(@RequestBody StateNoUserRequest request) {
    request.serial().forEach(s ->
        ingresoService.updateStateEntryNotUsuarioRepaired(request.estadoId(), request.palletId(), request.fecha(), s));
    return ResponseEntity.ok(ApiSuccess.of("Estado de reparación actualizado correctamente."));
  }

  @PostMapping("/updateStateEntryNotUsuarioDiagnosed")
  public ResponseEntity<ApiSuccess> updateStateEntryNotUsuarioDiagnosed(@RequestBody StateNoUserRequest request) {
    request.serial().forEach(s ->
        ingresoService.updateStateEntryNotUsuarioDiagnosed(request.estadoId(), request.palletId(), request.fecha(), s));
    return ResponseEntity.ok(ApiSuccess.of("Estado de diagnóstico actualizado correctamente."));
  }

  // ── Packing ───────────────────────────────────────────────────────────────

  @PostMapping("/updatePackingEntry")
  public ResponseEntity<ApiSuccess> updatePackingEntry(@RequestBody PackingEntryRequest request) {
    ingresoService.updatePackingEntry(
        request.estadoId(), request.palletId(), request.cajaEmpaqueId(),
        request.usuarioIdMovimiento(), request.serial(), request.loteId());
    return ResponseEntity.ok(ApiSuccess.of("Empaque actualizado correctamente."));
  }

  @PostMapping("/updatePackingEntrySmartCard")
  public ResponseEntity<ApiSuccess> updatePackingEntrySmartCard(@RequestBody PackingSmartCardRequest request) {
    ingresoService.updatePackingEntrySmartCard(
        request.estadoId(), request.palletId(), request.cajaEmpaqueId(),
        request.usuarioId(), request.serial(), request.loteId(),
        request.smartCardId(), request.smartCard());
    return ResponseEntity.ok(ApiSuccess.of("Empaque con SmartCard actualizado correctamente."));
  }

  @PostMapping("/updatePackingAllEntry")
  public ResponseEntity<ApiSuccess> updatePackingAllEntry(@RequestBody PackingAllRequest request) {
    ingresoService.updatePackingAllEntry(request.estadoId(), request.usuarioIdMovimiento(), request.seriales());
    return ResponseEntity.ok(ApiSuccess.of("Empaque masivo actualizado correctamente."));
  }

  @PostMapping("/updateChangedPackingEntry")
  public ResponseEntity<ApiSuccess> updateChangedPackingEntry(@RequestBody ChangedPackingRequest request) {
    ingresoService.updateChangedPackingEntry(
        request.estadoId(), request.palletId(), request.cajaEmpaqueId(),
        request.serial(), request.usuarioIdMovimiento(), request.tipologiaId());
    return ResponseEntity.ok(ApiSuccess.of("Cambio de empaque actualizado correctamente."));
  }

  @PostMapping("/updateSmartCardEntry")
  public ResponseEntity<ApiSuccess> updateSmartCardEntry(@RequestBody UpdateSmartCardRequest request) {
    ingresoService.updateSmartCardEntry(request.smartCardId(), request.smartCardNuevo(), request.serial());
    return ResponseEntity.ok(ApiSuccess.of("SmartCard actualizada correctamente."));
  }

  @DeleteMapping("/packOffPalletEntry")
  public ResponseEntity<ApiSuccess> packOffPalletEntry(
      @RequestParam Integer palletId, @RequestParam Integer usuarioId) {
    ingresoService.packOffPalletEntry(palletId, usuarioId);
    return ResponseEntity.ok(ApiSuccess.of("Pallet desempacado correctamente."));
  }

  @PostMapping("/getModelDispatchBox")
  public ResponseEntity<List<IngresoModel>> getModelDispatchBox(
      @RequestParam Integer palletId, @RequestBody List<Integer> cajasIds) {
    return ResponseEntity.ok(ingresoService.getModelDispatchBox(palletId, cajasIds));
  }

  @PutMapping("/packOffBoxEntry")
  public ResponseEntity<ApiSuccess> packOffBoxEntry(
      @RequestParam Integer palletId,
      @RequestBody List<Integer> cajasIds,
      @RequestParam Integer usuarioId) {
    cajasIds.forEach(cajaId -> ingresoService.packOffBoxEntry(palletId, cajaId, usuarioId));
    return ResponseEntity.ok(ApiSuccess.of("Cajas desempacadas correctamente."));
  }

  // ── Novedades ─────────────────────────────────────────────────────────────

  @PutMapping("/updateNoveltyAllEntry")
  public ResponseEntity<ApiSuccess> updateNoveltyAllEntry(
      @RequestBody List<NoveltyAllEntryItemRequest> items,
      @RequestParam Integer estadoId,
      @RequestParam Integer tipologiaId,
      @RequestParam Integer usuarioId,
      @RequestParam String novedad) {
    items.forEach(item ->
        ingresoService.updateNoveltyAllEntry(
            item.serial(), estadoId, tipologiaId, item.observaciones(),
            novedad, usuarioId, item.fallaCosmeticaId(), item.fallaFuncionalId()));
    return ResponseEntity.ok(ApiSuccess.of(items.size() + " novedad(es) actualizada(s)."));
  }

  @PutMapping("/updateNoveltyEntry")
  public ResponseEntity<Integer> updateNoveltyEntry(@RequestBody List<UpdateNoveltyItemRequest> items) {
    int updated = 0;
    for (UpdateNoveltyItemRequest item : items) {
      updated += ingresoService.updateNoveltyEntry(
          item.id(), item.serial(), item.mac(), item.codigoSapId(), item.guia(),
          item.documento(), item.tipoOrigenId(), item.origenId(), item.tipologiaId(),
          item.estadoId(), item.tipoNovedad(), item.usuarioId());
    }
    return ResponseEntity.ok(updated);
  }

  // ── Despacho ──────────────────────────────────────────────────────────────

  @PostMapping("/updateDispatchEntry")
  public ResponseEntity<ApiSuccess> updateDispatchEntry(@RequestBody DispatchEntryRequest request) {
    ingresoService.updateDispatchEntry(
        request.serial(), request.estadoId(), request.palletId(),
        request.cajaDespachoId(), request.usuarioMovimientoId(), request.loteId());
    return ResponseEntity.ok(ApiSuccess.of("Despacho actualizado correctamente."));
  }

  @PostMapping("/updateEntryDispatch")
  public ResponseEntity<ApiSuccess> updateEntryDispatch(@RequestBody EntryDispatchRequest request) {
    ingresoService.updateEntryDispatch(
        request.estadoId(), request.palletId(), request.usuarioIdMovimiento(),
        request.serial(), request.loteId());
    return ResponseEntity.ok(ApiSuccess.of("Estado de despacho actualizado correctamente."));
  }

  // ── Reparación ────────────────────────────────────────────────────────────

  @PostMapping("/backRepairedEntry")
  public ResponseEntity<ApiSuccess> backRepairedEntry(@RequestBody BackRepairedRequest request) {
    request.seriales().forEach(ingresoService::backRepairedEntry);
    return ResponseEntity.ok(ApiSuccess.of("Seriales devueltos a reparación correctamente."));
  }

  // ── Batch / estado masivo ─────────────────────────────────────────────────

  @PostMapping("/updateStatusBatch")
  public ResponseEntity<ApiSuccess> updateStatusBatch(@RequestBody UpdateStatusBatchRequest request) {
    ingresoService.updateStatusBatch(
        request.estadoId(), request.palletId(), request.usuarioIdMovimiento(),
        request.fecha(), request.seriales(), request.loteId());
    return ResponseEntity.ok(ApiSuccess.of("Estado lote actualizado correctamente."));
  }

  // ── Inventario ────────────────────────────────────────────────────────────

  @PostMapping("/insertSerialInventory")
  public ResponseEntity<ApiSuccess> insertSerialInventory(@RequestBody InsertSerialInventoryRequest request) {
    ingresoService.insertSerialInventory(
        request.serial(), request.codigoSap(), request.palletNumero(), request.usuarioId());
    return ResponseEntity.ok(ApiSuccess.of("Serial insertado en inventario correctamente."));
  }

  // ── Scrap ─────────────────────────────────────────────────────────────────

  @PostMapping("/updateScrapAll")
  public ResponseEntity<ApiSuccess> updateScrapAll(@RequestBody List<IngresoModel> seriales) {
    seriales.forEach(s ->
        ingresoService.updateScrapAll(s.getEstadoId(), s.getUsuarioIdMovimiento(), s.getSerial(), s.getNovedad()));
    return ResponseEntity.ok(ApiSuccess.of("Scrap actualizado correctamente."));
  }

  // ── Etiquetas ZPL (impresoras térmicas Zebra) ─────────────────────────────

  @PostMapping("/imprimirHabladores")
  public ResponseEntity<String> printLabels(@RequestBody List<IngresoModel> seriales) throws Exception {
    String zpl = zplService.generarZpl(seriales);
    return ResponseEntity.ok(zpl);
  }

  @GetMapping("/truckroll-status")
  public ResponseEntity<Map<String, Object>> getTruckRollStatus(@RequestParam String serial) {
    return ResponseEntity.ok(ingresoService.getTruckRollStatus(serial));
  }

  // ── Ingreso Masivo ────────────────────────────────────────────────────────

  @PostMapping("/mass-upload/preview")
  public ResponseEntity<MassUploadPreviewResponseDTO> massUploadPreview(
      @RequestBody MassUploadPreviewRequestDTO request) {
    return ResponseEntity.ok(ingresoService.massUploadPreview(request.getRows(), request.getPalletConfig()));
  }

  @PostMapping("/mass-upload/confirm")
  public ResponseEntity<MassUploadConfirmResponseDTO> massUploadConfirm(
      @RequestBody MassUploadConfirmRequestDTO request) {
    return ResponseEntity.ok(ingresoService.massUploadConfirm(request.getSeriales()));
  }
}
