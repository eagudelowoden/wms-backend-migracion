package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.repositories.ClienteRepositories.WorkFlowDestinoRepository;

@Service
public class WorkFlowDestinoService {

    private final WorkFlowDestinoRepository repository;

    public WorkFlowDestinoService(WorkFlowDestinoRepository repository) {
        this.repository = repository;
    }

    public List<Integer> obtenerDestinoId(String opcion, String origen, String descripcion, int tipologiaId) {
        return repository.getIdDestino(opcion, origen, descripcion, tipologiaId);
    }

    public List<String> getNombresDestinos(String opcion, String origen, String descripcion, int tipologiaId) {
        return repository.listarDestinosWorkflow(opcion, origen, descripcion, tipologiaId);
    }

    public List<Map<String, Object>> search(int workFlowId) {
        List<Object[]> results = repository.search(workFlowId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("nombre", row[1]);
            list.add(map);
        }
        return list;
    }

    @Transactional
    public void agregarDestinos(int workFlowId, List<Integer> destinoIds, String usuario, String cliente) {
        for (int destinoId : destinoIds) {
            repository.createDestino(workFlowId, destinoId);
        }
        String descripcion = String.format("Se agregaron %d destino(s) al Workflow %d", destinoIds.size(), workFlowId);
        repository.insertLog(usuario, cliente, descripcion);
    }

    @Transactional
    public void removerDestinos(int workFlowId, List<Integer> destinoIds, String usuario, String cliente) {
        for (int destinoId : destinoIds) {
            repository.deleteDestino(workFlowId, destinoId);
        }
        String descripcion = String.format("Se removieron %d destino(s) del Workflow %d", destinoIds.size(), workFlowId);
        repository.insertLog(usuario, cliente, descripcion);
    }
}