package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.WmsWdGeneral.AvisoModel;
import com.woden.wms_backend.security.JwtUtil;

@RestController
@RequestMapping("/general/avisos")
public class AvisoController {

  @Autowired
  private JwtUtil jwtUtil;

  // ✅ Lista de usuarios autorizados para enviar avisos (por nombreUsuario)
  private static final List<String> DESARROLLADORES = List.of(
      "lberrio", // cámbialo por tu nombreUsuario exacto
      "dagudelo" // agrega más si quieres
  );

  // 🔥 Estado en memoria — sin BD
  private static AvisoModel avisoActual = new AvisoModel("", false);

  // Endpoint público — lo consulta el front con polling (no requiere auth)
  @GetMapping("/activo")
  public ResponseEntity<AvisoModel> getAvisoActivo() {
    return ResponseEntity.ok(avisoActual);
  }

  // Publicar aviso — solo desarrolladores autorizados
  @PostMapping
  public ResponseEntity<?> publicarAviso(
      @RequestBody AvisoModel aviso,
      @RequestHeader("Authorization") String authHeader) {

    String username = extraerUsername(authHeader);
    if (username == null || !DESARROLLADORES.contains(username)) {
      return ResponseEntity.status(403)
          .body(Map.of("error", "No autorizado para publicar avisos"));
    }

    avisoActual = new AvisoModel(aviso.getMensaje(), true);
    return ResponseEntity.ok(Map.of("message", "Aviso publicado correctamente"));
  }

  // Desactivar aviso — solo desarrolladores autorizados
  @DeleteMapping
  public ResponseEntity<?> desactivarAviso(
      @RequestHeader("Authorization") String authHeader) {

    String username = extraerUsername(authHeader);
    if (username == null || !DESARROLLADORES.contains(username)) {
      return ResponseEntity.status(403)
          .body(Map.of("error", "No autorizado"));
    }

    avisoActual = new AvisoModel("", false);
    return ResponseEntity.ok(Map.of("message", "Aviso desactivado"));
  }

  private String extraerUsername(String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith("Bearer "))
        return null;
      String token = authHeader.substring(7);
      return jwtUtil.extractUsername(token); // usa tu JwtUtil existente
    } catch (Exception e) {
      return null;
    }
  }
}