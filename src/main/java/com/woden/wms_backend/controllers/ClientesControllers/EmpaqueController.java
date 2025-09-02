package com.woden.wms_backend.controllers.ClientesControllers;


import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/client/empaque")
public class EmpaqueController extends BaseController<EmpaqueModel, Integer>{

    public EmpaqueController(EmpaqueService service) {super(service);}

    @Autowired
    private EmpaqueService empaqueService;

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private IngresoService ingresoService;


    @PostMapping("/insertPacking")
    public ResponseEntity<?> createEntity(@RequestBody EmpaqueModel requestBody) {
        Integer loteId = (requestBody.getLoteId() != null) ? requestBody.getLoteId() : 0;
        empaqueService.createEmpaque(
                requestBody.getSerialId(),
                requestBody.getSerial(),
                requestBody.getMac(),
                requestBody.getCodigoSapId(),
                requestBody.getPalletId(),
                requestBody.getCajaEmpaqueId(),
                requestBody.getNivelId(),
                requestBody.getUsuarioId(),
                requestBody.getFecha(),
                loteId,
                requestBody.getSmartCardId(),
                requestBody.getSmartCard()
        );
        return ResponseEntity.ok(1);
    }








    }





