package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.services.ClienteServices.MaestroService;

@RestController
@RequestMapping("/api/maestro")
public class MaestroController extends BaseController<MaestroModel, Integer> {
    public MaestroController(MaestroService maestroService) {
        super(maestroService);
    }
}
