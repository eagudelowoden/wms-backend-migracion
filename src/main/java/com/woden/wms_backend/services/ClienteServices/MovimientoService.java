package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.MovimientoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.MovimientoRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class MovimientoService extends BaseService<MovimientoModel, Integer> {

  @Autowired
  private MovimientoRepository movimientoRepository;

  // public Integer getLast(Integer palletId){
  // Integer result = movimientoRepository.getLast(palletId);
  // return result;
  // }

  @Autowired
  private DataSource dataSource;

  public Integer getLast(int palletId) {
    String sql = "SELECT origenId FROM Movimiento " +
        "WHERE id = (SELECT MAX(m.id) FROM Movimiento m " +
        "INNER JOIN Ingreso i ON m.serialId = i.id WHERE i.PalletId = ?)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, palletId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("origenId");
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener origenId: " + e.getMessage());
      // Puedes también loguear con logger
    }

    return null; // Devuelve 0 si no se encuentra nada o hay error
  }

  public Integer userCount(Integer usuarioId) {
    return movimientoRepository.userCount(usuarioId);
  }
}
