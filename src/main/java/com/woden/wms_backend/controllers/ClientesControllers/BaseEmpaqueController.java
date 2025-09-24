package com.woden.wms_backend.controllers.ClientesControllers;



import java.util.HashMap;
import java.util.Map;

import com.woden.wms_backend.dto.BaseEmpaqueDTO;
import com.woden.wms_backend.models.Entity.BaseEmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.BaseEmpaqueService;
import com.woden.wms_backend.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/baseEmpaque")
public class BaseEmpaqueController extends BaseController<BaseEmpaqueModel, Integer> {

    public BaseEmpaqueController(BaseEmpaqueService service) {super(service);}

    @Autowired
    private BaseEmpaqueService baseEmpaqueService;


    @GetMapping("/getModelBaseEmpaque")
    public ResponseEntity<?> getModelBaseEmpaque(@RequestParam String base,
                                                 @RequestParam String serial) {
        BaseEmpaqueDTO model = baseEmpaqueService.getModel(base, serial);
        // if (model == null) {
        //   return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Codigo Sap no encontrado");
        // }

        // and the baseIngreso object
        Map<String, Object> response = new HashMap<>();
        // response.put("model", model.getCodigoSap()); // Or any appropriate identifier
        response.put("baseEmpaque", model);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCountBase")
    public ResponseEntity<BaseEmpaqueDTO> getCountBase(@RequestParam String base) {
        BaseEmpaqueDTO model = baseEmpaqueService.getCountBase(base);

        if (model == null) {
            // 🚨 Caso: no hay registros
            BaseEmpaqueDTO empty = new BaseEmpaqueDTO();
            empty.setCantidad(0);
            return ResponseEntity.ok(empty);
        }

        return ResponseEntity.ok(model);
    }








}
