package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.dto.clientDTO.ClienteValidacionTipoDto;
import com.woden.wms_backend.dto.clientDTO.ValidacionAsignadaDto;
import com.woden.wms_backend.dto.clientDTO.ValidacionClienteDto;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteValidacionService;

@RestController
@RequestMapping("/general/validaciones-cliente")
public class ClienteValidacionController {

    @Autowired
    private ClienteValidacionService clienteValidacionService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<List<ValidacionClienteDto>> getValidaciones(@PathVariable Integer clienteId) {
        List<ValidacionClienteDto> validaciones = clienteValidacionService.getValidacionesByClienteId(clienteId);
        return ResponseEntity.ok(validaciones);
    }

    @GetMapping("/{clienteId}/disponibles")
    public ResponseEntity<List<ClienteValidacionTipoDto>> getDisponibles(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(clienteValidacionService.getDisponibles(clienteId));
    }

    @GetMapping("/{clienteId}/asignadas")
    public ResponseEntity<List<ValidacionAsignadaDto>> getAsignadas(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(clienteValidacionService.getAsignadas(clienteId));
    }

    @PostMapping("/{clienteId}/asignar")
    public ResponseEntity<Map<String, String>> asignar(@PathVariable Integer clienteId, @RequestBody Map<String, List<Integer>> body) {
        clienteValidacionService.asignar(clienteId, body.get("validacionTipoIds"));
        return ResponseEntity.ok(Map.of("message", "Validaciones asignadas."));
    }

    @PostMapping("/{clienteId}/remover")
    public ResponseEntity<Map<String, String>> remover(@PathVariable Integer clienteId, @RequestBody Map<String, List<Integer>> body) {
        clienteValidacionService.remover(clienteId, body.get("validacionTipoIds"));
        return ResponseEntity.ok(Map.of("message", "Validaciones removidas."));
    }

    @PutMapping("/{clienteId}/toggle")
    public ResponseEntity<Map<String, String>> toggle(@PathVariable Integer clienteId, @RequestBody Map<String, Object> body) {
        Integer validacionTipoId = (Integer) body.get("validacionTipoId");
        Boolean activo = (Boolean) body.get("activo");
        clienteValidacionService.toggle(clienteId, validacionTipoId, activo);
        return ResponseEntity.ok(Map.of("message", "Estado actualizado."));
    }

    @GetMapping("/{clienteId}/tiene")
    public ResponseEntity<Map<String, Boolean>> tieneValidacion(@PathVariable Integer clienteId, @RequestParam String codigo) {
        boolean tiene = clienteValidacionService.tieneValidacion(clienteId, codigo);
        return ResponseEntity.ok(Map.of("tiene", tiene));
    }
}
