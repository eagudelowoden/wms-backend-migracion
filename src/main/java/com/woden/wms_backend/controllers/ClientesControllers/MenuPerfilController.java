package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.services.ClienteServices.MenuPerfilService;

@RestController
@RequestMapping("/client/menu-perfil")
public class MenuPerfilController {

    private final MenuPerfilService menuPerfilService;

    public MenuPerfilController(MenuPerfilService menuPerfilService) {
        this.menuPerfilService = menuPerfilService;
    }

    @GetMapping("/{perfilId}/grupos")
    public ResponseEntity<List<Map<String, Object>>> getGrupos(@PathVariable int perfilId) {
        return ResponseEntity.ok(menuPerfilService.getGrupos(perfilId));
    }

    @GetMapping("/{perfilId}/items/{idPadre}")
    public ResponseEntity<List<Map<String, Object>>> getItems(
            @PathVariable int perfilId, @PathVariable String idPadre) {
        return ResponseEntity.ok(menuPerfilService.getItems(perfilId, idPadre));
    }

    @PostMapping("/{perfilId}/sync")
    public ResponseEntity<Map<String, String>> syncMenus(
            @PathVariable int perfilId,
            @RequestBody Map<String, List<String>> body) {
        menuPerfilService.syncMenus(perfilId, body.get("menuIds"));
        return ResponseEntity.ok(Map.of("message", "Excepciones guardadas."));
    }
}
