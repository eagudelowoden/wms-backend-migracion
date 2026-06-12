package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint TEMPORAL para diagnosticar el acceso a los archivos PRN del
 * servidor de etiquetas. Eliminar cuando se resuelva el problema de permisos.
 *
 * Uso:
 *   GET /general/diagnostico/prn                          → lista las carpetas de clientes
 *   GET /general/diagnostico/prn?cliente=TIGO-COLOMBIA-MEDELLIN → lista todo el contenido del cliente
 */
@RestController
@RequestMapping("/general/diagnostico")
public class DiagnosticoArchivoController {

  private static final String RUTA_BASE = "\\\\10.128.0.28\\archivos\\ENV\\PRD\\etiquetas";
  private static final int MAX_PROFUNDIDAD = 6;

  @GetMapping("/prn")
  public ResponseEntity<Map<String, Object>> diagnosticarPrn(
      @RequestParam(required = false) String cliente) {

    Map<String, Object> info = new LinkedHashMap<>();
    info.put("usuarioJvm", System.getProperty("user.name"));
    info.put("so", System.getProperty("os.name"));
    info.put("rutaBase", RUTA_BASE);

    // Evitar salirse de la carpeta de etiquetas con ".." o rutas absolutas
    if (cliente != null
        && (cliente.contains("..") || cliente.contains("\\") || cliente.contains("/"))) {
      info.put("error", "Nombre de cliente inválido.");
      return ResponseEntity.badRequest().body(info);
    }

    // Sin cliente: probar acceso al share y listar las carpetas de clientes
    if (cliente == null || cliente.trim().isEmpty()) {
      File base = new File(RUTA_BASE);
      info.put("exists", base.exists());
      info.put("canRead", base.canRead());
      try {
        String[] carpetas = base.list();
        info.put("clientes",
            carpetas != null ? List.of(carpetas) : "list() devolvió null (sin acceso al share)");
      } catch (Exception e) {
        info.put("errorListar", e.getClass().getSimpleName() + ": " + e.getMessage());
      }
      return ResponseEntity.ok(info);
    }

    // Con cliente: recorrer toda su carpeta y listar subcarpetas y archivos .prn
    File carpetaCliente = new File(RUTA_BASE, cliente.trim());
    info.put("cliente", cliente.trim());
    info.put("rutaCliente", carpetaCliente.getAbsolutePath());
    info.put("exists", carpetaCliente.exists());
    info.put("canRead", carpetaCliente.canRead());

    if (!carpetaCliente.exists()) {
      info.put("error", "La carpeta del cliente no existe o no hay acceso para verla.");
      return ResponseEntity.ok(info);
    }

    List<Map<String, Object>> prns = new ArrayList<>();
    List<String> carpetas = new ArrayList<>();
    List<String> errores = new ArrayList<>();
    recorrer(carpetaCliente, "", 0, prns, carpetas, errores);

    info.put("totalCarpetas", carpetas.size());
    info.put("totalPrn", prns.size());
    info.put("carpetas", carpetas);
    info.put("prns", prns);
    if (!errores.isEmpty()) {
      info.put("errores", errores);
    }

    // Prueba de lectura real sobre el primer PRN: reproduce el "Access is denied"
    if (!prns.isEmpty()) {
      String rutaPrimerPrn = String.valueOf(prns.get(0).get("ruta"));
      try (FileInputStream fis = new FileInputStream(rutaPrimerPrn)) {
        fis.read();
        info.put("pruebaLectura", "OK → " + rutaPrimerPrn);
      } catch (Exception e) {
        info.put("pruebaLectura",
            e.getClass().getSimpleName() + ": " + e.getMessage());
      }
    }

    return ResponseEntity.ok(info);
  }

  /**
   * Ejecuta "net use" dentro del proceso del backend para ver las conexiones
   * SMB de ESTA sesión y, opcionalmente, intentar conectarse al share con
   * credenciales explícitas. Devuelve la salida cruda de Windows.
   *
   *   GET /general/diagnostico/netuse                       → lista conexiones actuales
   *   GET /general/diagnostico/netuse?usuario=X&clave=Y     → intenta conectar y reporta
   */
  @GetMapping("/netuse")
  public ResponseEntity<Map<String, Object>> diagnosticarNetUse(
      @RequestParam(required = false) String usuario,
      @RequestParam(required = false) String clave) {

    Map<String, Object> info = new LinkedHashMap<>();
    info.put("usuarioJvm", System.getProperty("user.name"));

    // 1. Conexiones SMB actuales de esta sesión
    info.put("conexionesActuales", ejecutar(new String[] { "cmd", "/c", "net", "use" }));

    // 2. Si mandan credenciales, intentar la conexión desde este proceso
    if (usuario != null && !usuario.isBlank() && clave != null) {
      info.put("intentoConexion", ejecutar(new String[] {
          "cmd", "/c", "net", "use", "\\\\10.128.0.28\\archivos",
          "/user:" + usuario, clave }));

      // 3. Reintentar el acceso después del net use
      File base = new File(RUTA_BASE);
      Map<String, Object> reintento = new LinkedHashMap<>();
      reintento.put("exists", base.exists());
      reintento.put("canRead", base.canRead());
      try {
        String[] carpetas = base.list();
        reintento.put("clientes",
            carpetas != null ? List.of(carpetas) : "list() devolvió null (sin acceso)");
      } catch (Exception e) {
        reintento.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
      }
      info.put("accesoDespuesDeConectar", reintento);
    }

    return ResponseEntity.ok(info);
  }

  private String ejecutar(String[] comando) {
    try {
      Process proceso = new ProcessBuilder(comando)
          .redirectErrorStream(true)
          .start();
      StringBuilder salida = new StringBuilder();
      try (java.io.BufferedReader br = new java.io.BufferedReader(
          new java.io.InputStreamReader(proceso.getInputStream()))) {
        String linea;
        while ((linea = br.readLine()) != null) {
          salida.append(linea).append("\n");
        }
      }
      int codigo = proceso.waitFor();
      return "exitCode=" + codigo + "\n" + salida;
    } catch (Exception e) {
      return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
  }

  private void recorrer(File dir, String rutaRelativa, int nivel,
      List<Map<String, Object>> prns, List<String> carpetas, List<String> errores) {

    if (nivel > MAX_PROFUNDIDAD) {
      return;
    }

    File[] hijos;
    try {
      hijos = dir.listFiles();
    } catch (Exception e) {
      errores.add(rutaRelativa + " → " + e.getClass().getSimpleName() + ": " + e.getMessage());
      return;
    }

    if (hijos == null) {
      errores.add((rutaRelativa.isEmpty() ? "(raíz)" : rutaRelativa)
          + " → listFiles() devolvió null (sin acceso)");
      return;
    }

    for (File hijo : hijos) {
      String rutaHijo = rutaRelativa.isEmpty()
          ? hijo.getName()
          : rutaRelativa + "\\" + hijo.getName();

      if (hijo.isDirectory()) {
        carpetas.add(rutaHijo);
        recorrer(hijo, rutaHijo, nivel + 1, prns, carpetas, errores);
      } else if (hijo.getName().toLowerCase().endsWith(".prn")) {
        Map<String, Object> prn = new LinkedHashMap<>();
        prn.put("nombre", hijo.getName());
        prn.put("rutaRelativa", rutaHijo);
        prn.put("ruta", hijo.getAbsolutePath());
        prn.put("canRead", hijo.canRead());
        prn.put("tamano", hijo.length());
        prns.add(prn);
      }
    }
  }
}
