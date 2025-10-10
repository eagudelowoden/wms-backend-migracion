package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.services.ClienteServices.PrnConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client/prn")
@CrossOrigin(origins = "*")
public class PrnZplController {

    @Autowired
    private PrnConfigService service;

    /**
     * 🧠 Endpoint para verificar que la ruta se está leyendo correctamente
     */
    @GetMapping("/ping")
    public Map<String, String> testConnection() {
        String path = service.getBasePath();
        Map<String, String> info = new HashMap<>();
        File baseDir = new File(path);

        info.put("rutaConfigurada", path);
        info.put("existe", String.valueOf(baseDir.exists()));
        info.put("esDirectorio", String.valueOf(baseDir.isDirectory()));
        info.put("archivosDetectados",
                baseDir.exists() ? String.valueOf(Objects.requireNonNull(baseDir.list()).length) : "0");

        return info;
    }

    /**
     * 🗂️ Listar subcarpetas dentro de la ruta base
     */
    @GetMapping("/folders")
    public List<String> getFolders() {
        File baseDir = new File(service.getBasePath());
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new RuntimeException("❌ No se encontró el directorio base: " + service.getBasePath());
        }

        return Arrays.stream(Objects.requireNonNull(baseDir.listFiles(File::isDirectory)))
                .map(File::getName)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * 📄 Listar archivos .PRN dentro de una subcarpeta
     */
    @GetMapping("/templates/{folder}")
    public List<String> getTemplatesByFolder(@PathVariable String folder) {
        File dir = new File(service.getBasePath(), folder);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("❌ No se encontró la carpeta: " + folder);
        }

        return Arrays.stream(Objects.requireNonNull(dir.listFiles((f) ->
                        f.isFile() && f.getName().toLowerCase().endsWith(".prn"))))
                .map(File::getName)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * 🧾 Obtener el contenido de un archivo .PRN
     */
    @GetMapping("/content")
    public String getPrnContent(@RequestParam String folder, @RequestParam String file) throws java.io.IOException {
        Path path = Paths.get(service.getBasePath(), folder, file);
        if (!Files.exists(path)) {
            throw new RuntimeException("❌ No se encontró el archivo: " + file);
        }
        return Files.readString(path);
    }
}
