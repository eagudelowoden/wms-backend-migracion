package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.services.ClienteServices.AccesorioService;

@RestController
@RequestMapping("api/accesorio")
public class AccesorioController extends BaseController<AccesorioModel, Integer> {

    public AccesorioController(AccesorioService service) {
        super(service);
    }

}
