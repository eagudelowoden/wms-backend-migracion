package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.models.LoginRequest;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.services.WmsWdGeneral.UsuarioService;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> iniciarSesion(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        if (loginRequest.getNombreUsuario() == null || loginRequest.getClave() == null) {
            response.put("message", "Nombre de usuario o contraseña no proporcionados");
            return ResponseEntity.badRequest().body(response);
        }

        // boolean valido =
        // usuarioService.validateCredentials1(loginRequest.getNombreUsuario(),
        // loginRequest.getClave());

        // if (!valido) {
        // response.put("message", "Credenciales incorrectas o usuario no encontrado");
        // return ResponseEntity.status(401).body(response);
        // }

        Optional<UsuarioModel> usuarioOpt = usuarioService.getUserByNameUser(loginRequest.getNombreUsuario());

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getClave().equals(loginRequest.getClave())) {
            response.put("message", "Credenciales incorrectas o usuario no encontrado");
            return ResponseEntity.status(401).body(response);
        }

        UsuarioModel usuario = usuarioOpt.get();

        response.put("message", "Inicio de sesión exitoso");
        response.put("usuarioId", usuario.getId().toString()); // Enviar el ID del usuario

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
}
