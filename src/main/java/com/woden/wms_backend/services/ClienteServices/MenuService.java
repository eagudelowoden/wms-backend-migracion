package com.woden.wms_backend.services.ClienteServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.MenuModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MenuRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MenuService extends BaseService<MenuModel, String> {
  @Autowired
  private MenuRepository menuRepository;

  public List<MenuModel> GetByIdPadre(String id_padre) {
    return menuRepository.GetByIdPadre(id_padre);
  }

  public List<MenuModel> GetByIdPadrePerfil(Integer idPerfil, String id) {
    return menuRepository.GetByIdPadrePerfil(idPerfil, id);
  }

  public List<MenuModel> GetAllMenus() {
    return menuRepository.GetAllMenus();
  }
}
