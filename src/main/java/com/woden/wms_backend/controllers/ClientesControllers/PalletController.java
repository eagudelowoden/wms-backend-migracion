package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.ClienteServices.PalletService;

@RestController
@RequestMapping("/api/pallet")
public class PalletController extends BaseController<PalletModel, Integer> {
    public PalletController(PalletService service) {
        super(service);
    }
}
