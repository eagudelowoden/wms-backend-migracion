package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.EtiquetaCampoModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetaCampoService;

@RestController
@RequestMapping("/api/etiquetaCampo")
public class EtiquetaCampoController extends BaseController<EtiquetaCampoModel, Integer> {

  public EtiquetaCampoController(EtiquetaCampoService service) {
    super(service);
  }
  
  @Autowired
  private EtiquetaCampoService etiquetaCampoService;

  @GetMapping("/getListLabelField")
  public List<EtiquetaCampoModel> getListLabelField(@RequestParam Integer etiquetaId) {
    return etiquetaCampoService.getListLabelField(etiquetaId);
  }
}
