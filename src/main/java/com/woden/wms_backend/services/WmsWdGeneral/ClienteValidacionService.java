package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.clientDTO.ClienteValidacionTipoDto;
import com.woden.wms_backend.dto.clientDTO.ValidacionAsignadaDto;
import com.woden.wms_backend.dto.clientDTO.ValidacionClienteDto;
import com.woden.wms_backend.repositories.WmsWdGeneral.ClienteValidacionRepository;

@Service
public class ClienteValidacionService {

    @Autowired
    private ClienteValidacionRepository clienteValidacionRepository;

    public List<ValidacionClienteDto> getValidacionesByClienteId(Integer clienteId) {
        List<Object[]> rows = clienteValidacionRepository.getValidacionesByClienteId(clienteId);
        return rows.stream()
                .map(r -> new ValidacionClienteDto(
                        (String) r[0],
                        (Boolean) r[1]
                ))
                .collect(Collectors.toList());
    }

    public List<ClienteValidacionTipoDto> getDisponibles(Integer clienteId) {
        List<Object[]> rows = clienteValidacionRepository.getDisponibles(clienteId);
        return rows.stream()
                .map(r -> new ClienteValidacionTipoDto(
                        (Integer) r[0],
                        (String) r[1],
                        (String) r[2]
                ))
                .collect(Collectors.toList());
    }

    public List<ValidacionAsignadaDto> getAsignadas(Integer clienteId) {
        List<Object[]> rows = clienteValidacionRepository.getAsignadas(clienteId);
        return rows.stream()
                .map(r -> new ValidacionAsignadaDto(
                        (Integer) r[0],
                        (Integer) r[1],
                        (String) r[2],
                        (String) r[3],
                        (Boolean) r[4]
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void asignar(Integer clienteId, List<Integer> validacionTipoIds) {
        for (Integer id : validacionTipoIds) {
            clienteValidacionRepository.insertValidacion(clienteId, id);
        }
    }

    @Transactional
    public void remover(Integer clienteId, List<Integer> validacionTipoIds) {
        for (Integer id : validacionTipoIds) {
            clienteValidacionRepository.deleteValidacion(clienteId, id);
        }
    }

    @Transactional
    public void toggle(Integer clienteId, Integer validacionTipoId, Boolean activo) {
        clienteValidacionRepository.toggleValidacion(clienteId, validacionTipoId, activo);
    }

    /** true si el cliente tiene activa la validación con el código dado (ej. "TRUCKROLL"). */
    public boolean tieneValidacion(Integer clienteId, String codigo) {
        if (clienteId == null || codigo == null) return false;
        return getValidacionesByClienteId(clienteId).stream()
                .anyMatch(v -> codigo.equalsIgnoreCase(v.getCodigo()) && v.isActivo());
    }
}
