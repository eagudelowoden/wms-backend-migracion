package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;

@RestController
@RequestMapping("api/ingreso")
public class IngresoController extends BaseController<IngresoModel, Integer> {

    public IngresoController(IngresoService service) {
        super(service);
    }    
}
