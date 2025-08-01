package com.woden.wms_backend.services.ClienteServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.clientDTO.PalletReportDTO;

import net.sf.jasperreports.engine.*;
import java.awt.image.BufferedImage;

@Service
public class JasperReportService {

  public byte[] generarReporte(String reportName, PalletReportDTO palletReportDTO) throws JRException, IOException {
    try (InputStream reportStream = this.getClass().getResourceAsStream("/reports/Pallet.jasper")) {
      if (reportStream == null) {
        throw new JRException("Report template not found: /reports/Pallet.jasper");
      }

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("Cliente", palletReportDTO.getCliente());
      parameters.put("Fecha", palletReportDTO.getFecha());
      parameters.put("Pallet", palletReportDTO.getPallet());
      parameters.put("Origen", palletReportDTO.getOrigen());
      parameters.put("Destino", palletReportDTO.getDestino());
      parameters.put("Tipologia", palletReportDTO.getTipologia());
      parameters.put("CodigoSap", palletReportDTO.getCodigoSap());
      parameters.put("Modelo", palletReportDTO.getModelo());
      parameters.put("Usuario", palletReportDTO.getUsuario());
      parameters.put("Cantidad", palletReportDTO.getCantidad());
      parameters.put("Posicion", palletReportDTO.getPosicion());
      // Generate barcode image separately
      ByteArrayInputStream barcodeImage = generateBarcode128(palletReportDTO.getPallet()); // Using Pallet value
      parameters.put("BarcodeImage", barcodeImage);

      try (InputStream iconoStream = this.getClass().getResourceAsStream("/reports/Icono.png");
          InputStream logoStream = this.getClass().getResourceAsStream("/reports/Logo.png")) {

        if (iconoStream == null) {
          throw new JRException("Resource not found: /reports/Icono.png");
        }
        if (logoStream == null) {
          throw new JRException("Resource not found: /reports/Logo.png");
        }

        parameters.put("Icono", iconoStream);
        parameters.put("Logo", logoStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(jasperPrint);
      }
    } catch (IOException e) {
      throw new JRException("Error reading report resources", e);
    }
  }

  private ByteArrayInputStream generateBarcode128(String code) throws IOException {
    Code128Bean barcodeGenerator = new Code128Bean();
    BitmapCanvasProvider canvas = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      barcodeGenerator.setModuleWidth(0.3);
      barcodeGenerator.setBarHeight(15);
      barcodeGenerator.doQuietZone(true);

      canvas = new BitmapCanvasProvider(baos, "image/png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
      barcodeGenerator.generateBarcode(canvas, code);
      canvas.finish();

      return new ByteArrayInputStream(baos.toByteArray());
    } finally {
      try {
        baos.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }
}
