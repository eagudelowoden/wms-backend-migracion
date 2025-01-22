package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.CodigoSapModel;
import com.woden.wms_backend.services.ClienteServices.CodigoSapService;

@RestController
@RequestMapping("/api/codigosap")
public class CodigoSapController extends BaseController<CodigoSapModel, Integer> {
        
    public CodigoSapController(CodigoSapService service) {
        super(service);
    }
}
