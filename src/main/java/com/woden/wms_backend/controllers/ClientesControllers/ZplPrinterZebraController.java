package com.woden.wms_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/print")
public class ZplPrinterZebraController {

    // 🔹 Enviar ZPL a Zebra (igual que ya tienes)
    @PostMapping
    public ResponseEntity<String> print(@RequestBody String zpl) {
        try (var socket = new java.net.Socket("127.0.0.1", 9102);
             var out = socket.getOutputStream()) {

            out.write(zpl.getBytes());
            out.flush();
            return ResponseEntity.ok("✅ ZPL enviado a Zebra Virtual");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("❌ Error al conectar con Zebra Virtual: " + e.getMessage());
        }
    }

    // 🔹 Listar impresoras instaladas (ya lo tienes)
    @GetMapping("/printers")
    public ResponseEntity<String> listarImpresoras() {
        StringBuilder sb = new StringBuilder("Impresoras detectadas:\n");
        var servicios = javax.print.PrintServiceLookup.lookupPrintServices(null, null);

        if (servicios.length == 0) {
            sb.append("⚠️ No se detectaron impresoras instaladas en este servidor.");
        } else {
            for (var ps : servicios) {
                sb.append("🖨️ ").append(ps.getName()).append("\n");
            }
        }
        return ResponseEntity.ok(sb.toString());
    }

    // 🔹 NUEVO ENDPOINT: Listar archivos en la ruta especificada
    @GetMapping("/files")
    public ResponseEntity<?> listarArchivos() {
        String ruta = "C:\\Users\\e.agudelo\\Documents\\PruebasPrn\\TELEFONICA-COLOMBIA\\prns\\empaque";

        File folder = new File(ruta);

        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(404).body("❌ La ruta no existe o no es un directorio.");
        }

        File[] archivos = folder.listFiles();
        List<Map<String, Object>> lista = new ArrayList<>();

        if (archivos != null) {
            for (File archivo : archivos) {
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", archivo.getName());
                item.put("tipo", archivo.isDirectory() ? "DIR" : "FILE");
                item.put("tamaño", archivo.isDirectory() ? null : archivo.length());
                lista.add(item);
            }
        }

        return ResponseEntity.ok(lista); // JSON automáticamente
    }

}
