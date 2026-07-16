package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.CodigoSapModelDTO;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.services.ClienteServices.CodigoSapService;

@RestController
@RequestMapping("/client/codigosap")
public class CodigoSapController {

    @Autowired
    private CodigoSapService codigoSapService;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAll() {
        return ResponseEntity.ok(codigoSapService.getListDescriptionSapCode());
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> getListDescriptionSapCode() {
        return ResponseEntity.ok(codigoSapService.getListDescriptionSapCode());
    }

    @GetMapping("/list/serial/{id}")
    public ResponseEntity<List<Map<String, String>>> getListDescriptionSapCodeSerial(@PathVariable int id) {
        return ResponseEntity.ok(codigoSapService.getListDescriptionSapCodeSerial(id));
    }

    @GetMapping("/list/noserial/{id}")
    public ResponseEntity<List<Map<String, String>>> getListDescriptionSapCodeNoSerial(@PathVariable int id) {
        return ResponseEntity.ok(codigoSapService.getListDescriptionSapCodeNoSerial(id));
    }

    @GetMapping("/id")
    public ResponseEntity<Map<String, Integer>> getIdByCodigoSap(@RequestParam String codigo) {
        int id = codigoSapService.getIdByCodigo(codigo);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/idSerial")
    public Integer getIdSerial(@RequestParam String tipo) {
        return codigoSapService.getIdSerial(tipo);
    }

    @GetMapping("/idNoSerial")
    public Integer getIdNoSerial(@RequestParam String tipo) {
        return codigoSapService.getIdNoSerial(tipo);
    }

    @GetMapping("/getIdComboPallet")
    public ResponseEntity<Map<String, Integer>> getIdComboPallet(
            @RequestParam String codigo,
            @RequestParam String descripcion) {
        Map<String, Integer> response = new HashMap<>();
        Integer id = codigoSapService.getIdComboPallet(codigo, descripcion);
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getModel")
    public ResponseEntity<CodigoSapModelDTO> obtenerCodigoSap(@RequestParam String codigo) {
        CodigoSapModelDTO codigoSap = codigoSapService.obtenerModeloPorCodigo(codigo);
        if (codigoSap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(codigoSap);
    }

    @GetMapping("/getFamilyId")
    public ResponseEntity<Integer> getFamilyId(@RequestParam String codigoSap) {
        return ResponseEntity.ok(codigoSapService.getFamilyId(codigoSap));
    }

    @GetMapping("/{id}/has-movements")
    public ResponseEntity<Boolean> hasMovements(@PathVariable Integer id) {
        Integer count = codigoSapService.getCount(id);
        return ResponseEntity.ok(count != null && count > 0);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(defaultValue = "") String codigo) {
        return ResponseEntity.ok(codigoSapService.search(codigo));
    }

    @GetMapping("/catalogos")
    public ResponseEntity<Map<String, List<MaestroModel>>> getCatalogos() {
        return ResponseEntity.ok(codigoSapService.getCatalogos());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CodigoSapModel model) {
        codigoSapService.create(model);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Código SAP creado.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @RequestBody CodigoSapModel model) {
        model.setId(id);
        Integer filas = codigoSapService.update(model);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Código SAP actualizado.");
        } else {
            response.put("message", "No se pudo actualizar.");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        Integer count = codigoSapService.getCount(id);
        Map<String, Object> response = new HashMap<>();
        if (count != null && count > 0) {
            response.put("hasMovements", true);
            response.put("message", "El código SAP tiene movimientos en el sistema.");
        } else {
            codigoSapService.delete(id);
            response.put("hasMovements", false);
            response.put("message", "Código SAP eliminado.");
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Integer id, @RequestParam Integer estado) {
        Integer filas = codigoSapService.innactivate(id, estado);
        Map<String, Object> response = new HashMap<>();
        if (filas != null && filas > 0) {
            response.put("message", "Estado actualizado.");
        } else {
            response.put("message", "No se pudo actualizar el estado.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accesorios/all")
    public ResponseEntity<List<Map<String, Object>>> getAllAccesorios() {
        return ResponseEntity.ok(codigoSapService.searchAllCodigos());
    }

    @GetMapping("/accesorios/asignados/{codigoSapId}")
    public ResponseEntity<List<Map<String, Object>>> getAccesoriosAsignados(@PathVariable Integer codigoSapId) {
        return ResponseEntity.ok(codigoSapService.searchAccesoriosAsignados(codigoSapId));
    }

    @PostMapping("/accesorios/asignar")
    public ResponseEntity<Map<String, Object>> asignarAccesorio(@RequestBody Map<String, Object> body) {
        Integer codigoSapId = Integer.valueOf(body.get("codigoSapId").toString());
        Integer accesorioId = Integer.valueOf(body.get("accesorioId").toString());
        codigoSapService.asignarAccesorio(codigoSapId, accesorioId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Accesorio asignado.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accesorios/remover")
    public ResponseEntity<Map<String, Object>> removerAccesorio(@RequestBody Map<String, Object> body) {
        Integer codigoSapId = Integer.valueOf(body.get("codigoSapId").toString());
        Integer accesorioId = Integer.valueOf(body.get("accesorioId").toString());
        codigoSapService.removerAccesorio(codigoSapId, accesorioId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Accesorio removido.");
        return ResponseEntity.ok(response);
    }
}
