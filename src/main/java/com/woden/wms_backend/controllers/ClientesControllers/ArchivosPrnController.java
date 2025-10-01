package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/archivos")
public class ArchivosPrnController {

    @GetMapping("/listar")
    public ResponseEntity<?> listarArchivos(@RequestParam String ruta) {
        try {
            Path dirPath = Paths.get(ruta);

            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                return ResponseEntity.badRequest()
                        .body("❌ La ruta no existe o no es un directorio: " + ruta);
            }

            // Solo archivos .prn
            List<String> archivos = Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.toLowerCase().endsWith(".prn"))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(archivos);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Error leyendo la carpeta: " + e.getMessage());
        }
    }
}
