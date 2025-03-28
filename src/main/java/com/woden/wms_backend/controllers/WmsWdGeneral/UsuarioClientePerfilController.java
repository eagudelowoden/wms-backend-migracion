package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.WmsWdGeneral.UsuarioClientePerfilService;

@RestController
@RequestMapping("/api/usuarioclienteperfil")
public class UsuarioClientePerfilController {

    @Autowired
    private UsuarioClientePerfilService usuarioClientePerfilService;

    @GetMapping("/id")
    public ResponseEntity<Map<String, Integer>> getIdUsuarioClientePerfil(@RequestParam int usuarioClienteId) {
        int id = usuarioClientePerfilService.getIdUsuarioClientePerfil(usuarioClienteId);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}