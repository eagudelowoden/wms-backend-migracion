package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.services.ClienteServices.MaestroService;

@RestController
@RequestMapping("/api/maestros")
public class MaestroController extends BaseController<MaestroModel, Integer> {

    private final MaestroService maestroService;

    public MaestroController(MaestroService maestroService) {
        super(maestroService);
        this.maestroService = maestroService;
    }

    @GetMapping("/tipologias")
    public List<String> getTipologias(
            @RequestParam String desc1,
            @RequestParam String desc2,
            @RequestParam String desc3,
            @RequestParam String desc4) {
        return maestroService.obtenerTipologias(desc1, desc2, desc3, desc4);
    }

    @GetMapping("/tipoMaestro/{tipoMaestroId}")
    public List<MaestroModel> getByTipoMaestroId(@PathVariable int tipoMaestroId) {
        return maestroService.getByTipoMaestroId(tipoMaestroId);
    }

}
