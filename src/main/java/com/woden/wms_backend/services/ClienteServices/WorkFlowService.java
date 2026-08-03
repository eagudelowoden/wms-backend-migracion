package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.repositories.ClienteRepositories.WorkFlowRepository;
import com.woden.wms_backend.repositories.WmsWdGeneral.EstadoRepository;

@Service
public class WorkFlowService {
  @Autowired
  private WorkFlowRepository repository;

  @Autowired
  private EstadoRepository estadoRepository;

  public Integer getLevelId(String opcion, String origen, String descripcion, int tipologiaId) {
    return repository.getLevelId(opcion, origen, descripcion, tipologiaId);
  }

  public List<Map<String, Object>> search() {
    List<Object[]> results = repository.search();
    List<Map<String, Object>> list = new ArrayList<>();
    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("modulo", row[1]);
      map.put("origen", row[2]);
      map.put("opcion", row[3]);
      map.put("descripcion", row[4]);
      map.put("tipologia", row[5]);
      map.put("nivel", row[6]);
      list.add(map);
    }
    return list;
  }

  public List<Map<String, Object>> searchConDestinos() {
    List<Map<String, Object>> list = search();
    Map<Integer, List<String>> destinosByWorkflow = new HashMap<>();
    for (Object[] row : repository.searchDestinos()) {
      Integer workflowId = ((Number) row[0]).intValue();
      destinosByWorkflow.computeIfAbsent(workflowId, k -> new ArrayList<>()).add((String) row[1]);
    }
    for (Map<String, Object> map : list) {
      Integer id = ((Number) map.get("id")).intValue();
      map.put("destinos", destinosByWorkflow.getOrDefault(id, new ArrayList<>()));
    }
    return list;
  }

  @Transactional
  public void create(int moduloId, int origenId, int opcionId, int tipologiaId, int nivelId,
                     String usuario, String cliente) {
    repository.create(moduloId, origenId, opcionId, tipologiaId, nivelId);
    String descripcion = String.format(
      "Se creó el Workflow (ModuloId: %d, OrigenId: %d, OpcionId: %d, TipologiaId: %d, NivelId: %d)",
      moduloId, origenId, opcionId, tipologiaId, nivelId);
    repository.insertLog(usuario, cliente, descripcion);
  }

  @Transactional
  public void update(int id, int moduloId, int origenId, int opcionId, int tipologiaId, int nivelId,
                     String usuario, String cliente) {
    repository.updateWorkflow(moduloId, origenId, opcionId, tipologiaId, nivelId, id);
    String descripcion = String.format(
      "Se actualizó el Workflow (ModuloId: %d, OrigenId: %d, OpcionId: %d, TipologiaId: %d, NivelId: %d)",
      moduloId, origenId, opcionId, tipologiaId, nivelId);
    repository.insertLog(usuario, cliente, descripcion);
  }

  @Transactional
  public void delete(int id, String usuario, String cliente) {
    repository.deleteWorkflow(id);
    String descripcion = String.format("Se eliminó el Workflow (Id: %d)", id);
    repository.insertLog(usuario, cliente, descripcion);
  }

  public List<String> searchModulos() {
    return estadoRepository.searchModulos();
  }

  public List<Map<String, Object>> searchEstados() {
    List<Object[]> results = estadoRepository.searchEstados();
    List<Map<String, Object>> list = new ArrayList<>();
    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("nombre", row[1]);
      list.add(map);
    }
    return list;
  }

  public List<String> searchEstadosByModulo(String moduloNombre) {
    return estadoRepository.searchEstadosByModulo(moduloNombre);
  }

  public List<String> getDescripciones(String moduloNombre) {
    return estadoRepository.getDescripciones(moduloNombre);
  }

  public List<String> getOpciones(String moduloNombre, String descripcion) {
    return estadoRepository.getOpciones(moduloNombre, descripcion);
  }

  public Integer getPermisoId(String modulo, String nombre) {
    return estadoRepository.getPermisoId(modulo, nombre);
  }

  public Integer getModuloId(String nombre) {
    return estadoRepository.getModuloId(nombre);
  }

  public Integer getEstadoId(String nombre) {
    return estadoRepository.getIdByNombre(nombre);
  }
}