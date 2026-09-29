package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.InventarioModel;
import com.woden.wms_backend.services.ClienteServices.InventarioService;

@RestController
@RequestMapping("/client/inventario")
public class InventarioController extends BaseController<InventarioModel, Integer> {

  public InventarioController(InventarioService inventarioService) {
    super(inventarioService);
  }

  @Autowired
  private InventarioService inventarioService;

  @GetMapping("/getCountEntrySap")
  public ResponseEntity<Integer> getCountEntrySap(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(inventarioService.getCountEntrySap(codigoSapId));
  }

  @GetMapping("/getCountInventorySap")
  public ResponseEntity<Integer> getCountInventorySap(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(inventarioService.getCountInventorySap(codigoSapId));
  }

  @GetMapping("/getCountDiffSap")
  public ResponseEntity<Integer> getCountDiffSap(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(inventarioService.getCountDiffSap(codigoSapId));
  }

  @GetMapping("/getPercentDiffSap")
  public ResponseEntity<Double> getPercentDiffSap(@RequestParam Integer codigoSapId) {
    return ResponseEntity.ok(inventarioService.getPercentDiffSap(codigoSapId));
  }

  @GetMapping("/getCountEntry")
  public ResponseEntity<Integer> getCountEntry() {
    return ResponseEntity.ok(inventarioService.getCountEntry());
  }

  @GetMapping("/getCountInventory")
  public ResponseEntity<Integer> getCountInventory() {
    return ResponseEntity.ok(inventarioService.getCountInventory());
  }

  @GetMapping("/getCountDiff")
  public ResponseEntity<Integer> getCountDiff() {
    return ResponseEntity.ok(inventarioService.getCountDiff());
  }

  @GetMapping("/getPercentDiff")
  public ResponseEntity<Double> getPercentDiff() {
    return ResponseEntity.ok(inventarioService.getPercentDiff());
  }

  @DeleteMapping("/deleteSerialInventory")
  public ResponseEntity<Integer> deleteSerialInventory(@RequestParam String serial) {
    try {
      Integer count = inventarioService.deleteSerialInventory(serial);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(0);
    }
  }

  @GetMapping("/getCountSurplus")
  public ResponseEntity<Double> getCountSurplus() {
    return ResponseEntity.ok(inventarioService.getCountSurplus());
  }

  @GetMapping("/getModel")
  public ResponseEntity<InventarioModel> getModel(@RequestParam String serial) {
    try {
      InventarioModel model = inventarioService.getModel(serial);
      return ResponseEntity.ok(model);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @GetMapping("/getListStateInventory")
  public ResponseEntity<List<String>> getListStateInventory() {
    try {
      List<String> list = inventarioService.getListStateInventory();
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @GetMapping("/getModelFinishInventory")
  public ResponseEntity<List<InventarioModel>> getModelFinishInventory() {
    try {
      List<InventarioModel> list = inventarioService.getModelFinishInventory();
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @DeleteMapping("/cleanTableInventory")
  public ResponseEntity<Integer> cleanTable() {
    try {
      Integer count = inventarioService.cleanTable();
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(0);
    }
  }

  @GetMapping("/searchInventory")
  public ResponseEntity<List<InventarioModel>> searchInventory(@RequestParam String pallet) {
    try {
      List<InventarioModel> list = inventarioService.searchInventory(pallet);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @PostMapping("/createInventory")
  public ResponseEntity<Integer> create(@RequestBody InventarioModel inventario) {
    try {
      Integer count = inventarioService.create(inventario);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(0);
    }
  }

  @GetMapping("/searchSettings")
  public ResponseEntity<List<Map<String, Object>>> searchSettings(@RequestParam String palletReal) {
    try {
      List<Map<String, Object>> list = inventarioService.searchSettings(palletReal);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }
}