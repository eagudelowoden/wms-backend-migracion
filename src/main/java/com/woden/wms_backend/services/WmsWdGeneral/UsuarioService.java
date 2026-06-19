package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.controllers.ClientesControllers.IngresoController;
import com.woden.wms_backend.models.WmsWdGeneral.UsuarioModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioRepository;
import com.woden.wms_backend.util.EncryptUtil;
import com.woden.wms_backend.util.TypeMapper;

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
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    public boolean validateCredentials(String nombreUsuario, String clave) {
        long start = System.currentTimeMillis();
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        UsuarioModel usuario = usuarioOpt.get();

        if (usuario.getClave() == null || clave == null) {
            long end = System.currentTimeMillis();
            log.info("Tiempo total en servicio: {} ms", (end - start));
            return false;
        }

        long end = System.currentTimeMillis();
        log.info("Tiempo total en servicio: {} ms", (end - start));
        return usuario.getClave().equals(clave);
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
        usuario.setTemaId((Integer) row[13]);
        usuario.setCargo((String) row[14]);
        usuario.setArea((String) row[15]);
        return usuario;
    }

    public List<String> getListUser(Integer idCliente, String tipoPerfil) {
        List<Object[]> results = usuarioRepository.getListUser(idCliente, tipoPerfil);
        return results.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
    }

    public Integer getIdUser(String nombreUsuario) {
        return usuarioRepository.getIdUser(nombreUsuario);
    }

    public List<Map<String, Object>> search(String term) {
        String searchTerm = (term == null || term.trim().isEmpty()) ? "" : term.trim();
        List<Object[]> rows = usuarioRepository.searchEditUser(searchTerm);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("identificacion", row[1]);
            item.put("nombres", row[2]);
            item.put("apellidos", row[3]);
            item.put("nombreUsuario", row[4]);
            item.put("fechaNacimiento", row.length > 5 ? row[5] : null);
            item.put("correo", row.length > 6 ? row[6] : null);
            item.put("cargo", row.length > 7 ? row[7] : null);
            item.put("area", row.length > 8 ? row[8] : null);
            boolean activo = true;
            if (row.length > 10 && row[10] != null) {
                Object val = row[10];
                if (val instanceof Number) {
                    activo = ((Number) val).intValue() != 0;
                } else {
                    activo = !"0".equals(val.toString()) && !"NO".equalsIgnoreCase(val.toString());
                }
            }
            item.put("activo", activo);
            result.add(item);
        }
        return result;
    }

    public void updateUser(int id, Map<String, Object> body) {
        String clave = body.containsKey("clave") ? (String) body.get("clave") : "";
        if (clave == null || clave.isEmpty()) {
            Optional<UsuarioModel> existing = usuarioRepository.findById(id);
            clave = existing.map(UsuarioModel::getClave).orElse("");
        }
        Long identificacion = body.get("identificacion") != null ? ((Number) body.get("identificacion")).longValue() : 0L;
        usuarioRepository.updateUser(
            identificacion,
            (String) body.get("nombres"),
            (String) body.get("apellidos"),
            (String) body.get("nombreUsuario"),
            clave,
            (String) body.get("fechaNacimiento"),
            (String) body.get("correo"),
            body.get("cargoId") != null ? ((Number) body.get("cargoId")).intValue() : null,
            body.get("areaId") != null ? ((Number) body.get("areaId")).intValue() : null,
            id
        );
    }

    public void toggleActivo(int id, int estado) {
        usuarioRepository.innactivateUser(id, estado);
    }

    public List<Map<String, Object>> getCargos() {
        List<Object[]> rows = usuarioRepository.getListPosition();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", row[0]);
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getAreas() {
        List<Object[]> rows = usuarioRepository.getListArea();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", row[0]);
            result.add(item);
        }
        return result;
    }

    public Integer getCargoIdByName(String nombre) {
        List<Object[]> rows = usuarioRepository.getIdPosition(nombre);
        if (rows.isEmpty()) return null;
        return ((Number) rows.get(0)[0]).intValue();
    }

    public Integer getAreaIdByName(String nombre) {
        List<Object[]> rows = usuarioRepository.getIdArea(nombre);
        if (rows.isEmpty()) return null;
        return ((Number) rows.get(0)[0]).intValue();
    }
}