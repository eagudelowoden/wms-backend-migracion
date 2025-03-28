package com.woden.wms_backend.services.WmsWdGeneral;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.WmsWdGeneral.UsuarioClienteRepository;

@Service
public class UsuarioClienteService {
    
    private final UsuarioClienteRepository usuarioClienteRepository;

    public UsuarioClienteService(UsuarioClienteRepository usuarioClienteRepository) {
        this.usuarioClienteRepository = usuarioClienteRepository;
    }

    public Integer obtenerIdUsuarioCliente(int usuarioId, String cliente) {
        return usuarioClienteRepository.getIdUserClient(usuarioId, cliente);
    }
}
