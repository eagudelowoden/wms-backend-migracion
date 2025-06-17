package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.services.ClienteServices.PosicionService;

@RestController
@RequestMapping("/client/posicion")
public class PosicionController extends BaseController<PosicionModel, Integer> {
    public PosicionController(PosicionService service) {
        super(service);
    }

    @Autowired
    private PosicionService posicionService;

    @GetMapping("/getList/{reservado}")
    public List<String> getList(@PathVariable Integer reservado) {
        return posicionService.getList(reservado);
    }

    @GetMapping("/getId/{numero}")
    public Integer getId(@PathVariable String numero) {
        return posicionService.getId(numero);
    }
}
