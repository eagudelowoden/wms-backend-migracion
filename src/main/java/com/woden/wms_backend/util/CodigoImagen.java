package com.woden.wms_backend.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class CodigoImagen {

  public record Imagen(String uri, double proporcion) {}

  private CodigoImagen() {
  }

  public static String barras(String codigo) throws Exception {
    return barrasImagen(codigo).uri();
  }

  public static String qr(String texto) throws Exception {
    return qrImagen(texto).uri();
  }

  public static Imagen barrasImagen(String codigo) throws Exception {
    Code128Bean bean = new Code128Bean();
    bean.setModuleWidth(0.3);
    bean.setBarHeight(15);
    bean.doQuietZone(true);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", 300, BufferedImage.TYPE_BYTE_BINARY,
          false, 0);
      bean.generateBarcode(canvas, codigo);
      canvas.finish();
      byte[] png = out.toByteArray();
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
      double proporcion = img == null || img.getHeight() == 0 ? 3 : img.getWidth() / (double) img.getHeight();
      return new Imagen("data:image/png;base64," + Base64.getEncoder().encodeToString(png), proporcion);
    }
  }

  public static Imagen qrImagen(String texto) throws Exception {
    BitMatrix m = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 0, 0,
        Map.of(EncodeHintType.CHARACTER_SET, "UTF-8", EncodeHintType.MARGIN, 1,
            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M));
    int escala = 8;
    BufferedImage img = new BufferedImage(m.getWidth() * escala, m.getHeight() * escala, BufferedImage.TYPE_BYTE_BINARY);
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        img.setRGB(x, y, m.get(x / escala, y / escala) ? 0xFF000000 : 0xFFFFFFFF);
      }
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, "png", out);
      return new Imagen("data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray()), 1);
    }
  }
}
