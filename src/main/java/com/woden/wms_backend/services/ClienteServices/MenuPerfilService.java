package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.models.Entity.MenuPerfilModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MenuPerfilRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MenuPerfilService extends BaseService<MenuPerfilModel, Integer> {

    private final MenuPerfilRepository menuPerfilRepository;

    public MenuPerfilService(MenuPerfilRepository menuPerfilRepository) {
        this.menuPerfilRepository = menuPerfilRepository;
    }

    public List<Map<String, Object>> getGrupos(int perfilId) {
        String cliente = ClientDatabaseContext.getCurrentClientDb();
        List<Object[]> disponibles = menuPerfilRepository.getAvailableGrupos(perfilId, cliente);
        List<Object[]> asignados = menuPerfilRepository.getAssignedGrupos(perfilId, cliente);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : disponibles) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("descripcion", row[1]);
            item.put("asignado", false);
            result.add(item);
        }
        for (Object[] row : asignados) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("descripcion", row[1]);
            item.put("asignado", true);
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getItems(int perfilId, String idPadre) {
        String cliente = ClientDatabaseContext.getCurrentClientDb();
        List<Object[]> disponibles = menuPerfilRepository.getAvailableItems(perfilId, idPadre, cliente);
        List<Object[]> asignados = menuPerfilRepository.getAssignedItems(perfilId, idPadre, cliente);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : disponibles) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("descripcion", row[1]);
            item.put("asignado", false);
            result.add(item);
        }
        for (Object[] row : asignados) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("descripcion", row[1]);
            item.put("asignado", true);
            result.add(item);
        }
        return result;
    }

    @Transactional
    public void syncMenus(int perfilId, List<String> menuIds) {
        String cliente = ClientDatabaseContext.getCurrentClientDb();
        String ids = String.join(",", menuIds);
        menuPerfilRepository.syncMenuPerfil(perfilId, ids, cliente);
    }
}
