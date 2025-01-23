package com.woden.wms_backend.controllers.ClientesControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.LoteModel;
import com.woden.wms_backend.services.ClienteServices.LoteService;

@RestController
@RequestMapping("/api/lote")
public class LoteController extends BaseController<LoteModel, Integer> {
    public LoteController(LoteService loteService) {
        super(loteService);
    }
}
