package com.woden.wms_backend.controllers.ClientesControllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.PqrsService;

@RestController
@RequestMapping("/client/pqrs")
public class PqrsController {

  private static final Logger logger = LoggerFactory.getLogger(PqrsController.class);

  private final PqrsService pqrsService;

  @Value("${PQRS_IMAGENES_PATH}")
  private String pqrsImagenesPath;

  public PqrsController(PqrsService pqrsService) {
    this.pqrsService = pqrsService;
  }

  @PostMapping("/update")
  public ResponseEntity<?> updatePqrs(@RequestBody List<Map<String, Object>> request) {
    for (Map<String, Object> item : request) {
      Integer estadoDiagnosticoId = (Integer) item.get("estadoDiagnosticoId");
      Integer fallaDiagnosticoId = (Integer) item.get("fallaDiagnosticoId");
      String name = (String) item.get("name");
      String xStudioDiagnosticoTecnicoWoden = (String) item.get("xStudioDiagnosticoTecnicoWoden");

      pqrsService.updatePqrs(estadoDiagnosticoId, fallaDiagnosticoId, name, xStudioDiagnosticoTecnicoWoden);
    }
    return ResponseEntity.ok(1);
  }

  @GetMapping("/getModelPqrs")
  public ResponseEntity<?> getModelPqrs(@RequestParam String serial, @RequestParam Integer stage) {
    return ResponseEntity.ok(pqrsService.getModelPqrs(serial, stage));
  }

  /**
   * Detalle de PQRS — reemplazo de getModelPqrs() sobre App_PQRS_Tickets /
   * App_PQRS_Truckrolls (la tabla PQRS vieja va a eliminarse). Devuelve 200
   * con null en el body si el serial no tiene PQRS en ninguna de las dos.
   */
  @GetMapping("/detalle")
  public ResponseEntity<?> buscarDetallePqrs(@RequestParam String serial) {
    return ResponseEntity.ok(pqrsService.buscarDetallePqrs(serial));
  }

  /**
   * Sirve la foto adjunta de la app externa de PQRS/Tickets cuando
   * foto_mac_path solo trae el nombre del archivo (no una URL completa).
   * Protegido por el mismo filtro JWT que el resto de /client/**, por eso el
   * frontend la trae como blob autenticado en vez de un <img src> directo.
   */
  @GetMapping("/imagen")
  public ResponseEntity<byte[]> obtenerImagenPqrs(@RequestParam String nombre) {
    // Solo el nombre del archivo — nunca una ruta (evita path traversal).
    String nombreSano = Paths.get(nombre).getFileName().toString();
    Path ruta = Paths.get(pqrsImagenesPath, nombreSano);

    if (!Files.exists(ruta)) {
      return ResponseEntity.notFound().build();
    }

    try {
      byte[] bytes = Files.readAllBytes(ruta);
      return ResponseEntity.ok().contentType(determinarContentType(nombreSano)).body(bytes);
    } catch (IOException e) {
      logger.error("[PqrsController] Error leyendo imagen '{}': {}", nombreSano, e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  private MediaType determinarContentType(String nombreArchivo) {
    String ext = nombreArchivo.toLowerCase();
    if (ext.endsWith(".png")) return MediaType.IMAGE_PNG;
    if (ext.endsWith(".gif")) return MediaType.IMAGE_GIF;
    if (ext.endsWith(".webp")) return MediaType.valueOf("image/webp");
    return MediaType.IMAGE_JPEG; // .jpg/.jpeg por defecto
  }

  @PostMapping("/updateSerialIdAndSapCodeIdPqrs")
  public ResponseEntity<?> updateSerialIdAndSapCodeIdPqrs(@RequestBody Map<String, Object> request) {
    Integer serialId = (Integer) request.get("serialId");
    Integer codigoSapId = (Integer) request.get("codigoSapId");
    String serial = (String) request.get("serial");
    pqrsService.updateSerialIdAndSapCodeIdPqrs(serialId, codigoSapId, serial);
    return ResponseEntity.ok(1);
  }

  @PostMapping("/updateObservationPqrs")
  public ResponseEntity<?> updateObservationPqrs(@RequestBody Map<String, Object> request) {
    Integer serialId = (Integer) request.get("serialId");
    String serial = (String) request.get("serial");
    String observacion = (String) request.get("observacion");
    pqrsService.updateObservationPqrs(serialId, serial, observacion);
    return ResponseEntity.ok(1);
  }
}
