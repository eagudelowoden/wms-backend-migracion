package com.woden.wms_backend.controllers.ClientesControllers;

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

import com.woden.wms_backend.dto.clientDTO.BoletaMovimientoDTO;
import com.woden.wms_backend.services.ClienteServices.BoletaMovimientoService;

@RestController
@RequestMapping("/client/reports")
public class BoletaMovimientoController {

  private static final Logger logger = LoggerFactory.getLogger(BoletaMovimientoController.class);

  @Autowired
  private BoletaMovimientoService boletaMovimientoService;

  @PostMapping("/boleta-movimiento")
  public ResponseEntity<byte[]> generarBoletaMovimiento(@RequestBody BoletaMovimientoDTO request) {
    try {
      byte[] pdf = boletaMovimientoService.generarBoleta(request);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-Disposition", "inline; filename=boleta-movimiento.pdf");
      return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("[BoletaMovimiento] Error generando boleta: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
