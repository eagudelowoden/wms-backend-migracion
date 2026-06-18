package com.woden.wms_backend.services.WmsWdGeneral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.models.Entity.PerfilModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MenuPerfilRepository;
import com.woden.wms_backend.repositories.WmsWdGeneral.PerfilRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PerfilService extends BaseService<PerfilModel, Integer> {

  private final PerfilRepository perfilRepository;
  private final ClienteService clienteService;
  private final MenuPerfilRepository menuPerfilRepository;

  public PerfilService(PerfilRepository perfilRepository, ClienteService clienteService, MenuPerfilRepository menuPerfilRepository) {
    this.perfilRepository = perfilRepository;
    this.clienteService = clienteService;
    this.menuPerfilRepository = menuPerfilRepository;
  }

  public String getNameProfile(Integer usuarioClienteId) {
    return perfilRepository.getNameProfile(usuarioClienteId);
  }

  public List<Map<String, Object>> search(String nombre, String cliente) {
    if (cliente == null || cliente.isEmpty()) {
      cliente = ClientDatabaseContext.getCurrentClientName();
      if (cliente == null) cliente = "";
    }
    List<Object[]> rows = perfilRepository.searchProfile(nombre, cliente);
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] row : rows) {
      Map<String, Object> item = new HashMap<>();
      item.put("id", row[0]);
      item.put("nombre", row[1]);
      result.add(item);
    }
    return result;
  }

  @Transactional
  public Map<String, Object> create(String nombre, String cliente) {
    if (cliente == null || cliente.isEmpty()) {
      cliente = ClientDatabaseContext.getCurrentClientName();
      if (cliente == null) cliente = "";
    }
    Integer clienteId = clienteService.getIdClient(cliente);
    if (clienteId == null) clienteId = 0;
    perfilRepository.insertProfile(nombre, clienteId);
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Perfil creado.");
    return response;
  }

  @Transactional
  public Map<String, Object> update(int id, String nombre) {
    Integer clienteId = ClientDatabaseContext.getCurrentClientId();
    if (clienteId == null) clienteId = 0;
    perfilRepository.updateProfile(nombre, id, clienteId);
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Perfil actualizado.");
    return response;
  }

  @Transactional
  public Map<String, Object> delete(int id) {
    menuPerfilRepository.deleteByPerfilId(id);
    perfilRepository.deleteProfile(id);
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Perfil eliminado.");
    return response;
  }

  public List<Map<String, Object>> getAvailableSections(int perfilId) {
    return mapIdName(perfilRepository.searchAvailableSection(perfilId));
  }

  public List<Map<String, Object>> getAggregatedSections(int perfilId) {
    return mapIdName(perfilRepository.searchAggregatesSection(perfilId));
  }

  @Transactional
  public void assignSections(int perfilId, List<Integer> seccionIds) {
    for (int seccionId : seccionIds) {
      perfilRepository.insertProfileSection(perfilId, seccionId);
    }
  }

  @Transactional
  public void removeSections(int perfilId, List<Integer> seccionIds) {
    for (int seccionId : seccionIds) {
      perfilRepository.deleteProfileSection(perfilId, seccionId);
    }
  }

  public List<Map<String, Object>> getAvailableModules(int perfilId, int seccionId) {
    return mapIdName(perfilRepository.searchAvailableModule(perfilId, seccionId));
  }

  public List<Map<String, Object>> getAggregatedModules(int perfilId, int seccionId) {
    return mapIdName(perfilRepository.searchAggregatesModule(perfilId, seccionId));
  }

  @Transactional
  public void assignModules(int perfilId, List<Integer> moduloIds) {
    for (int moduloId : moduloIds) {
      perfilRepository.insertProfileModule(perfilId, moduloId);
    }
  }

  @Transactional
  public void removeModules(int perfilId, List<Integer> moduloIds) {
    for (int moduloId : moduloIds) {
      perfilRepository.deleteProfileModule(perfilId, moduloId);
    }
  }

  public List<Map<String, Object>> getAvailablePermisos(int perfilId, int moduloId) {
    return mapIdName(perfilRepository.searchAvailablePermit(moduloId, perfilId));
  }

  public List<Map<String, Object>> getAggregatedPermisos(int perfilId, int moduloId) {
    return mapIdName(perfilRepository.searchAggregatesPermit(perfilId, moduloId));
  }

  @Transactional
  public void assignPermisos(int perfilId, List<Integer> permisoIds) {
    for (int permisoId : permisoIds) {
      perfilRepository.insertProfilePermit(perfilId, permisoId);
    }
  }

  @Transactional
  public void removePermisos(int perfilId, List<Integer> permisoIds) {
    for (int permisoId : permisoIds) {
      perfilRepository.deleteProfilePermit(perfilId, permisoId);
    }
  }

  public List<Map<String, Object>> getAvailableTipoPerfiles(int perfilId) {
    return mapIdName(perfilRepository.searchAvailableProfileType(perfilId));
  }

  public List<Map<String, Object>> getAggregatedTipoPerfiles(int perfilId) {
    return mapIdName(perfilRepository.searchAggregatesProfileType(perfilId));
  }

  @Transactional
  public void assignTipoPerfiles(int perfilId, List<Integer> tipoPerfilIds) {
    for (int tipoPerfilId : tipoPerfilIds) {
      perfilRepository.insertPerfilTipoPerfil(perfilId, tipoPerfilId);
    }
  }

  @Transactional
  public void removeTipoPerfiles(int perfilId, List<Integer> tipoPerfilIds) {
    for (int tipoPerfilId : tipoPerfilIds) {
      perfilRepository.deletePerfilTipoPerfil(perfilId, tipoPerfilId);
    }
  }

  public List<Map<String, Object>> listTipoPerfil() {
    return mapIdName(perfilRepository.listTipoPerfil());
  }

  private List<Map<String, Object>> mapIdName(List<Object[]> rows) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] row : rows) {
      Map<String, Object> item = new HashMap<>();
      item.put("id", row[0]);
      item.put("nombre", row[1]);
      result.add(item);
    }
    return result;
  }
}
