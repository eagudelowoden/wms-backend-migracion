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

    @GetMapping("/BaseEmpaqueON/{id}")
    public int getBaseEmpaqueON(@PathVariable Integer id) {
        return clienteService.getBaseEmpaqueON(id);
    }

    @GetMapping("/SerialMasterCalidadON/{id}")
    public int getSerialMasterCalidadON(@PathVariable Integer id) {
        Integer value = clienteService.getSerialMasterCalidadON(id);
        return value != null ? value : 0;
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
  public ResponseEntity<Map<String, Object>> actualizarBaseEmpaque(
      @PathVariable int id,
      @RequestParam int baseEmpaqueON) {

    int updated = clienteService.updateBaseEmpaqueON(id, baseEmpaqueON);

    Map<String, Object> response = new HashMap<>();
    response.put("success", updated > 0);
    response.put("message", updated > 0
        ? "BaseEmpaqueON actualizado correctamente"
        : "No se actualizó ningún registro");

    return ResponseEntity.ok(response);
  }

  @GetMapping("/getEtiquetaUnitariaON/{id}")
  public ResponseEntity<Boolean> getEtiquetaUnitariaON(@PathVariable Integer id) {
    Boolean etiquetaUnitariaON = clienteService.getEtiquetaUnitariaON(id);
    return ResponseEntity.ok(etiquetaUnitariaON);
  }

  @GetMapping("/getSerialMasterON")
  public ResponseEntity<Integer> getSerialMasterON(@RequestParam Integer idCliente) {
    Integer v = clienteService.getSerialMasterON(idCliente);
    return ResponseEntity.ok(v != null ? v : 0);
  }

  @GetMapping("/getSerialMasterON/{id}")
  public ResponseEntity<Integer> getSerialMasterONById(@PathVariable Integer id) {
    Integer v = clienteService.getSerialMasterON(id);
    return ResponseEntity.ok(v != null ? v : 0);
  }
}
