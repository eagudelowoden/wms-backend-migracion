package com.woden.wms_backend.controllers.ClientesControllers;


import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.SmartCardDTO;
import com.woden.wms_backend.models.Entity.SmartCardModel;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
import com.woden.wms_backend.services.ClienteServices.SmartCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.woden.wms_backend.repositories.ClienteRepositories.SmartCardRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/smartCard")
public class smartCardControllar  extends BaseController<SmartCardModel, Integer> {

    public smartCardControllar(SmartCardService service){super(service);}

    @Autowired
    private EmpaqueService empaqueService;
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
        empaqueService.updateSmartCard(estadoFinalId, fallaId, serial);

        return ResponseEntity.ok(1);
    }
    @PostMapping("/insertSmartCard")
    public ResponseEntity<?> createSmartCard(@RequestBody SmartCardDTO request) {
        try {
            // 1. Validación básica
            if (request.getSerial() == null || request.getSerial().isEmpty()) {
                return ResponseEntity.badRequest().body("El serial es obligatorio");
            }

            // 2. LLAMADA CORREGIDA: Pasa el objeto 'request' completo
            smartCardService.createSmartCard(request);

            // 3. Respuesta exitosa
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("mensaje", "SmartCard creada con éxito"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @PostMapping("/preasignarSmartCard")
    public ResponseEntity<?> preasignar(@RequestBody Map<String, Object> data) {
        String serial = (String) data.get("serial");
        Integer usuarioId = (Integer) data.get("usuarioId");

        Integer resultado = smartCardService.updateToPreasignado(serial, usuarioId);
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/getPreasignadas/{usuarioId}")
    public ResponseEntity<List<Map<String, Object>>> getPreasignadas(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(smartCardService.getPreasignadas(usuarioId));
    }
    @PostMapping("/updateEstadosSmartcard")
    public ResponseEntity<?> updateEstadosSmartcard(@RequestBody Map<String, Object> data) {
        Integer estadoFinalId = (Integer) data.get("estadoFinalId");
        Integer fallaId = (Integer) data.get("fallaId");
        String serial = (String) data.get("serial");

        Integer resultado = smartCardService.updateEstadosSmartcard(estadoFinalId, fallaId,serial);
        return ResponseEntity.ok(resultado);
    }

}
