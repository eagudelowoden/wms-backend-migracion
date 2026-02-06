package com.woden.wms_backend.controllers.ClientesControllers;


import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.SmartCardDTO;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.services.ClienteServices.SmartCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/updateSmartCard")
    public ResponseEntity<?> updateSmartCardEntry(@RequestBody Map<String, Object> smartcard) {
        Integer estadoFinalId = (Integer) smartcard.get("estadoFinalId");
        String fallaId = (String) smartcard.get("fallaId");
        String serial = (String) smartcard.get("serial");
        smartCardService.updateSmartCard(estadoFinalId, fallaId, serial);

        return ResponseEntity.ok(1);
    }
    @PostMapping("/insertSmartCard")
    public ResponseEntity<?> createSmartCard(@RequestBody SmartCardDTO request) {
        try {
            smartCardService.createSmartCard(
                    request.getSerialId(),
                    request.getSerial(),
                    request.getCodigoSapId(),
                    request.getUsuarioId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("SmartCard creada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
