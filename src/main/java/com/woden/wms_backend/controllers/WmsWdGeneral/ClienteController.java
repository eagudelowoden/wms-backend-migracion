package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController extends BaseController<ClienteModel, Integer> {
    private final ClienteService clienteService;

    public ClienteController(ClienteService service) {
        super(service);
        this.clienteService = service;
    }

    @GetMapping("/list/{usuarioId}")
    public List<String> getListClient(@PathVariable int usuarioId) {
        return clienteService.getListClient(usuarioId);
    }

    // Endpoint para obtener el ID del cliente por nombre
    @GetMapping("/id/{nombre}")
    public Integer getIdClient(@PathVariable String nombre) {
        return clienteService.getIdClient(nombre);
    }
}
