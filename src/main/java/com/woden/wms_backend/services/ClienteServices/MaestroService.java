package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.Entity.MaestroModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MaestroService extends BaseService<MaestroModel, Integer> {

  @Autowired
  private MaestroRepository maestroRepository;

  public List<MaestroModel> getByTipoMaestroId(int tipoMaestroId) {
    return maestroRepository.findByTipoMaestroId(tipoMaestroId);
  }

  public List<Map<String, Object>> search(String tipoMaestro) {
    List<Object[]> results = maestroRepository.searchSP(tipoMaestro);
    List<Map<String, Object>> list = new ArrayList<>();
    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("codigo", row[1]);
      map.put("descripcion", row[2]);
      map.put("detalle", row[3]);
      map.put("adicional", row[4]);
      map.put("tipoMaestro", row[5]);
      Object activoObj = row[6];
      map.put("activo", activoObj instanceof Boolean ? (((Boolean) activoObj) ? 1 : 0) : activoObj);
      list.add(map);
    }
    return list;
  }

  @Transactional
  public void create(MaestroModel model) {
    maestroRepository.insertSP(model.getCodigo(), model.getDescripcion(),
        model.getDetalle(), model.getAdicional(), model.getTipoMaestroId());
    if (model.getActivo() == 0) {
      Integer id = maestroRepository.getIdByCodigo(model.getCodigo(), model.getTipoMaestroId());
      if (id != null) {
        maestroRepository.innactivateSP(0, id);
      }
    }
  }

  @Transactional
  public Integer update(MaestroModel model) {
    Integer result = maestroRepository.updateSP(model.getCodigo(), model.getDescripcion(),
        model.getDetalle(), model.getAdicional(), model.getTipoMaestroId(), model.getId());
    if (result != null && result > 0) {
      MaestroModel current = getById(model.getId());
      if (current != null && current.getActivo() != model.getActivo()) {
        maestroRepository.innactivateSP(model.getActivo(), model.getId());
      }
    }
    return result;
  }

  @Transactional
  public void delete(Integer id) {
    maestroRepository.deleteSP(id);
  }

  @Transactional
  public Integer toggle(Integer id, Integer estado) {
    return maestroRepository.innactivateSP(estado, id);
  }

  public List<String> obtenerTipologias(String desc1, String desc2, String desc3, String desc4) {
    List<Object[]> result = maestroRepository.getTipologias(desc1, desc2, desc3, desc4);
    return result.stream()
        .map(r -> (String) r[0]) // extrae el código del resultado
        .collect(Collectors.toList());
  }

  public Integer getIdMaster(String codigo, String tipo) {
    List<Integer> result = maestroRepository.getIdMaster(codigo, tipo);
    return result.isEmpty() ? null : result.get(0);
  }

  public List<String> getListByTipo(String tipo) {
    return maestroRepository.getListByTipo(tipo);
  }

  public List<String> obtenerOrigenes(String tipo) {
    return maestroRepository.getOrigenes(tipo);
  }

  public int getFamilyNumberPallet(String codigoSap) {
    return maestroRepository.getFamilyNumberPallet(codigoSap);
  }

  public int addCountPalletFamily(int familyId, String value, int filas) {
    return maestroRepository.addCountPalletFamily(value, familyId, filas);
  }

  public List<String> getModelMaster(String codigoSap) {
    List<Object[]> results = maestroRepository.getModelMaster(codigoSap);
    return results.stream()
        .map(obj -> (String) obj[0]) // devuelve solo el string
        .collect(Collectors.toList());
  }

  public List<String> getLevelsClasification() {
    List<Object[]> results = maestroRepository.getLevelsClasification();
    return results.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
  }

  public List<Map<String, String>> getFallas(String nombre) {
    List<Object[]> results = maestroRepository.getFallas(nombre);

    return results.stream().map(obj -> {
      Map<String, String> map = new HashMap<>();
      map.put("codigo", (String) obj[0]);
      map.put("descripcion", (String) obj[1]);
      return map;
    }).collect(Collectors.toList());
  }

  public List<String> getDesctiption(String codigo) {
    List<Object[]> results = maestroRepository.getDesctiption(codigo);
    return results.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
  }

  public Integer getWarranty() {
    return maestroRepository.getWarranty();
  }

  public Integer searchLevelComponentsAsig(String serial) {
    return maestroRepository.searchLevelComponentsAsig(serial);
  }

  public Integer getLevelById(Integer levelId) {
    return maestroRepository.getLevelById(levelId);
  }

  public Integer getLevelId(String level) {
    return maestroRepository.getLevelId(level);
  }

  public Integer getIdLevelRepairMaster() {
    return maestroRepository.getIdLevelRepairMaster();
  }

  public void insertComponentsAsig(String serial, Integer componenteId) {
    maestroRepository.insertComponentsAsig(serial, componenteId);
  }

  public void deleteComponentsAsig(String serial, Integer componenteId) {
    maestroRepository.deleteComponentsAsig(serial, componenteId);
  }

  public List<Map<String, Object>> searchComponents(String serial, String falla) {
    List<Object[]> results = maestroRepository.searchComponents(serial, falla);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", (Integer) obj[0]);
      map.put("componente", (String) obj[1]);
      map.put("nivel", (String) obj[2]);
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchComponentsAsig(String serial, String falla) {
    int filas = 0; // Valor inicial para filas OUT
    List<Object[]> results = maestroRepository.searchComponentsAsig(serial, falla, filas);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", (Integer) obj[0]);
      map.put("componente", (String) obj[1]);
      map.put("nivel", (String) obj[2]);
      return map;
    }).collect(Collectors.toList());
  }

  public Integer getFamilyNumberBox(String codigoSap, String destino) {
    return maestroRepository.getFamilyNumberBox(codigoSap, destino);
  }

  public String getFamilyAcronyms(String codigoSap, String destino) {
    List<String> siglas = maestroRepository.getFamilyAcronyms(codigoSap, destino);
    return siglas.get(0);
  }

  public void addCountBoxFamily(int familyId, String value) {
    Integer filasOut = 4;
    maestroRepository.addCountBoxFamily(familyId, value, filasOut);
  }

  public String getFamilyMaster(String codigoSap) {
    List<Object[]> results = maestroRepository.getFamilyMaster(codigoSap);
    List<String> siglas = results.stream()
        .map(obj -> (String) obj[0])
        .collect(Collectors.toList());
    return siglas.get(0);
  }

  public String getProviderMaster(String codigoSap) {
    return maestroRepository.getProviderMaster(codigoSap).stream()
        .map(obj -> (String) obj[0])
        .findFirst()
        .orElse(null);
  }

  public String getProviderDescriptionMaster(String codigoSap) {
    return maestroRepository.getProviderDescriptionMaster(codigoSap).stream()
        .map(obj -> (String) obj[0])
        .findFirst()
        .orElse(null);
  }

  public String getMasterNameById(Integer id) {
    return maestroRepository.getMasterNameById(id).stream()
        .findFirst()
        .orElse(null);
  }

  public String getMasterDescriptionById(Integer id) {
    return maestroRepository.getMasterDescriptionById(id).stream()
        .findFirst()
        .orElse(null);
  }

  public String getMasterDetailById(Integer id) {
    return maestroRepository.getMasterDetailById(id).stream()
        .findFirst()
        .orElse(null);
  }

}
