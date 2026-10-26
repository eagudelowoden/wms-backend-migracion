package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;

/**
 * Interruptores por cliente (Clientes > Validaciones) para los flujos de alto volumen (cada serie de Ingreso o de
 * Empaque): se guardan 60 s por cliente y código para no sumar una consulta por serie. Si la lectura falla se toma
 * como apagado (el flujo sigue como siempre).
 */
@Service
public class InterruptorClienteService {

  private static final Logger logger = LoggerFactory.getLogger(InterruptorClienteService.class);
  private static final long CACHE_MS = 60_000;

  private record Lectura(boolean activo, long vence) {}

  private static final Map<String, Lectura> CACHE = new ConcurrentHashMap<>();

  @Autowired
  private DataSource dataSource;

  public boolean activo(String codigo) {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    if (cliente == null || cliente <= 0) {
      return false;
    }
    String clave = cliente + "|" + codigo;
    Lectura l = CACHE.get(clave);
    if (l != null && l.vence() > System.currentTimeMillis()) {
      return l.activo();
    }
    boolean activo = false;
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM WmsWdGeneral.dbo.ClienteValidacion cv "
            + "JOIN WmsWdGeneral.dbo.ClienteValidacionTipo t ON t.Id = cv.ValidacionTipoId "
            + "WHERE cv.ClienteId = ? AND t.Codigo = ? AND cv.Activo = 1 AND t.Activo = 1")) {
      ps.setInt(1, cliente);
      ps.setString(2, codigo);
      try (ResultSet rs = ps.executeQuery()) {
        activo = rs.next() && rs.getInt(1) > 0;
      }
    } catch (Exception e) {
      logger.warn("[Interruptor] {} del cliente {} no se pudo leer: {}", codigo, cliente, e.getMessage());
    }
    CACHE.put(clave, new Lectura(activo, System.currentTimeMillis() + CACHE_MS));
    return activo;
  }
}
