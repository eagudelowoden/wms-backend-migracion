package com.woden.wms_backend.controllers.IntegracionControllers;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.IntegracionServices.HojaVidaIntegracionService;

/**
 * Descarga de evidencias para integraciones externas (sistema a sistema, sin
 * usuario/JWT — ver ApiKeyAuthenticationFilter). Genérico a nivel de ruta
 * (/evidencias/**) para dejar espacio a otros tipos de evidencia si aparecen
 * más adelante; por ahora solo hay Hoja de Vida.
 *
 * clienteId va como parámetro explícito porque no hay sesión de usuario de
 * la que sacarlo — ApiKeyAuthenticationFilter ya lo usó para dejar
 * ClientDatabaseContext apuntando a la BD correcta antes de llegar acá.
 */
@RestController
@RequestMapping("/evidencias")
public class EvidenciasController {

  private static final Logger logger = LoggerFactory.getLogger(EvidenciasController.class);

  @Autowired
  private HojaVidaIntegracionService hojaVidaIntegracionService;

  @GetMapping("/hoja-vida/descargar")
  public ResponseEntity<?> descargarHojaVida(
      @RequestParam String serial,
      @RequestParam Integer clienteId,
      @RequestParam(required = false) String nombre) {
    try {
      Path archivo = hojaVidaIntegracionService.buscarArchivo(serial);
      if (archivo == null || !Files.exists(archivo)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No hay hoja de vida disponible para el serial indicado.");
      }

      Resource resource = new FileSystemResource(archivo);
      String nombreDescarga = construirNombreDescarga(nombre, archivo.getFileName().toString());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreDescarga + "\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);
    } catch (Exception e) {
      logger.error("[EvidenciasController] Error descargando hoja de vida serial={}: {}", serial, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al descargar la hoja de vida");
    }
  }

  /**
   * Si no mandan "nombre", se descarga con el nombre real del archivo (como
   * hoy). Si sí lo mandan, se usa — pero la extensión SIEMPRE es la del
   * archivo real, sin importar qué extensión (o ninguna) haya puesto el
   * llamador, para no entregar un .pdf que en realidad es un .docx o
   * viceversa. También se sanea para que no rompa el header Content-Disposition
   * (comillas, saltos de línea, etc. — mismo criterio que sanitizarSerial en
   * DiagnosticoService).
   */
  private String construirNombreDescarga(String nombrePersonalizado, String nombreArchivoReal) {
    String extensionReal = "";
    int puntoReal = nombreArchivoReal.lastIndexOf('.');
    if (puntoReal > 0) {
      extensionReal = nombreArchivoReal.substring(puntoReal + 1);
    }

    if (nombrePersonalizado == null || nombrePersonalizado.isBlank()) {
      return nombreArchivoReal;
    }

    String base = nombrePersonalizado.strip().replaceAll("[\\r\\n\"]", "");
    int puntoBase = base.lastIndexOf('.');
    if (puntoBase > 0) {
      base = base.substring(0, puntoBase); // se descarta la extensión que haya puesto el llamador
    }
    if (base.isBlank()) {
      return nombreArchivoReal;
    }

    return extensionReal.isBlank() ? base : base + "." + extensionReal;
  }
}
