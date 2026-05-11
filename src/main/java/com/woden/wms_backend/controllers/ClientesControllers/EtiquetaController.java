package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetaService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/client/etiqueta")
public class EtiquetaController extends BaseController<EtiquetaModel, Integer> {

  public EtiquetaController(EtiquetaService service) {
    super(service);
  }

  @Autowired
  private EtiquetaService etiquetaService;

  @GetMapping("/getModelLabel")
  public EtiquetaModel getModelLabel(@RequestParam String nombre) {
    return etiquetaService.getModelLabel(nombre);
  }

  @GetMapping("/getListLabel")
  public List<EtiquetaListDTO> getListLabel(@RequestParam String tipo) {
    return etiquetaService.getListLabel(tipo);
  }

  @GetMapping("/searchLabeled")
  public ResponseEntity<List<Map<String, Object>>> searchLabeled(@RequestParam String nombre,
      @RequestParam String tipo) {
    return ResponseEntity.ok(etiquetaService.searchLabeled(nombre, tipo));
  }
}