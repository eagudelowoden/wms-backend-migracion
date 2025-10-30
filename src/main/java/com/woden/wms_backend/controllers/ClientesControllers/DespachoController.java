package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.models.Entity.DespachoModel;
import com.woden.wms_backend.services.ClienteServices.DespachoService;

@RestController
@RequestMapping("/client/despacho")
public class DespachoController extends BaseController<DespachoModel, Integer> {

  public DespachoController(DespachoService service) {
    super(service);
  }

  @Autowired
  private DespachoService despachoService;

  @PostMapping("/insertDispatch")
  public ResponseEntity<Integer> insertDispatch(@RequestBody List<DespachoModel> despachos,
      @RequestParam Integer estadoId,
      @RequestParam Integer usuarioId,
      @RequestParam String pedidoSap) {
    int resultadoGlobal = 1;

    try {
      for (DespachoModel despacho : despachos) {
        Integer cruce = despacho.getCruce() == true ? 1 : 0;
        Integer result = despachoService.insertDispatch(
            despacho.getId(),
            despacho.getSerial(),
            despacho.getMac(),
            despacho.getCodigoSapId(),
            despacho.getPalletId(),
            despacho.getPalletIdIngreso(),
            despacho.getCajaDespachoId(),
            estadoId,
            despacho.getTipoOrigenId(),
            despacho.getOrigenId(),
            despacho.getTipologiaId(),
            despacho.getNivelId(),
            despacho.getTramite(),
            despacho.getDocumento(),
            despacho.getGuia(),
            despacho.getFalla(),
            despacho.getPrealertaId(),
            cruce,
            despacho.getNovedad(),
            usuarioId,
            despacho.getFecha() != null ? despacho.getFecha().toString() : null,
            pedidoSap,
            despacho.getSmartCardId(),
            despacho.getSmartCard(),
            despacho.getLoteId(),
            despacho.getSerial3(),
            despacho.getCajaIngresoId(),
            despacho.getNumeroSmartcard(),
            despacho.getFallaCosmeticaId(),
            despacho.getFallaFuncionalId());
            System.out.println(despacho);

        // Si uno falla, devolvemos 0
        if (result == null || result == 0) {
          resultadoGlobal = 0;
        }
      }

      return ResponseEntity.ok(resultadoGlobal);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

  @PostMapping("/insertDispatchAccesory")
  public ResponseEntity<Integer> insertDispatchAccesory(@RequestBody List<AccesorioModel> accesorios,
      @RequestParam Integer estadoId,
      @RequestParam Integer usuarioId,
      @RequestParam String pedidoSap) {
    int resultadoGlobal = 1;

    try {
      for (AccesorioModel accesorio : accesorios) {
        Integer result = despachoService.insertDispatchAccesory(
            accesorio.getId(),
            accesorio.getCodigoSapId(),
            accesorio.getTipoAccesorio(),
            accesorio.getTipoOrigenId(),
            accesorio.getOrigenId(),
            accesorio.getPalletId(),
            estadoId,
            accesorio.getDocumento(),
            accesorio.getObservacion(),
            accesorio.getGuia(),
            accesorio.getFecha() != null ? accesorio.getFecha().toString() : null,
            usuarioId,
            accesorio.getCaja(),
            accesorio.getFechaLimpieza(),
            accesorio.getFechaEmpaque(),
            accesorio.getUsuarioLimpiezaId());
        if (result == null || result == 0) {
          resultadoGlobal = 0;
        }
      }

      return ResponseEntity.ok(resultadoGlobal);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
    }
  }

}
