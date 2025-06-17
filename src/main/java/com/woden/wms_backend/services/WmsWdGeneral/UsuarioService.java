package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.controllers.ClientesControllers.IngresoController;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioRepository;
import com.woden.wms_backend.util.EncryptUtil;
import com.woden.wms_backend.util.TypeMapper;;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private static final Logger log = LoggerFactory.getLogger(IngresoController.class);

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private EncryptUtil encryptUtil;

    public UsuarioModel saveUser(UsuarioModel usuario) {

        String claveEncriptada = encryptUtil.encode(usuario.getClave());
        usuario.setClave(claveEncriptada);
        return usuarioRepository.save(usuario);
    }

    public List<UsuarioModel> getAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> getUserById(int id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioModel> getUserByNameUser(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public boolean deleteUser(int id) {
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            UsuarioModel usuario = usuarioOpt.get();
            usuario.setActivo(false); // Cambiar estado a inactivo
            usuarioRepository.save(usuario); // Guardar el cambio en la base de datos
            return true;
        }
        return false;
    }

    public boolean validateCredentials(String nombreUsuario, String clave) {
        long start = System.currentTimeMillis();
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return false; // Usuario no encontrado
        }

        UsuarioModel usuario = usuarioOpt.get();

        // Manejo de valores nulos
        if (usuario.getClave() == null || clave == null) {
            System.out.println("Clave:" + usuario.getClave()); // Imprimir en consola
            long end = System.currentTimeMillis();
            log.info("Tiempo total en servicio: {} ms", (end - start));
            return false; // Contraseña inválida
        }

        long end = System.currentTimeMillis();
        log.info("Tiempo total en servicio: {} ms", (end - start));
        return usuario.getClave().equals(clave); // Comparación con el campo 'clave'
    }

    public boolean validateCredentials1(String nombreUsuario, String clave) {
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getClave().equals(clave)) {
            return true;
        }

        return false;
    }

    public List<UsuarioModel> getUserActive() {
        return usuarioRepository.findByActivoTrue();
    }

    public UsuarioModel getModel(String nombreUsuario, String clave) {
        List<Object[]> results = usuarioRepository.getModelUser(nombreUsuario, clave);

        if (results.isEmpty())
            return null;

        Object[] row = results.get(0);

        UsuarioModel usuario = new UsuarioModel();
        usuario.setId((Integer) row[0]);
        usuario.setIdentificacion((Long) row[1]);
        usuario.setNombres((String) row[2]);
        usuario.setApellidos((String) row[3]);
        usuario.setNombreUsuario((String) row[4]);
        usuario.setClave((String) row[5]);
        usuario.setFechaCreacion(row[6] != null ? row[6].toString() : null);
        usuario.setIp((String) row[7]);
        usuario.setActivo(TypeMapper.toBoolean(row[8]));
        usuario.setFechaUltimoAcceso(row[9] != null ? row[9].toString() : null);
        usuario.setCorreo((String) row[11]);
        usuario.setFechaNacimiento(row[12] != null ? row[12].toString() : null);
        usuario.setTemaId((Integer) row[13]); // O temaId según tu lógica
        usuario.setCargo((String) row[14]);
        usuario.setArea((String) row[15]);
        return usuario;
    }
}
