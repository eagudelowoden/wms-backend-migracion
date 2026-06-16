package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
