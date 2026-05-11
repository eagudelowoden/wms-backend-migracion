package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import com.woden.wms_backend.dto.PalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/buscar")
    public ResponseEntity<List<PalletDTO>> getPalletBusqueda(
            @RequestParam(name = "numero", required = false, defaultValue = "") String numero) {

        List<PalletDTO> pallets = posicionService.getPalletBusqueda(numero);

        if (pallets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(pallets, HttpStatus.OK);
    }

}
