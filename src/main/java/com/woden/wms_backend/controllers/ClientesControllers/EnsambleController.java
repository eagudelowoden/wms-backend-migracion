package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.EnsambleModel;
import com.woden.wms_backend.services.ClienteServices.EnsambleService;

@RestController
@RequestMapping("/client/ensamble")
public class EnsambleController extends BaseController<EnsambleModel, Integer> {
  @Autowired
  private EnsambleService ensambleService;

  public EnsambleController(EnsambleService service) {
    super(service);
  }

  @PostMapping("/insertAssemble")
  public ResponseEntity<Integer> insertAssemble(@RequestBody EnsambleModel ensambleModel) {
    try {
      Integer result = ensambleService.insertAssemble(ensambleModel.getSerialId(), ensambleModel.getSerial(),
          ensambleModel.getSerial3(), ensambleModel.getSerial4(), ensambleModel.getSerial5(), ensambleModel.getMac(),
          ensambleModel.getCodigoSapId(), ensambleModel.getPalletId(), ensambleModel.getEstadoId(),
          ensambleModel.getTipologiaId(), ensambleModel.getNivelId(), ensambleModel.getUsuarioId(),
          ensambleModel.getUsuarioIdAsignado(), ensambleModel.getLoteId(), ensambleModel.getSmartCard());
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(0);
    }
  }

  @DeleteMapping("/deleteAssemble")
  public ResponseEntity<Integer> deleteAssemble(@RequestBody List<String> seriales) {
    try {
      Integer result = 0;
      result = ensambleService.deleteAssemble(seriales);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(0);
    }
  }

  @GetMapping("/getAssembleUser")
  public ResponseEntity<List<Map<String, Object>>> getAssembleUser(
      @RequestParam String perfil, @RequestParam Integer usuarioId) {
    try {
      return ResponseEntity.ok(ensambleService.getAssembleUser(perfil, usuarioId));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(null);
    }
  }
}
