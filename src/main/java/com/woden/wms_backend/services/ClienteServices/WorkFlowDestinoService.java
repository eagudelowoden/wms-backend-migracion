package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.WorkFlowDestinoRepository;

@Service
public class WorkFlowDestinoService {

    private final WorkFlowDestinoRepository repository;

    public WorkFlowDestinoService(WorkFlowDestinoRepository repository) {
        this.repository = repository;
    }

    public Integer obtenerDestinoId(String origen, String opcion, String descripcion, int tipologiaId) {
        return repository.getIdDestino(origen, opcion, descripcion, tipologiaId);
    }
}
