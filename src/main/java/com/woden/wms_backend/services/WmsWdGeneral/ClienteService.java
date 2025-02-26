package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.ClienteDTO;
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
}
