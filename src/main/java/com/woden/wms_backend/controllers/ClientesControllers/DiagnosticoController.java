package com.woden.wms_backend.controllers.ClientesControllers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.woden.wms_backend.dto.clientDTO.diagnostico.DiagnosticoRequest;
import com.woden.wms_backend.services.ClienteServices.DiagnosticoService;

@RestController
@RequestMapping("/client/diagnostico")
public class DiagnosticoController {
  private static final Logger logger = LoggerFactory.getLogger(DiagnosticoController.class);

  @Autowired
  private DiagnosticoService service;

  @GetMapping("/updateCodigoSapDiagnostico")
  public Integer updateSapCode(@RequestParam String serial) {
    return service.updateSapCode(serial);
  }

  @PostMapping("/createDiagnostico")
  public ResponseEntity<?> insertarDiagnostico(
      @RequestParam Integer serialId,
      @RequestParam String serial,
      @RequestParam String mac,
      @RequestParam Integer codigoSapId,
      @RequestParam Integer usuarioId,
      @RequestParam String variable1,
      @RequestParam String variable2,
      @RequestParam String variable3,
      @RequestParam String variable4) {
    try {
      service.create(serialId, serial, mac, codigoSapId, usuarioId, variable1, variable2, variable3, variable4);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      String errorMsg = e.getMessage();
      if (errorMsg != null && errorMsg.toLowerCase().contains("reporte34")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(1001);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(0);
    }
  }

  @PostMapping("/updateDiagnostico")
  public ResponseEntity<?> updateDiagnostico(@RequestBody List<Map<String, Object>> requestList) {
    String primerSerial = (!requestList.isEmpty() && requestList.get(0).get("serial") != null)
        ? requestList.get(0).get("serial").toString() : "N/A";
    logger.info("[DIAGNOSTICO-PROCESO] Recibida solicitud para actualizar diagnóstico de {} seriales, primer serial: {}", requestList.size(), primerSerial);
    try {
      service.updateDiagnosticos(requestList);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      logger.error("[DIAGNOSTICO-PROCESO] Error actualizando diagnósticos: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/getFailureIdDiagnostic")
  public ResponseEntity<Integer> getFailureIdDiagnostic(@RequestParam Integer serialId) {
    return ResponseEntity.ok(service.getFailureIdDiagnostic(serialId));
  }

  @GetMapping("/getFailureDiagnostic")
  public ResponseEntity<String> getFailureDiagnostic(@RequestParam String serial) {
    return ResponseEntity.ok(service.getFailureDiagnostic(serial));
  }

  @DeleteMapping("/deleteDiagnostico")
  public ResponseEntity<?> deleteDiagnostico(@RequestBody List<String> seriales) {
    try {
      for (String s : seriales) {
        service.delete(s);
      }
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @PostMapping("/save")
  public ResponseEntity<?> save(@RequestBody List<DiagnosticoRequest> items) {
    String primer = !items.isEmpty() ? items.get(0).getSerial() : "N/A";
    logger.info("[DIAGNOSTICO] save: {} seriales, primer serial: {}", items.size(), primer);
    try {
      service.save(items);
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      logger.error("[DIAGNOSTICO] Error en save: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @GetMapping("/getDiagnosedUser")
  public ResponseEntity<List<Map<String, Object>>> getDiagnosedUser(
      @RequestParam String perfil, @RequestParam Integer usuarioId) {
    return ResponseEntity.ok(service.getDiagnosedUser(perfil, usuarioId));
  }

  @GetMapping("/getDiagnosticVariables")
  public ResponseEntity<List<Map<String, Object>>> getDiagnosticVariables(@RequestParam String serial) {
    return ResponseEntity.ok(service.getDiagnosticVariables(serial));
  }

  @PostMapping("/uploadHojaVida")
  public ResponseEntity<?> uploadHojaVida(@RequestParam String serial,
      @RequestParam("file") MultipartFile file,
      @RequestParam Integer usuarioId,
      @RequestParam String modulo) {
    try {
      service.uploadHojaVida(serial, file, usuarioId, modulo);
      return ResponseEntity.ok(1);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      logger.error("[HOJA-VIDA] Error subiendo hoja de vida para serial {}: {}", serial, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo");
    }
  }

  @GetMapping("/hasHojaVida")
  public ResponseEntity<Boolean> hasHojaVida(@RequestParam String serial) {
    return ResponseEntity.ok(service.hasHojaVida(serial));
  }

  @GetMapping("/downloadPlantillaHojaVida")
  public ResponseEntity<?> downloadPlantillaHojaVida() {
    try {
      Path plantilla = service.getPlantillaHojaVida();
      if (plantilla == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No hay plantilla de hoja de vida disponible");
      }
      Resource resource = new FileSystemResource(plantilla);
      String nombre = plantilla.getFileName().toString();
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      logger.error("[HOJA-VIDA] Error descargando plantilla: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al descargar la plantilla");
    }
  }

  @DeleteMapping("/deleteHojaVida")
  public ResponseEntity<?> deleteHojaVida(@RequestParam String serial, @RequestParam Integer usuarioId) {
    try {
      service.deleteHojaVida(serial, usuarioId);
      return ResponseEntity.ok(1);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      logger.error("[HOJA-VIDA] Error eliminando hoja de vida para serial {}: {}", serial, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el archivo");
    }
  }
}
