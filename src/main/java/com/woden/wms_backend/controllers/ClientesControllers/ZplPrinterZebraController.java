package com.woden.wms_backend.controllers.ClientesControllers;

import java.io.OutputStream;
import java.net.Socket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/print")
public class ZplPrinterZebraController {

    // 🔹 Clase que representa la petición desde Angular
    public static class PrintRequest {
        public String zpl;
        public String host;
        public int port;
    }

    @PostMapping
    public ResponseEntity<String> print(@RequestBody PrintRequest request) {
        try (Socket socket = new Socket(request.host, request.port);
             OutputStream out = socket.getOutputStream()) {

            out.write(request.zpl.getBytes("UTF-8")); // forzamos UTF-8
            out.flush();

            return ResponseEntity.ok(
                    "✅ ZPL enviado correctamente a " + request.host + ":" + request.port
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("❌ Error al conectar con impresora en "
                            + request.host + ":" + request.port
                            + " → " + e.getMessage());
        }
    }
}
