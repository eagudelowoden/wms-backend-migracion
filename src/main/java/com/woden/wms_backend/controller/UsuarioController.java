package com.woden.wms_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.woden.wms_backend.models.Usuario;
import com.woden.wms_backend.services.UsuarioService;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.getAll();
    }

    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.saveUser(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.getUserById(id);
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

    boolean valido = usuarioService.validateCredentials1(loginRequest.getNombreUsuario(), loginRequest.getClave());

    if (!valido) {
        response.put("message", "Credenciales incorrectas o usuario no encontrado");
        return ResponseEntity.status(401).body(response);
    }

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
    public List<Usuario> getUsersActive() {
        return usuarioService.getUserActive();
    }

}
