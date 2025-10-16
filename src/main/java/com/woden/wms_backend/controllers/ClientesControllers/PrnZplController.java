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

/*    @GetMapping("/ruta-cliente/{cliente}")
    public Map<String, Object> getRutaCliente(@PathVariable String cliente) {
        Map<String, Object> info = new HashMap<>();

        try {
            // 🔹 Decodificar cliente (por si viene con %20 o similares)
            String clienteDecodificado = java.net.URLDecoder.decode(cliente, StandardCharsets.UTF_8).trim();

            // 🔹 Obtener la ruta física desde el servicio
            String rutaFisica = service.getRutaCliente(clienteDecodificado);

            // ✅ Forzar el formato UNC correcto (4 barras visibles)
            if (!rutaFisica.startsWith("\\\\")) {
                rutaFisica = "\\\\" + rutaFisica; // asegúrate de que empiece con doble barra real
            }

            // 🔹 Crear objeto File con la ruta limpia (solo 2 barras físicas)
            File dir = new File(rutaFisica);

            // 🔹 Generar la versión JSON con 4 barras visibles
            String rutaJson = rutaFisica.replace("\\", "\\\\");
            // ejemplo resultante: "\\\\10.128.0.28\\archivos\\ENV\\PRD\\etiquetas\\LEGACY-COSTARICA\\PRNS\\EMPAQUE"

            // 🔹 Construir respuesta
            info.put("cliente", clienteDecodificado);
            info.put("rutaConstruida", rutaJson);
            info.put("existe", dir.exists());
            info.put("esDirectorio", dir.isDirectory());

            // 📁 Subcarpetas
            List<String> subcarpetas = service.listarSubcarpetasCliente(clienteDecodificado);
            info.put("subcarpetas", subcarpetas);
            info.put("cantidadSubcarpetas", subcarpetas.size());

            // 🧾 Log en consola (también con las 4 barras)
            String rutaConCuatroBarras = rutaFisica.replace("\\", "\\\\");
            System.out.println("📂 Ruta PRN generada para cliente [" + clienteDecodificado + "]: " + rutaConCuatroBarras);
            if (dir.exists()) {
                System.out.println("✅ Directorio accesible");
            }
            if (!subcarpetas.isEmpty()) {
                System.out.println("📁 Subcarpetas encontradas: " + String.join(", ", subcarpetas));
            }

        } catch (Exception e) {
            info.put("error", e.getMessage());
            info.put("existe", false);
            info.put("esDirectorio", false);
            info.put("subcarpetas", List.of());
            info.put("cantidadSubcarpetas", 0);
        }

        return info;
    }

 */
    @GetMapping("/ruta")
    public Map<String, Object> getRutaPorPath(@RequestParam String path) {
        Map<String, Object> info = new HashMap<>();

        try {
            // 🔹 1️⃣ Decodificar ruta recibida del frontend (de %5C%5C a \\)
            String rutaDecodificada = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8);

            // 🔹 2️⃣ No tocar estructura UNC, solo limpiar posibles espacios y barras normales
            String rutaNormalizada = rutaDecodificada
                    .replaceAll("[/]+", "\\\\") // convierte / a \
                    .trim();

            // 🔹 3️⃣ Mostrar cómo llega la ruta
            System.out.println("📥 Ruta PRN recibida (cruda): " + path);
            System.out.println("📂 Ruta PRN decodificada: " + rutaDecodificada);
            System.out.println("📁 Ruta PRN normalizada: " + rutaNormalizada);

            // 🔹 4️⃣ Crear File y validar
            File dir = new File(rutaNormalizada);
            boolean existe = dir.exists();
            boolean esDirectorio = dir.isDirectory();
            boolean legible = dir.canRead();

            info.put("rutaRecibida", rutaNormalizada.replace("\\", "\\\\"));
            info.put("existe", existe);
            info.put("esDirectorio", esDirectorio);
            info.put("esLegible", legible);

            // 🔹 5️⃣ Listar subcarpetas si existe
            if (existe && esDirectorio && legible) {
                List<String> subcarpetas = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                        .map(File::getName)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());

                info.put("subcarpetas", subcarpetas);
                info.put("cantidadSubcarpetas", subcarpetas.size());
            } else {
                info.put("subcarpetas", List.of());
                info.put("cantidadSubcarpetas", 0);
            }

            // 🔹 6️⃣ Log visual claro en consola
            if (existe) {
                System.out.println("✅ Directorio accesible: " + rutaNormalizada);
            } else {
                System.out.println("❌ No se puede acceder a la ruta: " + rutaNormalizada);
            }

        } catch (Exception e) {
            info.put("error", e.getMessage());
            info.put("existe", false);
            info.put("esDirectorio", false);
            info.put("subcarpetas", List.of());
            info.put("cantidadSubcarpetas", 0);
            System.err.println("💥 Error procesando ruta PRN: " + e.getMessage());
        }

        return info;
    }




}