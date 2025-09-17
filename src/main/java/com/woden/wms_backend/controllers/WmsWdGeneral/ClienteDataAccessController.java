package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woden.wms_backend.services.WmsWdGeneral.ClienteDataAccessService;

@RestController
@RequestMapping("/general/clientes-data-access")
public class ClienteDataAccessController {

  @Autowired
  private ClienteDataAccessService clienteService;

  @GetMapping("/tipo-origen/{id}")
  public ResponseEntity<Map<String, Integer>> getTipoOrigen(@PathVariable Integer id) {
    int tipoOrigen = clienteService.getTipoOrigenValue(id);
    Map<String, Integer> response = new HashMap<>();
    response.put("id", tipoOrigen);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/BaseIngresoOn/{id}")
  public ResponseEntity<Integer> getBaseIngresoOn(@PathVariable Integer id) {
    Integer baseIngresoOn = clienteService.getBaseIngresoON(id);
    return ResponseEntity.ok(baseIngresoOn);
  }

  @GetMapping("/BaseNoDisponibleOn/{id}")
  public ResponseEntity<Integer> getBaseNoDisponibleOn(@PathVariable Integer id) {
    Integer baseNoDisponibleOn = clienteService.getBaseNoDisponibleON(id);
    return ResponseEntity.ok(baseNoDisponibleOn);
  }

  @GetMapping("/getCalidadON/{id}")
  public ResponseEntity<Integer> getCalidadON(@PathVariable Integer id) {
    Integer calidadOn = clienteService.getCalidadON(id);
    return ResponseEntity.ok(calidadOn);
  }

  @GetMapping("/getNivelClasificacion/{id}")
  public ResponseEntity<Integer> getNivelClasificacionValue(@PathVariable Integer id) {
    Integer nivelClasificacionValue = clienteService.getNivelClasificacionValue(id);
    return ResponseEntity.ok(nivelClasificacionValue);
  }

  @GetMapping("/getOdooPqrsON/{id}")
  public ResponseEntity<Integer> getOdooPqrsON(@PathVariable Integer id) {
    Integer odooPqrsOn = clienteService.getOdooPqrsON(id);
    return ResponseEntity.ok(odooPqrsOn);
  }

    @GetMapping("/getsmartCardInfoON/{id}")
    public ResponseEntity<Integer> gesmartCardInfoON(@PathVariable Integer id) {
        Integer smartCardInfoON = clienteService.getsmartCardInfoON(id);
        return ResponseEntity.ok(smartCardInfoON);
    }

    @PostMapping("/updateBaseEmpaqueClient/{id}")
    public String actualizarBaseEmpaque(
            @PathVariable int id,
            @RequestParam int baseEmpaqueON) {

        int updated = clienteService.updateBaseEmpaqueON(id, baseEmpaqueON);
        return updated > 0
                ? "✅ BaseEmpaqueON actualizado correctamente"
                : "⚠️ No se actualizó ningún registro";
    }

}
