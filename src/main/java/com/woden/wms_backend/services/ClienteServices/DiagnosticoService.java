package com.woden.wms_backend.services.ClienteServices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.repositories.ClienteRepositories.DiagnosticoRepository;

import jakarta.transaction.Transactional;

@Service
public class DiagnosticoService {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private DiagnosticoRepository repository;

  @Transactional
  public void create(Integer serialId, String serial, String mac, Integer codigoSapId, Integer usuarioId,
      String variable1, String variable2, String variable3, String variable4) {
    repository.create(serialId, serial, mac, codigoSapId, usuarioId, variable1, variable2, variable3, variable4);
  }

  public Integer updateSapCode(String serial) {
    try (Connection conn = dataSource.getConnection()) {
      CallableStatement stmt = conn.prepareCall("{call pa_UpdateSapCodeDiagnostic(?, ?)}");

      // Parámetro de entrada
      stmt.setString(1, serial);

      // Parámetro de salida
      stmt.registerOutParameter(2, Types.INTEGER); // "Filas" está en la posición 2

      stmt.execute();

      Integer filas = stmt.getInt(2); // Obtener el valor del OUT param

      stmt.close();
      return filas > 0 ? 1 : 0;

    } catch (SQLException e) {
      System.err.println("Error en updateSapCode: " + e.getMessage());
      return 0;
    }
  }

  public void updateDiagnostico(Integer estadoFinalId, Integer fallaId, String serial, Integer estadoCalidadId) {
    try {
      repository.updateDiagnostico(estadoFinalId, fallaId, serial, estadoCalidadId);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public Integer getFailureIdDiagnostic(Integer serialId) {
    return repository.getFailureIdDiagnostic(serialId);
  }

  public String getFailureDiagnostic(String serial) {
    return repository.getFailureDiagnostic(serial);
  }

  public void delete(String serial) {
    try {
      repository.delete(serial);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public List<Map<String, Object>> getDiagnosedUser(Integer usuarioId) {
    List<Object[]> results = repository.getDiagnosedUser(usuarioId);

    List<Map<String, Object>> formattedResults = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0].toString());
      map.put("serialId", row[1].toString());
      map.put("serial", row[2].toString());
      map.put("mac", row[3].toString());
      map.put("codigoSap", row[4].toString());
      map.put("descripcion", row[5].toString());
      map.put("fallaId", Integer.parseInt(row[6].toString()));
      map.put("estadoFinalId", Integer.parseInt(row[7].toString()));
      map.put("usuario", row[8].toString());
      map.put("loteId", row[9] != null ? Integer.parseInt(row[9].toString()) : 0);
      map.put("lote", row[10] != null ? row[10].toString() : "");
      map.put("pqrs", row[11] != null ? row[11].toString() : "");
      map.put("fecha", row[12] != null ? row[12].toString() : "");
      formattedResults.add(map);
    }
    return formattedResults;
  }
}
