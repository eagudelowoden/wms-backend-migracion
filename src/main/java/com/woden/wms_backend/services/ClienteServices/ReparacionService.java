package com.woden.wms_backend.services.ClienteServices;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.ReparacionModel;
import com.woden.wms_backend.repositories.ClienteRepositories.ReparacionRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class ReparacionService extends BaseService<ReparacionModel, Integer> {
  @Autowired
  ReparacionRepository repository;

  public void create(Integer serialId, String serial, String mac, Integer codigoSapId, Integer estadoFinalId,
      Integer fallaDxId, Integer tecnicoAsignacionId, String fechaAsignacion, Integer usuarioId) {
    repository.create(serialId, serial, mac, codigoSapId, estadoFinalId, fallaDxId, tecnicoAsignacionId,
        fechaAsignacion, usuarioId);
  }

  public void updateRepair(Integer estadoFinalId, Integer falla1Id, Integer falla2Id, Integer falla3Id,
      Integer falla4Id, String partesCambiadas, Integer motivoScrapId, Integer tecnicoReparacionId, String serial,
      Integer estadoCalidadId) {
    Integer filas = 0;
    repository.updateRepair(estadoFinalId, falla1Id, falla2Id, falla3Id, falla4Id, partesCambiadas, motivoScrapId,
        tecnicoReparacionId, serial, estadoCalidadId, filas);
  }

  public void deleteRepair(List<String> seriales) {
    for (String serial : seriales) {
      repository.deleteRepair(serial);
    }
  }

  public List<ReparacionModel> getHistoricRepair(String serial) {
    List<Object[]> results = repository.getHistoricRepair(serial);

    if (results.isEmpty()) {
      return null;
    }

    List<ReparacionModel> response = new ArrayList<>();

    for (Object[] obj : results) {
      ReparacionModel reparacion = new ReparacionModel();
      reparacion.setSerial((String) obj[0]);
      reparacion.setMac((String) obj[1]);
      reparacion.setCodigoSap((String) obj[2]);
      reparacion.setDescripcion((String) obj[3]);
      reparacion.setEstadoFinal((String) obj[4]);
      reparacion.setFalla1((String) obj[5]);
      reparacion.setTecnicoReparacion((String) obj[6]);
      reparacion.setFechaReparacion(obj[7] != null ? ((Timestamp) obj[7]).toString() : null);
      response.add(reparacion);
    }

    return response;
  }

  public ReparacionModel getLastRepair(String serial) {
    List<Object[]> results = repository.getLastRepair(serial);

    if (results.isEmpty()) {
      return null;
    }

    Object[] obj = results.get(0);
    ReparacionModel reparacion = new ReparacionModel();
    reparacion.setEstadoFinal((String) obj[0]);
    reparacion.setTecnicoReparacion((String) obj[1]);
    reparacion.setFechaReparacion(obj[2] != null ? ((Timestamp) obj[2]).toString() : null);

    return reparacion;
  }

  public List<ReparacionModel> getRepairUser(Integer usuarioId) {
    List<Object[]> results = repository.getRepairUser(usuarioId);

    if (results.isEmpty()) {
      return null;
    }

    List<ReparacionModel> response = new ArrayList<>();

    for (Object[] obj : results) {
      ReparacionModel reparacion = new ReparacionModel();
      reparacion.setSerial((String) obj[0]);
      reparacion.setMac((String) obj[1]);
      reparacion.setCodigoSap((String) obj[2]);
      reparacion.setDescripcion((String) obj[3]);
      reparacion.setTecnicoReparacion((String) obj[4]);
      response.add(reparacion);
    }
    return response;
  }

  public List<Map<String, Object>> searchAssignedRepair(Integer tecnicoAsignacionId, String tipo) {
    List<Object[]> results = repository.searchAssignedRepair(tecnicoAsignacionId, tipo);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("serial", obj[0]);
      map.put("fechaAsignacion", obj[1]);
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchRepairedRepair(Integer tecnicoReparacionId, String estado) {
    List<Object[]> results = repository.searchRepairedRepair(tecnicoReparacionId, estado);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("serial", obj[0]);
      map.put("fechaReparacion", obj[1]);
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> getAssignedTechnicianRepair(String serial) {
    List<Object[]> results = repository.getAssignedTechnicianRepair(serial);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("tecnicoAsignacion", obj[0]);
      return map;
    }).collect(Collectors.toList());
  }

}
