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
}
