package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.CerrarPalletDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.services.ClienteServices.AccesorioService;

@RestController
@RequestMapping("api/accesorio")
public class AccesorioController extends BaseController<AccesorioModel, Integer> {

    public AccesorioController(AccesorioService service) {
        super(service);
    }

    @Autowired
    private AccesorioService accesorioService;
    @PostMapping("/createAccesory")
    public ResponseEntity<Map<String, String>> guardarAccesorios(@RequestBody List<AccesorioModel> accesorios) {
        accesorioService.guardarAccesorios(accesorios);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registros guardados exitosamente.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchAccesory/{palletId}")
    public ResponseEntity<List<AccesorioSearchDTO>> buscarAccesorios(@PathVariable int palletId) {
        List<AccesorioSearchDTO> lista = accesorioService.buscarAccesoriosPorPallet(palletId);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarAccesorio")
    public ResponseEntity<Integer> eliminarAccesorio(
            @RequestParam int palletId,
            @RequestParam int cantidad,
            @RequestParam int codigoSapId) {
        int status = accesorioService.eliminarAccesorio(palletId, cantidad, codigoSapId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/cerrar-accesorio")
    public ResponseEntity<Map<String, String>> cerrarPalletAccesorio(@RequestBody CerrarPalletDTO dto) {
        Map<String, String> response = new HashMap<>();
        System.out.println("Cerrar accesorio - PalletId: " + dto);
        response.put("message", "Pallet cerrado correctamente");
        System.out.println("Cerrar accesorio - PalletId: " + dto.getPalletId()
                + ", Estado: " + dto.getEstado()
                + ", DestinoId: " + dto.getDestinoId()
                + ", TipologiaId: " + dto.getTipologiaId()
                + ", PosicionId: " + dto.getPosicionId());
        try {
            accesorioService.cerrarPalletAccesorio(dto);

            System.out.println("Cerrar accesorio - PalletId: " + dto);
            response.put("message", "Pallet cerrado correctamente");
            System.out.println("Cerrar accesorio - PalletId: " + dto.getPalletId()
                    + ", Estado: " + dto.getEstado()
                    + ", DestinoId: " + dto.getDestinoId()
                    + ", TipologiaId: " + dto.getTipologiaId()
                    + ", PosicionId: " + dto.getPosicionId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error al cerrar el pallet: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
