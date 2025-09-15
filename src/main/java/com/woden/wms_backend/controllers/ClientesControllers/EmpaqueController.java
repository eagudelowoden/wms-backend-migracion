package com.woden.wms_backend.controllers.ClientesControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private static final Logger logger = LoggerFactory.getLogger(EmpaqueController.class);

    @DeleteMapping("/eliminarSeriesEmpaque")
    public ResponseEntity<Integer> eliminarSeriesEmpaque(@RequestBody List<String> seriales) {
        if (seriales == null || seriales.isEmpty()) {
  //          logger.warn("⚠️ Se llamó a eliminarSeriesEmpaque pero la lista de seriales estaba vacía o nula");
        } else {
        //    logger.info("🗑️ Se van a eliminar los seriales: {}", seriales);
        }

        int status = empaqueService.eliminarSeriesEmpaque(seriales);

        //logger.info("✅ Resultado de eliminarSeriesEmpaque: {}", status);
        return ResponseEntity.ok(status);
    }


    @PostMapping("/updateSmartCard")
    public ResponseEntity<?> updateSmartCard(@RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        List<Map<String, Object>> ingresos = requestBody.get("ingresos");
        for (Map<String, Object> ingreso : ingresos) {
            Integer smartCardId = (Integer) ingreso.get("smartCardId");
            String smartCardNuevo = (String) ingreso.get("smartCardNuevo");
            String serial = (String) ingreso.get("serial");
            empaqueService.updateSmartCard(smartCardId, smartCardNuevo,serial);
        }
        return ResponseEntity.ok(1);
    }

    /*
    @PostMapping("/updatePacking")
    public ResponseEntity<?> UpdatePacking(@RequestBody List<Map<String, Object>> ingresos) {
        for (Map<String, Object> ingreso : ingresos) {
            Integer serialId = (Integer) ingreso.get("serialId");
            String serialNuevo = (String) ingreso.get("serialNuevo");
            String mac = (String) ingreso.get("mac");
            String serialAnterior = (String) ingreso.get("serialAnterior");
            empaqueService.UpdatePacking(serialId, serialNuevo, mac, serialAnterior);
        }
        return ResponseEntity.ok(1);
    }
*/
    @PostMapping("/updatePacking")
    public ResponseEntity<?> updatePacking(@RequestBody Map<String, Object> empaque) {
        Integer serialId = ((Number) empaque.get("serialId")).intValue();
        String serialNuevo = (String) empaque.get("serialNuevo");
        String mac = (String) empaque.get("mac");
        String serialAnterior = (String) empaque.get("serialAnterior");
        empaqueService.UpdatePacking(serialId, serialNuevo,mac,serialAnterior);
        return ResponseEntity.ok(1);
    }













    }





