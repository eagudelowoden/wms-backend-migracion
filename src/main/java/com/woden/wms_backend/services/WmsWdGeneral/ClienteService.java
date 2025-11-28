package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class ClienteService extends BaseService<ClienteModel, Integer> {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<String> getListClient(int usuarioId) {
        return clienteRepository.getListClient(usuarioId);
    }

    public int getIdClient(String nombre) {
        return clienteRepository.getIdClient(nombre);
    }

    public Boolean getKitIngresoValue(int id) {
        return clienteRepository.getKitIngresoON(id);
    }

    public List<Map<String, Object>> getClientes() {
        // Ejecutar query nativa que retorna Object[]
        List<Object[]> results = clienteRepository.getClientes();

        // Mapear Object[] a Map<String, Object>
        List<Map<String, Object>> clientes = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> cliente = new HashMap<>();
            cliente.put("id", (Integer) result[0]);
            cliente.put("nombre", (String) result[1]);
            clientes.add(cliente);
        }
        return clientes;
    }
}
