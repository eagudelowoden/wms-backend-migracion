package com.woden.wms_backend.controllers.ClientesControllers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.woden.wms_backend.models.Entity.EmpaqueModel;
import com.woden.wms_backend.services.ClienteServices.EmpaqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.dto.RegularizarLoteSerialDTO;
import com.woden.wms_backend.dto.RegularizarSapDTO;
import com.woden.wms_backend.dto.RegularizarSapSerialIngresoDTO;
import com.woden.wms_backend.dto.clientDTO.SendIngresoDTO;
import com.woden.wms_backend.dto.clientDTO.SendStorageEntryDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.services.ClienteServices.IngresoService;
import com.woden.wms_backend.services.ClienteServices.ZplService;
import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.EmpaqueModel;


@RestController
@RequestMapping("/client/empaque")
public class EmpaqueController extends BaseController<EmpaqueModel, Integer>{

    public EmpaqueController(EmpaqueService service) {super(service);}

    @Autowired
    private EmpaqueService empaqueService;





}
