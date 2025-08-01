package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.PalletReportDTO;
import com.woden.wms_backend.services.ClienteServices.JasperReportService;

import net.sf.jasperreports.engine.JRException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import org.slf4j.Logger;

@RestController
@RequestMapping("/client/reports")
public class JasperReportController {
  @Autowired
  private JasperReportService jasperReportService;

  public JasperReportController(JasperReportService jasperReportService) {
    this.jasperReportService = jasperReportService;
  }

  private static final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

  @PostMapping("/reporte")
  public ResponseEntity<byte[]> generarReportePallet(@RequestBody PalletReportDTO request) throws IOException, JRException {
    try {
      byte[] report = jasperReportService.generarReporte("Pallet", request);
      HttpHeaders headers = new HttpHeaders();
      String fileName = request.getPallet() + ".pdf";
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-Disposition", "inline; filename=" + fileName);
      return new ResponseEntity<>(report, headers, HttpStatus.OK);
    } catch (JRException e) {
      logger.error("Error al generar el reporte: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
