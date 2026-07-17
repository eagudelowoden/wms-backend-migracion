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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<Map<String, String>> guardarUsuario(@RequestBody UsuarioModel usuario) {
        usuarioService.saveUser(usuario);
        return ResponseEntity.ok(Map.of("message", "Usuario creado."));
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

        String claveEncriptada = encryptUtil.encode(loginRequest.getClave());

        if (!usuario.getClave().equals(claveEncriptada)) {
            response.put("message", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(response);
        }

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

        log.info("Refres-token endpoint iniciado");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token general faltante o mal formado"));
        }
        String oldGeneralToken = authHeader.substring(7);

        String oldClientToken = null;
        if (clientTokenHeader != null && clientTokenHeader.startsWith("Bearer ")) {
            oldClientToken = clientTokenHeader.substring(7);
        }

        try {
            if (!jwtUtil.validateToken(oldGeneralToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token general no valido o expirado"));
            }

            String username = jwtUtil.extractUsername(oldGeneralToken);
            String clientDb = jwtUtil.extractClientDb(oldGeneralToken);
            String clientName = jwtUtil.extractClientName(oldGeneralToken);
            Integer clientId = jwtUtil.extractClientId(oldGeneralToken);

            String newClientName = clientName;
            String newClientDb = clientDb;
            Integer newClientId = clientId;

            if (oldClientToken != null && jwtUtil.validateToken(oldClientToken)) {
                newClientName = jwtUtil.extractClientName(oldClientToken);
                newClientDb = jwtUtil.extractClientDb(oldClientToken);
                newClientId = jwtUtil.extractClientId(oldClientToken);
            }

            String newGeneralToken = jwtUtil.generateToken(username, clientName, clientDb, clientId);
            String newClientToken = jwtUtil.generateToken(username, newClientName, newClientDb, newClientId);

            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "token", newGeneralToken,
                            "clientToken", newClientToken,
                            "message", "Tokens actualizados correctamente"));

        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token expirado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al renovar los tokens"));
        }
    }

    @GetMapping("/getId")
    public Integer getIdUser(@RequestParam String username) {
        return usuarioService.getIdUser(username);
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "OK - Backend funcionando");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(defaultValue = "") String term) {
        return ResponseEntity.ok(usuarioService.search(term));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable int id, @RequestBody Map<String, Object> body) {
        usuarioService.updateUser(id, body);
        return ResponseEntity.ok(Map.of("message", "Usuario actualizado."));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, String>> toggleActivo(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        int estado = body.getOrDefault("estado", 1);
        usuarioService.toggleActivo(id, estado);
        return ResponseEntity.ok(Map.of("message", "Estado actualizado."));
    }

    @PostMapping("/{id}/activate-web")
    public ResponseEntity<Map<String, String>> activateWeb(@PathVariable int id) {
        int statusCode = usuarioService.activateWeb(id);
        if (statusCode == 200) {
            return ResponseEntity.ok(Map.of("message", "Usuario activado en WEB!"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error al activar usuario web. Código: " + statusCode));
    }

    @DeleteMapping("/{id}/deactivate-web")
    public ResponseEntity<Map<String, String>> deactivateWeb(@PathVariable int id) {
        usuarioService.deactivateWeb(id);
        return ResponseEntity.ok(Map.of("message", "Usuario desactivado en WEB!"));
    }

    @GetMapping("/cargos")
    public ResponseEntity<List<Map<String, Object>>> getCargos() {
        return ResponseEntity.ok(usuarioService.getCargos());
    }

    @GetMapping("/areas")
    public ResponseEntity<List<Map<String, Object>>> getAreas() {
        return ResponseEntity.ok(usuarioService.getAreas());
    }

    @GetMapping("/cargos/id")
    public ResponseEntity<Map<String, Integer>> getCargoId(@RequestParam String nombre) {
        Integer id = usuarioService.getCargoIdByName(nombre);
        return id != null ? ResponseEntity.ok(Map.of("id", id)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/areas/id")
    public ResponseEntity<Map<String, Integer>> getAreaId(@RequestParam String nombre) {
        Integer id = usuarioService.getAreaIdByName(nombre);
        return id != null ? ResponseEntity.ok(Map.of("id", id)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/profile-for-client")
    public ResponseEntity<Map<String, Object>> getProfileForClient(
            @RequestParam int usuarioId, @RequestParam int clienteId) {
        Map<String, Object> profile = usuarioService.getProfileForClient(usuarioId, clienteId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }
}