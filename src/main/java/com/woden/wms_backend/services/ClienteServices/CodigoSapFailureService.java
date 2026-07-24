package com.woden.wms_backend.services.ClienteServices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapFailureRepository;

@Service
public class CodigoSapFailureService {
  @Autowired
  private CodigoSapFailureRepository repository;

  @Autowired
  private DataSource dataSource;

  public Boolean getSapCodeFailureAsig(String nombre) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_GetSapCodeFailureAsig(?)}");
      stmt.setString(1, nombre);

      ResultSet rs = stmt.executeQuery();

      Boolean asignacionFalla = null;
      if (rs.next()) {
        asignacionFalla = rs.getBoolean("Asignacionfalla");
      }

      rs.close();
      stmt.close();

      return asignacionFalla != null ? asignacionFalla : false;

    } catch (SQLException e) {
      System.err.println("Error en codigosapFailure: " + e.getMessage());
      return false;
    }
  }

  public List<Map<String, String>> getFallas(String nombre) {
    List<Object[]> results = repository.getFallas(nombre);
    return results.stream().map(obj -> {
      Map<String, String> map = new HashMap<>();
      map.put("codigo", (String) obj[0]);
      map.put("descripcion", (String) obj[1]);
      return map;
    }).collect(Collectors.toList());
  }

  public Boolean searchMandatoryComponent(Integer codigoSapId, Integer fallaId) {
    return repository.searchMandatoryComponent(codigoSapId, fallaId);
  }

  public List<Map<String, Object>> getFailuresBySap(Integer codigoSapId) {
    List<Object[]> results = repository.getFailuresBySap(codigoSapId);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", (Integer) obj[0]);
      map.put("codigo", (String) obj[1]);
      map.put("descripcion", (String) obj[2]);
      Object raw = obj[3];
      map.put("componenteObligatorio", raw != null ? String.valueOf(raw) : "0");
      return map;
    }).collect(Collectors.toList());
  }

  public void createFalla(Integer codigoSapId, List<Integer> fallaIds) {
    if (fallaIds == null || fallaIds.isEmpty()) return;
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_InsertCodigoSapFalla(?, ?)}");
      for (Integer fallaId : fallaIds) {
        stmt.setInt(1, codigoSapId);
        stmt.setInt(2, fallaId);
        stmt.addBatch();
      }
      stmt.executeBatch();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("Error createFalla: " + e.getMessage());
      throw new RuntimeException("Error al asignar fallas: " + e.getMessage());
    }
  }

  public void deleteFalla(Integer codigoSapId, List<Integer> fallaIds) {
    if (fallaIds == null || fallaIds.isEmpty()) return;
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_DeleteCodigoSapFalla(?, ?)}");
      for (Integer fallaId : fallaIds) {
        stmt.setInt(1, codigoSapId);
        stmt.setInt(2, fallaId);
        stmt.addBatch();
      }
      stmt.executeBatch();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("Error deleteFalla: " + e.getMessage());
      throw new RuntimeException("Error al remover fallas: " + e.getMessage());
    }
  }

  public List<Map<String, Object>> searchSapCodeFailureComponent(Integer codigoSapId, Integer fallaId) {
    List<Object[]> results = repository.searchSapCodeFailureComponent(codigoSapId, fallaId);
    return results.stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", (Integer) obj[0]);
      map.put("componente", (String) obj[1]);
      map.put("nivel", (String) obj[2]);
      return map;
    }).collect(Collectors.toList());
  }
}
