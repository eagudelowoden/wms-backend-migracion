package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.services.ClienteServices.CodigoSapService;

@RestController
@RequestMapping("/api/codigosap")
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
}
