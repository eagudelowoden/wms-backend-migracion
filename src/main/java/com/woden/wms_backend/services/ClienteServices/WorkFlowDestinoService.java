package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import org.springframework.stereotype.Service;

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
}
