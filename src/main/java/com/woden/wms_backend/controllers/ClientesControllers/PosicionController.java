package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.services.ClienteServices.PosicionService;

@RestController
@RequestMapping("/api/posicion")
public class PosicionController extends BaseController<PosicionModel, Integer> {
    public PosicionController(PosicionService service) {
        super(service);
    }
}
