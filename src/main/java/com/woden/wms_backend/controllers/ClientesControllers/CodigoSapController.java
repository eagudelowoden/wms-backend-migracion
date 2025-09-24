package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.CodigoSapModelDTO;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.services.ClienteServices.CodigoSapService;

@RestController
@RequestMapping("/client/codigosap")
public class CodigoSapController extends BaseController<CodigoSapModel, Integer> {

    public CodigoSapController(CodigoSapService service) {
        super(service);
    }

    @Autowired
    private CodigoSapService codigoSapService;

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

    @GetMapping("/id/{codigo}")
    public ResponseEntity<Map<String, Integer>> getIdByCodigoSap(@PathVariable String codigo) {
        int id = codigoSapService.getIdByCodigo(codigo);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/idSerial/{tipo}")
    public Integer getIdSerial(@PathVariable String tipo) {
        return codigoSapService.getIdSerial(tipo);
    }

    @GetMapping("/idNoSerial/{tipo}")
    public Integer getIdNoSerial(@PathVariable String tipo) {
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

    @GetMapping("/getModel/{codigo}")
    public ResponseEntity<?> obtenerCodigoSap(@PathVariable String codigo) {
        CodigoSapModelDTO model = codigoSapService.obtenerModeloPorCodigo(codigo);
        if (model == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Codigo Sap no encontrado");
        }
        Map<String, CodigoSapModelDTO> response = new HashMap<>();
        response.put("codigosap", model);
        return ResponseEntity.ok(response);
    }
}
