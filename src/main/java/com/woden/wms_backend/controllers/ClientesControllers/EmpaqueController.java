package com.woden.wms_backend.controllers.ClientesControllers;


import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;


@RestController
@RequestMapping("/client/empaque")
public class EmpaqueController extends BaseController<EmpaqueModel, Integer>{

    public EmpaqueController(EmpaqueService service) {super(service);}

    // @Autowired
    // private EmpaqueService empaqueService;
}
