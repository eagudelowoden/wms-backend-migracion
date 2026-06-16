package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.ValidacionClienteDto;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteValidacionService;

@RestController
@RequestMapping("/general/validaciones-cliente")
public class ClienteValidacionController {

    @Autowired
    private ClienteValidacionService clienteValidacionService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<List<ValidacionClienteDto>> getValidaciones(@PathVariable Integer clienteId) {
        List<ValidacionClienteDto> validaciones = clienteValidacionService.getValidacionesByClienteId(clienteId);
        return ResponseEntity.ok(validaciones);
    }
}
