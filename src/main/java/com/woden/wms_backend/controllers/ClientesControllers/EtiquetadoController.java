package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.EtiquetadoModel;
import com.woden.wms_backend.services.ClienteServices.EtiquetadoService;

@RestController
@RequestMapping("/client/etiquetado")
public class EtiquetadoController extends BaseController<EtiquetadoModel, Integer> {

  public EtiquetadoController(EtiquetadoService service) {
    super(service);
  }

  @Autowired
  private EtiquetadoService etiquetadoService;

  @PostMapping("/insertEtiquetado")
  public ResponseEntity<Integer> insertEtiquetado(@RequestBody List<EtiquetadoModel> etiquetados) {
    try {
      for (EtiquetadoModel etiquetado : etiquetados) {
        etiquetadoService.insertEtiquetado(etiquetado.getSerial(), etiquetado.getMac(), etiquetado.getVariable1(),
            etiquetado.getVariable2(), etiquetado.getVariable3(), etiquetado.getVariable4(),
            etiquetado.getReimpresion(), etiquetado.getUsuarioId(), etiquetado.getFecha());
      }
      return ResponseEntity.ok(1);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace(); // 👈 se mantiene simple sin log
      return ResponseEntity.badRequest().body(0);
    }
  }

}
