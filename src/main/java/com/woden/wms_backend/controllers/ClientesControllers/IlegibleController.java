package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.IlegibleModel;
import com.woden.wms_backend.services.ClienteServices.IlegibleService;

@RestController
@RequestMapping("/api/ilegible")
public class IlegibleController extends BaseController<IlegibleModel, Integer> {

    public IlegibleController(IlegibleService service) {
        super(service);
    }
}
