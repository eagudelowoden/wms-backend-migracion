// package com.woden.wms_backend.services.ClienteServices;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import java.io.File;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;

// @Service
// public class PrnConfigService {

//     @Value("${prn.base-path}")
//     private String basePath; // Ejemplo: Z:\\archivos\\ENV\\PRD\\archivos\\etiquetas

//     public String getBasePath() {
//         return normalizarRuta(basePath);
//     }

//     /**
//      * 🧩 Construye la ruta completa para un cliente específico
//      */
//     public String getRutaCliente(String cliente) {
//         // No reemplazar guiones — conservar tal cual
//         String clienteNormalizado = cliente.trim();

//         // Construir la ruta base completa
//         Path ruta = Paths.get(getBasePath(), clienteNormalizado, "PRNS", "EMPAQUE");

//         return ruta.toString();
//     }

//     /**
//      * 🧾 Lista las subcarpetas dentro de la ruta del cliente
//      */
//     public List<String> listarSubcarpetasCliente(String cliente) {
//         String rutaCliente = getRutaCliente(cliente);
//         File dir = new File(rutaCliente);

//         if (!dir.exists() || !dir.isDirectory()) {
//             return List.of();
//         }

//         return Arrays.stream(Objects.requireNonNull(dir.listFiles(File::isDirectory)))
//                 .map(File::getName)
//                 .sorted(String::compareToIgnoreCase)
//                 .collect(Collectors.toList());
//     }

//     private String normalizarRuta(String ruta) {
//         if (ruta == null) return "";
//         return ruta.trim().replaceAll("[/]+", "\\\\").replaceAll("\\\\\\\\+", "\\\\\\\\");
//     }

//     public boolean rutaExiste(String ruta) {
//         File dir = new File(ruta);
//         return dir.exists() && dir.isDirectory();
//     }
// }