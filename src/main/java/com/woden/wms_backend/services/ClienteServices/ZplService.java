package com.woden.wms_backend.services.ClienteServices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.IngresoModel;

@Service
public class ZplService {
  public String generarZpl(List<IngresoModel> ingresos) throws IOException {
    int cantidad = ingresos.size();

    Path plantillaPath = Paths.get("plantillas/codigo" + cantidad + ".prn");
    if (!Files.exists(plantillaPath)) {
      throw new IOException("No se encontró plantilla para cantidad: " + cantidad);
    }

    List<String> lineas = Files.readAllLines(plantillaPath, StandardCharsets.UTF_8);
    StringBuilder zplBuilder = new StringBuilder();
    for (String linea : lineas) {
      zplBuilder.append(linea).append("\n");
    }

    String zpl = zplBuilder.toString();

    for (int i = 0; i < ingresos.size(); i++) {
      IngresoModel ingreso = ingresos.get(i);
      zpl = zpl.replace("serial" + (i + 1), ingreso.getSerial() != null ? ingreso.getSerial() : "");
    }

    // Reemplazos globales, si existen
    if (!ingresos.isEmpty()) {
      IngresoModel uno = ingresos.get(0);
      zpl = zpl.replace("modelo", uno.getModelo() != null ? uno.getModelo() : "");
      zpl = zpl.replace("usuario", uno.getUsuario() != null ? uno.getUsuario() : "");
      zpl = zpl.replace("fecha", uno.getFecha() != null ? uno.getFecha().toString() : "");
    }

    return zpl;
  }
}
