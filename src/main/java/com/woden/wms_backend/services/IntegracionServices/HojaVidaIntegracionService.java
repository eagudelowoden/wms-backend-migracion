package com.woden.wms_backend.services.IntegracionServices;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Descarga de Hoja de Vida para la integración externa (/evidencias/**).
 * Reutiliza la misma tabla HojaVida que ya llena DiagnosticoService — solo
 * lee, nunca escribe. Depende de que ApiKeyAuthenticationFilter ya haya
 * dejado ClientDatabaseContext apuntando a la BD del cliente correcto.
 */
@Service
public class HojaVidaIntegracionService {

  private static final Logger logger = LoggerFactory.getLogger(HojaVidaIntegracionService.class);

  @Autowired
  private DataSource dataSource;

  /**
   * Archivo de hoja de vida activo para el serial, o null si no existe.
   * ORDER BY Id DESC: en teoría solo debería existir una fila Activo=1 por
   * serial (índice único UQ_HojaVida_SerialId_Activo), pero no todas las BD
   * de cliente lo tienen creado todavía — con esto, si llega a haber más de
   * una fila activa por algún motivo, siempre se toma la más reciente.
   */
  public Path buscarArchivo(String serial) {
    String sql = "SELECT TOP 1 NombreArchivo, RutaCompleta FROM HojaVida WHERE Serial = ? AND Activo = 1 ORDER BY Id DESC";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, serial);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // RutaCompleta nunca sale de este método hacia el controller como texto
          // plano en la respuesta — solo se usa acá, server-side, para abrir el archivo.
          // .strip() por si el dato viene con espacios/saltos de línea sobrantes
          // (pasa con cargue manual) — un \n en Content-Disposition tumba la respuesta.
          String ruta = rs.getString("RutaCompleta");
          return ruta != null ? Paths.get(ruta.strip()) : null;
        }
      }
    } catch (SQLException e) {
      logger.error("[HojaVidaIntegracionService] Error consultando HojaVida para serial {}: {}", serial, e.getMessage());
    }
    return null;
  }
}
