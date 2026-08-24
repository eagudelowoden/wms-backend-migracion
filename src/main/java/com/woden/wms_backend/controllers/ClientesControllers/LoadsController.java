package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.woden.wms_backend.dto.clientDTO.loads.UpdateNotAvailableRowDTO;
import com.woden.wms_backend.dto.response.ApiSuccess;
import com.woden.wms_backend.services.ClienteServices.LoadsService;

@RestController
@RequestMapping("/client/loads")
public class LoadsController {

  @Autowired
  private LoadsService loadsService;

  @DeleteMapping("/{base}")
  public ResponseEntity<ApiSuccess> deleteBase(@PathVariable String base) {
    loadsService.deleteBase(base);
    return ResponseEntity.ok(ApiSuccess.of("Tabla " + base + " limpiada correctamente."));
  }

  @GetMapping("/{base}/search")
  public ResponseEntity<?> searchBase(
      @PathVariable String base,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    if (page >= 0 && size > 0 && size <= 500) {
      Map<String, Object> result = loadsService.searchBasePaged(base, page, size);
      return ResponseEntity.ok(result);
    }
    List<Map<String, Object>> results = loadsService.searchBase(base);
    return ResponseEntity.ok(results);
  }

  @GetMapping("/{base}/count")
  public ResponseEntity<Map<String, Object>> countBase(@PathVariable String base) {
    int total = loadsService.countBase(base);
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("total", total);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/upload")
  public ResponseEntity<?> upload(
      @RequestParam String base,
      @RequestParam("file") MultipartFile file) {
    Map<String, Object> result = loadsService.uploadFile(base, file);
    if (Boolean.TRUE.equals(result.get("ok"))) {
      return ResponseEntity.ok(ApiSuccess.of(result.get("registros") + " registro(s) insertado(s)."));
    }
    return ResponseEntity.unprocessableEntity().body(result);
  }

  @PostMapping("/update-not-available")
  public ResponseEntity<?> updateNotAvailable(@RequestBody List<UpdateNotAvailableRowDTO> rows) {
    Map<String, Object> result = loadsService.updateNotAvailable(rows);
    if (Boolean.TRUE.equals(result.get("ok"))) {
      return ResponseEntity.ok(ApiSuccess.of(result.get("procesados") + " registro(s) actualizado(s)."));
    }
    return ResponseEntity.unprocessableEntity().body(result);
  }
}
