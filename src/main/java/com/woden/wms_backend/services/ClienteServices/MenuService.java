package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.MenuModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MenuRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.MenuModuloMapping;

@Service
public class MenuService extends BaseService<MenuModel, String> {
  @Autowired
  private MenuRepository menuRepository;

  public List<MenuModel> GetByIdPadre(String id_padre) {
    return menuRepository.GetByIdPadre(id_padre);
  }

  public List<MenuModel> GetByIdPadrePerfil(Integer idPerfil, String id) {
    Integer tiene = menuRepository.tieneValidacion(idPerfil, "USAR_MENUXPERFILES");
    if (tiene == null || tiene == 0) {
      List<MenuModel> all = menuRepository.GetByIdPadre(id);
      if ("0".equals(id)) {
        List<Integer> secciones = menuRepository.getAssignedSeccionIds(idPerfil);
        Set<String> allowedGroups = MenuModuloMapping.getGroupIds(secciones);
        return all.stream()
            .filter(m -> allowedGroups.contains(m.getId()))
            .collect(Collectors.toList());
      }
      List<Integer> modulos = menuRepository.getAssignedModuleIds(idPerfil);
      Set<String> allowed = MenuModuloMapping.getMenuIds(modulos);
      return all.stream()
          .filter(m -> allowed.contains(m.getId()) || !MenuModuloMapping.tieneMapping(m.getId()))
          .collect(Collectors.toList());
    }
    return menuRepository.GetByIdPadrePerfil(idPerfil, id);
  }

  public List<MenuModel> GetAllMenus() {
    return menuRepository.GetAllMenus();
  }
}
