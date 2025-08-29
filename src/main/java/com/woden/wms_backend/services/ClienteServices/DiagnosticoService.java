package com.woden.wms_backend.services.ClienteServices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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
  public void create(Integer serialId, String serial, String mac, Integer codigoSapId, Integer usuarioId, String variable1, String variable2, String variable3, String variable4) {
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

  public void updateDiagnostico(Integer estadoFinalId, Integer fallaId, String serial, Integer estadoCalidadId){
    try{
      repository.updateDiagnostico(estadoFinalId, fallaId, serial, estadoCalidadId);
    }
    catch(Exception e){
      System.out.println("Error: " + e.getMessage());
    }
  }

  public Integer getFailureIdDiagnostic(String serial) {
    return repository.getFailureIdDiagnostic(serial);
  }

  public String getFailureDiagnostic(String serial) {
    return repository.getFailureDiagnostic(serial);
  }
}
