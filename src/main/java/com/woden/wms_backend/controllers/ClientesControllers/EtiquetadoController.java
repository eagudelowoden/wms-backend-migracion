package com.woden.wms_backend.controllers.ClientesControllers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.clientDTO.EtiquetadoRequestDTO;
import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetadoService;

@RestController
@RequestMapping("/client/etiquetado")
public class EtiquetadoController extends BaseController<EtiquetadoModel, Integer> {

  public EtiquetadoController(EtiquetadoService service) {
    super(service);
  }

  @Autowired
  private EtiquetadoService etiquetadoService;

  @PostMapping("/insertEtiquetado")
  public ResponseEntity<Integer> insertEtiquetado(@RequestBody List<EtiquetadoModel> etiquetados) {
    try {
      for (EtiquetadoModel etiquetado : etiquetados) {
        etiquetadoService.insertEtiquetado(etiquetado.getSerial(), etiquetado.getMac(), etiquetado.getVariable1(),
            etiquetado.getVariable2(), etiquetado.getVariable3(), etiquetado.getVariable4(),
            etiquetado.getReimpresion(), etiquetado.getUsuarioId(), etiquetado.getFecha());
      }
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace(); // 👈 se mantiene simple sin log
      return ResponseEntity.badRequest().body(0);
    }
  }

  /**
   * 🏗️ GENERA CÓDIGO ZPL PARA IMPRIMIR
   * 
   * @param request DTO con todos los datos necesarios (plantillas, seriales,
   *                campos, etc.)
   * @return String con el código ZPL generado
   */
  @PostMapping("/generar")
  public ResponseEntity<String> generarZpl(@RequestBody EtiquetadoRequestDTO request) {
    try {
      // Validaciones básicas
      if (request == null) {
        return ResponseEntity.badRequest().body("❌ Request vacío");
      }

      if (request.getEtiqueta() == null) {
        return ResponseEntity.badRequest().body("❌ Etiqueta no especificada");
      }

      if (request.getListaSeriales() == null || request.getListaSeriales().isEmpty()) {
        return ResponseEntity.badRequest().body("❌ Lista de seriales vacía");
      }

      if (request.getRutaPlantillas() == null || request.getRutaPlantillas().isBlank()) {
        System.out.println("⚠️ Ruta de plantillas no especificada en request, se resolverá localmente si PRN_LOCAL_PATH está configurado");
      }

      // Generar ZPL
      System.out.println("🔄 Generando ZPL para " + request.getListaSeriales().size() + " seriales");
      System.out.println("📂 Ruta plantillas: " + request.getRutaPlantillas());
      System.out.println("🏷️ Etiqueta: " + request.getEtiqueta().getNombre());

      String zpl = etiquetadoService.generarCodigoZpl(request);

      if (zpl == null || zpl.trim().isEmpty()) {
        System.err.println("❌ ZPL generado está vacío");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("❌ Error: ZPL generado está vacío");
      }

      System.out.println("✅ ZPL generado correctamente (" + zpl.length() + " caracteres)");
      return ResponseEntity.ok(zpl);

    } catch (IllegalArgumentException e) {
      System.err.println("❌ Error de validación: " + e.getMessage());
      return ResponseEntity.badRequest().body("❌ " + e.getMessage());

    } catch (RuntimeException e) {
      System.err.println("❌ Error al leer plantilla: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("❌ " + e.getMessage());

    } catch (Exception e) {
      System.err.println("❌ Error generando ZPL: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ Error interno generando ZPL: " + e.getMessage());
    }
  }

  /**
   * 👁️ GENERA VISTA PREVIA DE ZPL COMO IMAGEN BASE64
   * Usa Labelary API para convertir ZPL a PNG
   * 
   * @param zpl Código ZPL a convertir
   * @return Data URL con imagen en base64 (data:image/png;base64,...)
   */
  @PostMapping("/preview-base64")
  public ResponseEntity<String> obtenerPreviewBase64(@RequestBody String zpl) {
    final int maxIntentos = 3;
    Exception ultimoError = null;

    for (int intento = 1; intento <= maxIntentos; intento++) {
      try {
        // Endpoint Labelary para generar imagen PNG
        // Parámetros: 8dpmm = densidad, 4x6 = tamaño de etiqueta (ajusta según necesites)
        URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "image/png");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
          os.write(zpl.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
          System.err.println("❌ Intento " + intento + "/" + maxIntentos + ": Labelary API retornó código: " + responseCode);
          esperarAntesDeReintentar(intento);
          continue;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream in = conn.getInputStream()) {
          byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
          }
        }

        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        String dataUrl = "data:image/png;base64," + base64Image;
        return ResponseEntity.ok(dataUrl);

      } catch (Exception e) {
        ultimoError = e;
        System.err.println("❌ Intento " + intento + "/" + maxIntentos + ": error generando vista previa: " + e.getMessage());
        esperarAntesDeReintentar(intento);
      }
    }

    System.err.println("❌ /preview-base64 falló tras " + maxIntentos + " intentos: "
        + (ultimoError != null ? ultimoError.getMessage() : ""));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("❌ Error generando vista previa tras varios intentos");
  }

  /**
   * 👁️ GENERA VISTA PREVIA COMO BLOB (alternativa)
   * Retorna directamente los bytes de la imagen PNG
   */
  @PostMapping("/vistaPrevia")
  public ResponseEntity<byte[]> vistaPreviaZpl(@RequestBody String zpl) {
    final int maxIntentos = 3;
    Exception ultimoError = null;

    for (int intento = 1; intento <= maxIntentos; intento++) {
      try {
        URL url = new URL("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "image/png");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(15000);

        try (OutputStream os = conn.getOutputStream()) {
          os.write(zpl.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
          System.err.println("❌ Intento " + intento + "/" + maxIntentos + ": Labelary API retornó código: " + responseCode);
          esperarAntesDeReintentar(intento);
          continue;
        }

        try (InputStream in = conn.getInputStream()) {
          byte[] imageBytes = in.readAllBytes();
          return ResponseEntity.ok()
              .contentType(MediaType.IMAGE_PNG)
              .body(imageBytes);
        }

      } catch (Exception e) {
        ultimoError = e;
        System.err.println("❌ Intento " + intento + "/" + maxIntentos + ": error generando vista previa: " + e.getMessage());
        esperarAntesDeReintentar(intento);
      }
    }

    System.err.println("❌ /vistaPrevia falló tras " + maxIntentos + " intentos: "
        + (ultimoError != null ? ultimoError.getMessage() : ""));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
  }

  /** Backoff simple: 300ms, 600ms, ... antes del siguiente intento. */
  private void esperarAntesDeReintentar(int intento) {
    try {
      Thread.sleep(300L * intento);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
