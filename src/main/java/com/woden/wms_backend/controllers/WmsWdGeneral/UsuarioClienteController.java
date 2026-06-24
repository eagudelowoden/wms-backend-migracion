package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.UsuarioClienteService;

@RestController
@RequestMapping("/general/usuariocliente")
public class UsuarioClienteController {

    private final UsuarioClienteService usuarioClienteService;

    public UsuarioClienteController(UsuarioClienteService usuarioClienteService) {
        this.usuarioClienteService = usuarioClienteService;
    }

    @GetMapping("/id")
    public ResponseEntity<Map<String, Integer>> obtenerIdUsuarioCliente(
            @RequestParam int usuarioId,
            @RequestParam String cliente) {
        Integer id = usuarioClienteService.obtenerIdUsuarioCliente(usuarioId, cliente);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{usuarioId}/clientes/disponibles")
    public ResponseEntity<List<Map<String, Object>>> getAvailableClients(@PathVariable int usuarioId) {
        return ResponseEntity.ok(usuarioClienteService.getAvailableClients(usuarioId));
    }

    @GetMapping("/{usuarioId}/clientes/asignados")
    public ResponseEntity<List<Map<String, Object>>> getAssignedClients(@PathVariable int usuarioId) {
        return ResponseEntity.ok(usuarioClienteService.getAssignedClients(usuarioId));
    }

    @PostMapping("/{usuarioId}/clientes")
    public ResponseEntity<Map<String, String>> assignClients(
            @PathVariable int usuarioId,
            @RequestBody Map<String, List<Integer>> body) {
        usuarioClienteService.assignClients(usuarioId, body.get("clienteIds"));
        return ResponseEntity.ok(Map.of("message", "Clientes asignados."));
    }

    @DeleteMapping("/{usuarioId}/clientes")
    public ResponseEntity<Map<String, String>> removeClients(
            @PathVariable int usuarioId,
            @RequestBody Map<String, List<Integer>> body) {
        usuarioClienteService.removeClients(usuarioId, body.get("clienteIds"));
        return ResponseEntity.ok(Map.of("message", "Clientes removidos."));
    }

    @GetMapping("/{usuarioClienteId}/perfiles/disponibles")
    public ResponseEntity<List<Map<String, Object>>> getAvailableProfiles(
            @PathVariable Integer usuarioClienteId,
            @RequestParam String cliente) {
        return ResponseEntity.ok(usuarioClienteService.getAvailableProfiles(usuarioClienteId, cliente));
    }

    @GetMapping("/{usuarioClienteId}/perfiles/asignados")
    public ResponseEntity<List<Map<String, Object>>> getAssignedProfiles(@PathVariable Integer usuarioClienteId) {
        return ResponseEntity.ok(usuarioClienteService.getAssignedProfiles(usuarioClienteId));
    }

    @PostMapping("/{usuarioClienteId}/perfiles")
    public ResponseEntity<Map<String, String>> assignProfiles(
            @PathVariable Integer usuarioClienteId,
            @RequestBody Map<String, List<Integer>> body) {
        usuarioClienteService.assignProfiles(usuarioClienteId, body.get("perfilIds"));
        return ResponseEntity.ok(Map.of("message", "Perfiles asignados."));
    }

    @DeleteMapping("/{usuarioClienteId}/perfiles")
    public ResponseEntity<Map<String, String>> removeProfiles(
            @PathVariable Integer usuarioClienteId,
            @RequestBody Map<String, List<Integer>> body) {
        usuarioClienteService.removeProfiles(usuarioClienteId, body.get("perfilIds"));
        return ResponseEntity.ok(Map.of("message", "Perfiles removidos."));
    }
}