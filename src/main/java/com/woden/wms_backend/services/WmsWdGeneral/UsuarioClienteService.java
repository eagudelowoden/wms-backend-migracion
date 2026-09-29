package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioClientePerfilRepository;
import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioClienteRepository;

@Service
public class UsuarioClienteService {

    private final UsuarioClienteRepository usuarioClienteRepository;
    private final UsuarioClientePerfilRepository usuarioClientePerfilRepository;

    public UsuarioClienteService(UsuarioClienteRepository usuarioClienteRepository,
                                  UsuarioClientePerfilRepository usuarioClientePerfilRepository) {
        this.usuarioClienteRepository = usuarioClienteRepository;
        this.usuarioClientePerfilRepository = usuarioClientePerfilRepository;
    }

    public Integer obtenerIdUsuarioCliente(int usuarioId, String cliente) {
        return usuarioClienteRepository.getIdUserClient(usuarioId, cliente);
    }

    public List<Map<String, Object>> getAvailableClients(int usuarioId) {
        return mapRows(usuarioClienteRepository.searchAvailableUserClient(usuarioId));
    }

    public List<Map<String, Object>> getAssignedClients(int usuarioId) {
        return mapRows(usuarioClienteRepository.searchAggregatesUserClient(usuarioId));
    }

    @Transactional
    public void assignClients(int usuarioId, List<Integer> clienteIds) {
        for (int clienteId : clienteIds) {
            usuarioClienteRepository.insertUserClient(usuarioId, clienteId);
        }
    }

    @Transactional
    public void removeClients(int usuarioId, List<Integer> clienteIds) {
        for (int clienteId : clienteIds) {
            usuarioClienteRepository.deleteUserClient(usuarioId, clienteId);
        }
    }

    public List<Map<String, Object>> getAvailableProfiles(Integer usuarioClienteId, String cliente) {
        return mapRows(usuarioClientePerfilRepository.searchAvailableUserClientProfile(cliente, usuarioClienteId));
    }

    public List<Map<String, Object>> getAssignedProfiles(Integer usuarioClienteId) {
        return mapRows(usuarioClientePerfilRepository.searchAggregatesUserClientProfile(usuarioClienteId));
    }

    @Transactional
    public void assignProfiles(Integer usuarioClienteId, List<Integer> perfilIds) {
        for (int perfilId : perfilIds) {
            usuarioClientePerfilRepository.insertUserClientProfile(usuarioClienteId, perfilId);
        }
    }

    @Transactional
    public void removeProfiles(Integer usuarioClienteId, List<Integer> perfilIds) {
        for (int perfilId : perfilIds) {
            usuarioClientePerfilRepository.deleteUserClientProfile(usuarioClienteId, perfilId);
        }
    }

    private List<Map<String, Object>> mapRows(List<Object[]> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("nombre", row[1]);
            if (row.length > 2) {
                item.put("dbase", row[2]);
            }
            result.add(item);
        }
        return result;
    }
}