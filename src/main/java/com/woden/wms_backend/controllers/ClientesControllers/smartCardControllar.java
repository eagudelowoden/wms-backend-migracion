package com.woden.wms_backend.controllers.ClientesControllers;


import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.services.ClienteServices.SmartCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/smartCard")
public class smartCardControllar  extends BaseController<SmartCardModel, Integer> {

    public smartCardControllar(SmartCardService service){super(service);}

    @Autowired
    private  SmartCardService smartCardService;


    @GetMapping("/validateSmartCardInfo")
    public ResponseEntity<Boolean> validateSmartCardInfo(@RequestParam String smartCard) {
        boolean yaEmpacada = smartCardService.validateSmartCardInfo(smartCard);
        return ResponseEntity.ok(yaEmpacada);
    }
}
