package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.TipoMaestroModel;
import com.woden.wms_backend.services.ClienteServices.TipoMaestroService;

@RestController
@RequestMapping("/api/tipoMaestro")
public class TipoMaestroController extends BaseController<TipoMaestroModel, Integer> {
    public TipoMaestroController(TipoMaestroService service) {
        super(service);
    }
}
