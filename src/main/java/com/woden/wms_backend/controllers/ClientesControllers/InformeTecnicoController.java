package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.InformeTecnicoDTO;
import com.woden.wms_backend.services.ClienteServices.InformeTecnicoService;

/**
 * Informe Técnico PQRS (dictamen de Garantías/TruckRolls) — mismo patrón que
 * BoletaMovimientoController: /preview solo genera el PDF (para el iframe de
 * vista previa en el modal), /guardar además lo registra como Hoja de Vida
 * del serial (vía DiagnosticoService, mismo destino que la carga manual).
 */
@RestController
@RequestMapping("/client/reports/informe-tecnico")
public class InformeTecnicoController {

  private static final Logger logger = LoggerFactory.getLogger(InformeTecnicoController.class);

  @Autowired
  private InformeTecnicoService informeTecnicoService;

  @PostMapping("/preview")
  public ResponseEntity<byte[]> preview(@RequestBody InformeTecnicoDTO request) {
    try {
      byte[] pdf = informeTecnicoService.generarPdf(request);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-Disposition", "inline; filename=informe-tecnico-pqrs.pdf");
      return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("[InformeTecnico] Error generando vista previa: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/guardar")
  public ResponseEntity<?> guardar(@RequestBody InformeTecnicoDTO request) {
    try {
      informeTecnicoService.generarYGuardar(request);
      return ResponseEntity.ok(Map.of("message", "Informe técnico guardado correctamente.", "success", true));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
    } catch (Exception e) {
      logger.error("[InformeTecnico] Error guardando informe para serial {}: {}", request.getSerial(), e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("message", "Error al generar/guardar el informe.", "success", false));
    }
  }
}
