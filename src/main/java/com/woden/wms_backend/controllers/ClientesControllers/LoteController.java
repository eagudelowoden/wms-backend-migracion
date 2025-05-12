package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.LoteDTO;
import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.services.ClienteServices.LoteService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/client/lote")
public class LoteController extends BaseController<LoteModel, Integer> {
    @Autowired
    private LoteService loteService;

    public LoteController(LoteService loteService) {
        super(loteService);
    }

    @GetMapping("/getLotes")
    public ResponseEntity<List<LoteDTO>> getLotes() {
        List<LoteDTO> results = loteService.getLotes();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getIdByLote/{lote}")
    public Integer getIdByLote(@PathVariable String lote) {
        return loteService.getIdByLote(lote);
    }

    @GetMapping("/getBatchName/{id}")
    public String getBatchName(@PathVariable Integer id) {
        return loteService.getBatchName(id);
    }

}
