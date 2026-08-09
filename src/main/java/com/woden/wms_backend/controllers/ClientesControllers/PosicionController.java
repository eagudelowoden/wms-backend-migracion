package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import com.woden.wms_backend.dto.PalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woden.wms_backend.models.Entity.PosicionModel;
import com.woden.wms_backend.services.ClienteServices.PosicionService;

@RestController
@RequestMapping("/client/posicion")
public class PosicionController {

    @Autowired
    private PosicionService posicionService;

    @GetMapping
    public ResponseEntity<List<PosicionModel>> getAll() {
        return ResponseEntity.ok(posicionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PosicionModel> getById(@PathVariable Integer id) {
        PosicionModel model = posicionService.getById(id);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(model);
    }

    @GetMapping("/getList/{reservado}")
    public List<String> getList(@PathVariable Integer reservado) {
        return posicionService.getList(reservado);
    }

    @GetMapping("/getId/{numero}")
    public Integer getId(@PathVariable String numero) {
        return posicionService.getId(numero);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PalletDTO>> getPalletBusqueda(
            @RequestParam(name = "numero", required = false, defaultValue = "") String numero) {
        List<PalletDTO> pallets = posicionService.getPalletBusqueda(numero);
        if (pallets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pallets);
    }

    @GetMapping("/listado")
    public ResponseEntity<List<Map<String, Object>>> listado(
            @RequestParam(name = "numero", required = false, defaultValue = "") String numero) {
        return ResponseEntity.ok(posicionService.buscarListado(numero));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(posicionService.getListEnsamble());
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Integer> count(@PathVariable Integer id) {
        return ResponseEntity.ok(posicionService.getCountPosition(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> save(@RequestBody PosicionModel posicion) {
        posicionService.crear(posicion.getNumero(), posicion.getCodigoSapId());
        return ResponseEntity.ok(Map.of("message", "Posición creada."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Integer id, @RequestBody PosicionModel posicion) {
        posicionService.actualizar(id, posicion.getNumero(), posicion.getCodigoSapId());
        return ResponseEntity.ok(Map.of("message", "Posición actualizada."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer id) {
        posicionService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Posición eliminada."));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, String>> toggle(@PathVariable Integer id,
            @RequestParam Integer estado) {
        posicionService.toggle(id, estado);
        return ResponseEntity.ok(Map.of("message", "Estado actualizado."));
    }
}
