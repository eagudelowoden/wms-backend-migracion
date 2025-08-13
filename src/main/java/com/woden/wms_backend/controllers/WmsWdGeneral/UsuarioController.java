package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.LoginRequest;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.security.JwtUtil;
import com.woden.wms_backend.services.WmsWdGeneral.UsuarioService;
import com.woden.wms_backend.util.EncryptUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/general/users")
public class UsuarioController {

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    @Autowired
    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioModel> getAll() {
        return usuarioService.getAll();
    }

    @PostMapping
    public UsuarioModel guardarUsuario(@RequestBody UsuarioModel usuario) {
        return usuarioService.saveUser(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<UsuarioModel> usuario = usuarioService.getUserById(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    EncryptUtil encryptUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> iniciarSesion(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        if (loginRequest.getNombreUsuario() == null || loginRequest.getClave() == null) {
            response.put("message", "Faltan credenciales");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<UsuarioModel> usuarioOpt = usuarioService.getUserByNameUser(loginRequest.getNombreUsuario());

        if (usuarioOpt.isEmpty()) {
            response.put("message", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(response);
        }

        UsuarioModel usuario = usuarioOpt.get();

        // Encriptar la contraseña proporcionada por el usuario
        String claveEncriptada = encryptUtil.encode(loginRequest.getClave());

        // Comparar la contraseña encriptada almacenada con la contraseña encriptada
        // proporcionada
        if (!usuario.getClave().equals(claveEncriptada)) {
            response.put("message", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(response);
        }

        // Generar el token JWT
        String token = jwtUtil.generateToken(usuario.getNombreUsuario(), "WmsWdGeneral", "WmsWdGeneral", 0);

        response.put("token", token);
        response.put("usuarioId", usuario.getId().toString());
        response.put("message", "Inicio de sesión exitoso");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioLogicamente(@PathVariable int id) {
        boolean eliminado = usuarioService.deleteUser(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activos")
    public List<UsuarioModel> getUsersActive() {
        return usuarioService.getUserActive();
    }

    @GetMapping("/getModel")
    public ResponseEntity<?> getModelUsuario(
            @RequestParam String nombreUsuario,
            @RequestParam String clave) {

        // 🔐 Encriptar la clave para que coincida con la de la base de datos
        String claveEncriptada = encryptUtil.encode(clave);

        UsuarioModel usuario = usuarioService.getModel(nombreUsuario, claveEncriptada);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/listUser")
    public ResponseEntity<List<String>> getListUser(@RequestParam Integer idCliente, @RequestParam String tipoPerfil) {
        List<String> listUser = usuarioService.getListUser(idCliente, tipoPerfil);
        return ResponseEntity.ok(listUser);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Client-Token", required = false) String clientTokenHeader,
            HttpServletResponse response) {

        log.info("🔄 Endpoint de refresh-token iniciado");
        // 1. Validar el token de autorización (general)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Token general mal formado o faltante");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token general faltante o mal formado"));
        }
        String oldGeneralToken = authHeader.substring(7);

        // 2. Validar el clientToken (si se envía)
        String oldClientToken = null;
        if (clientTokenHeader != null && clientTokenHeader.startsWith("Bearer ")) {
            oldClientToken = clientTokenHeader.substring(7);
        }

        log.info("🔍 Token general recibido: {}", oldGeneralToken);
        log.info("🔍 Token cliente recibido: {}", oldClientToken);
        try {
            // 3. Validar el token general (debe ser válido)
            if (!jwtUtil.validateToken(oldGeneralToken)) {
                log.warn("❌ Token general no válido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token general no válido o expirado"));
            }

            // 4. Extraer datos del token general
            String username = jwtUtil.extractUsername(oldGeneralToken);
            String clientDb = jwtUtil.extractClientDb(oldGeneralToken);
            String clientName = jwtUtil.extractClientName(oldGeneralToken);
            Integer clientId = jwtUtil.extractClientId(oldGeneralToken);

            // 5. Si hay un clientToken, validarlo y extraer su información (si es
            // diferente)
            String newClientName = clientName;
            String newClientDb = clientDb;
            Integer newClientId = clientId;

            if (oldClientToken != null && jwtUtil.validateToken(oldClientToken)) {
                // Si el token del cliente es válido, usamos sus datos (pueden ser diferentes)
                newClientName = jwtUtil.extractClientName(oldClientToken);
                newClientDb = jwtUtil.extractClientDb(oldClientToken);
                newClientId = jwtUtil.extractClientId(oldClientToken);
            }

            // 6. Generar nuevos tokens
            String newGeneralToken = jwtUtil.generateToken(username, clientName, clientDb, clientId);
            String newClientToken = jwtUtil.generateToken(username, newClientName, newClientDb, newClientId);

            log.info("Tokens renovados para: {} (General) y {} (Client)", username, newClientName);

            // 7. Configurar headers de respuesta (seguridad)
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");

            // 8. Devolver ambos tokens en la respuesta
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "token", newGeneralToken, // Token general renovado
                            "clientToken", newClientToken, // Token del cliente renovado
                            "message", "Tokens actualizados correctamente"));

        } catch (ExpiredJwtException ex) {
            log.error("⏳ Token expirado: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token expirado"));
        } catch (Exception ex) {
            log.error("❗ Error inesperado: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al renovar los tokens"));
        }
    }

    @GetMapping("/getId")
    public Integer getIdUser(@RequestParam String username) {
        return usuarioService.getIdUser(username);
    }
}
