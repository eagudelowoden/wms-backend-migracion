package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.LoginRequest;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.security.JwtUtil;
import com.woden.wms_backend.services.WmsWdGeneral.UsuarioService;
import com.woden.wms_backend.util.EncryptUtil;

@RestController
@RequestMapping("/general/users")
public class UsuarioController {

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

}
