package com.woden.wms_backend.controllers.ClientesControllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.controllers.BaseController;
import com.woden.wms_backend.models.Entity.MenuModel;
import com.woden.wms_backend.services.ClienteServices.MenuService;

@RestController
@RequestMapping("/client/menu")
public class MenuController extends BaseController<MenuModel, String> {
  private final MenuService menuService;

  public MenuController(MenuService menuService) {
    super(menuService);
    this.menuService = menuService;
  }

  @GetMapping("/getByIdPadre")
  public ResponseEntity<List<MenuModel>> GetByIdPadre(@RequestParam String idPadre) {
    return ResponseEntity.ok(menuService.GetByIdPadre(idPadre));
  }

  @GetMapping("/getByIdPadrePerfil")
  public ResponseEntity<List<MenuModel>> GetByIdPadrePerfil(@RequestParam Integer idPerfil, @RequestParam String id) {
    return ResponseEntity.ok(menuService.GetByIdPadrePerfil(idPerfil, id));
  }

  @GetMapping("/getAllMenus")
  public ResponseEntity<List<MenuModel>> GetAllMenus() {
    return ResponseEntity.ok(menuService.GetAllMenus());
  }
}
