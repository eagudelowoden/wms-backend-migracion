package com.woden.wms_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Usuario;
import com.woden.wms_backend.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario saveUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUserById(int id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUserByNameUser(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public boolean deleteUser(int id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false); // Cambiar estado a inactivo
            usuarioRepository.save(usuario); // Guardar el cambio en la base de datos
            return true;
        }
        return false;
    }

    public boolean validateCredentials(String nombreUsuario, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        
        if (usuarioOpt.isEmpty()) {
            return false; // Usuario no encontrado
        }
    
        Usuario usuario = usuarioOpt.get();
        
        // Manejo de valores nulos
        if (usuario.getClave() == null || clave == null) {
            return false; // Contraseña inválida
        }
    
        return usuario.getClave().equals(clave);  // Comparación con el campo 'clave'
    }

    public boolean validateCredentials1(String nombreUsuario, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getClave().equals(clave)) {
            return true;
        }
    
        return false;
    }
    
    
    
    
    public List<Usuario> getUserActive() {
        return usuarioRepository.findByActivoTrue();
    }

}
