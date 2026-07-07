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

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private static final Logger log = LoggerFactory.getLogger(IngresoController.class);

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private EncryptUtil encryptUtil;

    public void saveUser(UsuarioModel usuario) {
        String claveEncriptada = encryptUtil.encode(usuario.getClave());
        usuarioRepository.insertUser(
            usuario.getIdentificacion(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getNombreUsuario(),
            claveEncriptada,
            usuario.getFechaNacimiento(),
            usuario.getCorreo(),
            usuario.getCargoId(),
            usuario.getAreaId()
        );
    }

    public List<UsuarioModel> getAll() {
        return usuarioRepository.findAll();
    }

    private Integer toInt(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) try { return Integer.parseInt((String) value); } catch (NumberFormatException e) { return null; }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) try { return Long.parseLong((String) value); } catch (NumberFormatException e) { return null; }
        return null;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        if (value instanceof String) return !"0".equals(value) && !"NO".equalsIgnoreCase((String) value);
        return null;
    }

    private UsuarioModel mapRow(Object[] row) {
        UsuarioModel u = new UsuarioModel();
        u.setId(toInt(row[0]));
        u.setIdentificacion(toLong(row[1]));
        u.setNombres(toString(row[2]));
        u.setApellidos(toString(row[3]));
        u.setNombreUsuario(toString(row[4]));
        u.setActivo(row.length > 5 ? toBoolean(row[5]) : null);
        u.setFechaNacimiento(toString(row[6]));
        u.setCorreo(toString(row[7]));
        u.setCargo(toString(row[8]));
        u.setArea(toString(row[9]));
        return u;
    }

    public Optional<UsuarioModel> getUserById(int id) {
        List<String> usuarios = usuarioRepository.getNombreUsuarioById(id);
        if (usuarios.isEmpty()) return Optional.empty();
        List<Object[]> rows = usuarioRepository.searchEditUser(usuarios.get(0));
        for (Object[] row : rows) {
            Integer rowId = toInt(row[0]);
            if (rowId != null && rowId == id) {
                return Optional.of(mapRow(row));
            }
        }
        return Optional.empty();
    }

    public Optional<UsuarioModel> getUserByNameUser(String nombreUsuario) {
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent() && !Boolean.TRUE.equals(usuarioOpt.get().getActivo())) {
            return Optional.empty();
        }
        return usuarioOpt;
    }

    public boolean deleteUser(int id) {
        List<String> usuarios = usuarioRepository.getNombreUsuarioById(id);
        if (usuarios.isEmpty()) return false;
        usuarioRepository.innactivateUser(id, 0);
        return true;
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
        usuario.setId(toInt(row[0]));
        usuario.setIdentificacion(toLong(row[1]));
        usuario.setNombres(toString(row[2]));
        usuario.setApellidos(toString(row[3]));
        usuario.setNombreUsuario(toString(row[4]));
        usuario.setClave(toString(row[5]));
        usuario.setFechaCreacion(toString(row[6]));
        usuario.setIp(toString(row[7]));
        usuario.setActivo(toBoolean(row[8]));
        usuario.setFechaUltimoAcceso(toString(row[9]));
        usuario.setCorreo(toString(row[11]));
        usuario.setFechaNacimiento(toString(row[12]));
        usuario.setTemaId(toInt(row[13]));
        usuario.setCargo(toString(row[14]));
        usuario.setArea(toString(row[15]));
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
            item.put("fechaNacimiento", row.length > 6 ? row[6] : null);
            item.put("correo", row.length > 7 ? row[7] : null);
            item.put("cargo", row.length > 8 ? row[8] : null);
            item.put("area", row.length > 9 ? row[9] : null);
            boolean activo = true;
            if (row.length > 5 && row[5] != null) {
                Object val = row[5];
                if (val instanceof Boolean) {
                    activo = (Boolean) val;
                } else if (val instanceof Number) {
                    activo = ((Number) val).intValue() != 0;
                } else {
                    activo = !"0".equals(val.toString());
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
            List<String> claves = usuarioRepository.getClaveById(id);
            clave = claves.isEmpty() ? "" : claves.get(0);
        } else {
            clave = encryptUtil.encode(clave);
        }
        Long identificacion = body.get("identificacion") != null ? toLong(body.get("identificacion")) : 0L;
        usuarioRepository.updateUser(
            identificacion,
            (String) body.get("nombres"),
            (String) body.get("apellidos"),
            (String) body.get("nombreUsuario"),
            clave,
            (String) body.get("fechaNacimiento"),
            (String) body.get("correo"),
            body.get("cargoId") != null ? toInt(body.get("cargoId")) : null,
            body.get("areaId") != null ? toInt(body.get("areaId")) : null,
            id
        );
        if (body.containsKey("activo")) {
            Boolean activo = toBoolean(body.get("activo"));
            if (activo != null) usuarioRepository.innactivateUser(id, activo ? 1 : 0);
        }
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
        return toInt(rows.get(0)[0]);
    }

    public Integer getAreaIdByName(String nombre) {
        List<Object[]> rows = usuarioRepository.getIdArea(nombre);
        if (rows.isEmpty()) return null;
        return toInt(rows.get(0)[0]);
    }

    public Map<String, Object> getProfileForClient(int usuarioId, int clienteId) {
        List<Object[]> rows = usuarioRepository.getProfileForClient(usuarioId, clienteId);
        if (rows.isEmpty()) return null;
        Object[] row = rows.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("id", row[0]);
        map.put("nombres", row[1]);
        map.put("nombreUsuario", row[2]);
        map.put("perfilId", row[3]);
        return map;
    }
}