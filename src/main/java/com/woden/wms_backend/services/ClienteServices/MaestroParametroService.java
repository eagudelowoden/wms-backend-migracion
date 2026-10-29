package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

/**
 * Parámetros por cliente guardados como Maestros de su base (Administración > Maestros), para que se cambien desde el
 * WMS sin scripts: "SmartCard Ingreso", "Limpieza Caracteres", "Diagnostico Serial3"... Solo las filas activas;
 * se guardan 60 s por cliente y tipo porque se leen en cada serie de Ingreso.
 */
@Service
public class MaestroParametroService {

  private static final Logger logger = LoggerFactory.getLogger(MaestroParametroService.class);
  private static final long CACHE_MS = 60_000;

  public record Fila(int id, String codigo, String descripcion, String detalle) {}

  private record Lectura(List<Fila> filas, long vence) {}

  private static final Map<String, Lectura> CACHE = new ConcurrentHashMap<>();

  @Autowired
  private DataSource dataSource;

  public List<Fila> activos(String tipoMaestro) {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    String clave = cliente + "|" + tipoMaestro;
    Lectura l = CACHE.get(clave);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.filas();
    }
    List<Fila> filas = new ArrayList<>();
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT m.Id, m.Codigo, m.Descripcion, m.Detalle FROM Maestro m "
            + "INNER JOIN TipoMaestro tm ON tm.Id = m.TipoMaestroId WHERE tm.Nombre = ? AND m.Activo = 1 "
            + "ORDER BY m.Id")) {
      ps.setString(1, tipoMaestro);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          filas.add(new Fila(rs.getInt(1), texto(rs.getString(2)), texto(rs.getString(3)), texto(rs.getString(4))));
        }
      }
    } catch (Exception e) {
      logger.warn("[Maestros] {} del cliente {} no se pudo leer: {}", tipoMaestro, cliente, e.getMessage());
    }
    CACHE.put(clave, new Lectura(filas, System.currentTimeMillis() + CACHE_MS));
    return filas;
  }

  private static String texto(String s) {
    return s == null ? "" : s;
  }
}
