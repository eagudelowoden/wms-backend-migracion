package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.models.Entity.EtiquetaModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetaService;

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
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping("/crearDirectorio")
  public ResponseEntity<Boolean> crearDirectorio(@RequestBody Map<String, String> body) {
    boolean ok = etiquetaService.crearDirectorioPrn(
        body.get("nombre"), body.get("tipo"));
    return ResponseEntity.ok(ok);
  }

  @PostMapping("/uploadPrn")
  public ResponseEntity<Boolean> uploadPrn(
      @RequestParam String nombre,
      @RequestParam String tipo,
      @RequestParam int impresion,
      @RequestParam("file") MultipartFile file) {
    boolean ok = etiquetaService.uploadPrn(nombre, tipo, impresion, file);
    return ResponseEntity.ok(ok);
  }

  @GetMapping("/listPrnFiles")
  public ResponseEntity<List<String>> listPrnFiles(
      @RequestParam String nombre,
      @RequestParam String tipo) {
    return ResponseEntity.ok(etiquetaService.listPrnFiles(nombre, tipo));
  }

  @DeleteMapping("/deletePrnFile")
  public ResponseEntity<Boolean> deletePrnFile(
      @RequestParam String nombre,
      @RequestParam String tipo,
      @RequestParam String archivo) {
    boolean ok = etiquetaService.deletePrnFile(nombre, tipo, archivo);
    return ResponseEntity.ok(ok);
  }

  @PostMapping("/previewPrn")
  public ResponseEntity<String> previewPrn(@RequestBody Map<String, String> body) {
    String base64 = etiquetaService.previewPrn(
        body.get("nombre"), body.get("tipo"), body.get("archivo"));
    return ResponseEntity.ok(base64);
  }
}