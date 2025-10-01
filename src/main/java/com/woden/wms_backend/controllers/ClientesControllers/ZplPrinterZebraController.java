package com.woden.wms_backend.controllers.ClientesControllers;

import java.io.OutputStream;
import java.net.Socket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/client/print")

public class ZplPrinterZebraController {

    @PostMapping
    public ResponseEntity<String> print(@RequestBody String zpl) {
        try (Socket socket = new Socket("127.0.0.1", 9102);
             OutputStream out = socket.getOutputStream()) {

            out.write(zpl.getBytes());
            out.flush();

            return ResponseEntity.ok("✅ ZPL enviado a Zebra Virtual");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("❌ Error al conectar con Zebra Virtual: " + e.getMessage());
        }
    }
}