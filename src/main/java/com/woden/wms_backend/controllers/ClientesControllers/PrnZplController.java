package com.woden.wms_backend.controllers.ClientesControllers;

import com.woden.wms_backend.dto.clientDTO.EtiquetaListDTO;
import com.woden.wms_backend.services.ClienteServices.EtiquetaService;
import com.woden.wms_backend.services.ClienteServices.PrnConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client/prn")
@CrossOrigin(origins = "*")
public class PrnZplController {

    @Autowired
    private PrnConfigService service;

    @Autowired
    private EtiquetaService etiquetaService;

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
    @GetMapping("/templates/{cliente}/{folder}")
    public List<String> getTemplatesByFolder(
            @PathVariable String cliente,
            @PathVariable String folder) {

        // 🧩 Construir ruta completa: ...\CLIENTE\prns\empaque\folder
        String rutaCliente = service.getRutaCliente(cliente);
        File dir = new File(rutaCliente, folder);

        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("❌ No se encontró la carpeta: " + folder + " en " + rutaCliente);
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
    public String getPrnContent(
            @RequestParam String cliente,
            @RequestParam String folder,
            @RequestParam String file) throws IOException {

        // 🧩 Construir ruta completa
        String rutaCliente = service.getRutaCliente(cliente);
        Path path = Paths.get(rutaCliente, folder, file);

        if (!Files.exists(path)) {
            throw new RuntimeException("❌ No se encontró el archivo: " + file + " en " + path);
        }

        return Files.readString(path);
    }


    @GetMapping("/getListLabel/{tipo}")
    public List<EtiquetaListDTO> getListLabel(@PathVariable String tipo) {
        return etiquetaService.getListLabel(tipo);
    }

    @PostMapping("/preview")
    public ResponseEntity<byte[]> renderZpl(@RequestBody String zpl) throws IOException {
        String url = "https://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "image/png");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(zpl.getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream in = connection.getInputStream()) {
            byte[] imageBytes = in.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/ruta/{cliente}")
    public Map<String, Object> getRutaCliente(@PathVariable String cliente) {
        Map<String, Object> info = new HashMap<>();
        String ruta = service.getRutaCliente(cliente);
        File dir = new File(ruta);

        info.put("cliente", cliente);
        info.put("rutaConstruida", ruta);
        info.put("existe", dir.exists());
        info.put("esDirectorio", dir.isDirectory());

        // 📁 Listar subcarpetas si existe
        List<String> subcarpetas = service.listarSubcarpetasCliente(cliente);
        info.put("subcarpetas", subcarpetas);
        info.put("cantidadSubcarpetas", subcarpetas.size());

        System.out.println("📂 Ruta PRN generada para cliente [" + cliente + "]: " + ruta);
        if (!subcarpetas.isEmpty()) {
            System.out.println("📁 Subcarpetas encontradas: " + String.join(", ", subcarpetas));
        }

        return info;
    }



}
