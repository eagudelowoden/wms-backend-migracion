package com.woden.wms_backend.services.ClienteServices;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

@Service
public class PrnPathResolverService {

  @Value("${PRN_LOCAL_PATH:}")
  private String localPrnPath;

  @Autowired
  private DataSource dataSource;

  public String resolvePath(String tipo) {
    if (localPrnPath != null && !localPrnPath.isBlank()) {
      String tipoLower = tipo != null ? tipo.toLowerCase() : "empaque";
      if ("empaque_despacho".equals(tipoLower)) {
        tipoLower = "empaque";
      }
      return localPrnPath + File.separator + tipoLower;
    }

    Integer clientId = ClientDatabaseContext.getCurrentClientId();
    if (clientId == null) return null;

    String columna = "ETIQUETADO".equalsIgnoreCase(tipo) ? "PrnEtiquetado" : "PrnEmpaque";
    String sql = "SELECT " + columna + " FROM [WmsWdGeneral].[dbo].[Cliente] WHERE Id = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, clientId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getString(1);
      }
    } catch (java.sql.SQLException e) {
      System.err.println("Error consultando ruta PRN del cliente " + clientId + ": " + e.getMessage());
    }
    return null;
  }
}
