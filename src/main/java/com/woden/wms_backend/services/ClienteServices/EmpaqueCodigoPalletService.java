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

@Service
public class EmpaqueCodigoPalletService {

  private static final Logger logger = LoggerFactory.getLogger(EmpaqueCodigoPalletService.class);
  public static final String INTERRUPTOR = "EMPAQUE_CODIGOSAP_PALLET";

  private record CodigoPallet(int codigoSapId, long vence) {}

  // el código SAP del pallet no cambia mientras se empaca: lo guardo un minuto para no consultarlo por cada serie
  private static final Map<String, CodigoPallet> PALLETS = new ConcurrentHashMap<>();

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private DataSource dataSource;

  public Integer codigoSap(Integer codigoSapSerie, Integer palletId) {
    if (palletId == null || palletId <= 0 || !interruptores.activo(INTERRUPTOR)) {
      return codigoSapSerie;
    }
    String clave = ClientDatabaseContext.getCurrentClientId() + "|" + palletId;
    CodigoPallet c = PALLETS.get(clave);
    if (c == null || c.vence() < System.currentTimeMillis()) {
      int codigo = 0;
      try (Connection conn = dataSource.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT p.CodigoSapId, cs.Codigo FROM Pallet p "
              + "INNER JOIN CodigoSap cs ON cs.Id = p.CodigoSapId WHERE p.Id = ?")) {
        ps.setInt(1, palletId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next() && rs.getInt(1) > 0 && !"MULTIMODELO".equalsIgnoreCase(String.valueOf(rs.getString(2)).trim())) {
            codigo = rs.getInt(1);
          }
        }
      } catch (Exception e) {
        logger.warn("[Empaque] código SAP del pallet {} no se pudo leer, queda el de la serie: {}", palletId, e.getMessage());
        return codigoSapSerie;
      }
      c = new CodigoPallet(codigo, System.currentTimeMillis() + 60_000);
      PALLETS.put(clave, c);
    }
    if (c.codigoSapId() <= 0) {
      return codigoSapSerie;
    }
    // solo entre códigos del mismo tipo: un equipo no puede quedar como smartcard o accesorio por el código del pallet
    Map<Integer, Integer> tipos = tipos();
    Integer tipoPallet = tipos.get(c.codigoSapId());
    Integer tipoSerie = codigoSapSerie == null ? null : tipos.get(codigoSapSerie);
    if (tipoPallet != null && tipoSerie != null && !tipoPallet.equals(tipoSerie)) {
      return codigoSapSerie;
    }
    return c.codigoSapId();
  }

  private record Tipos(Map<Integer, Integer> porCodigo, long vence) {}

  private static final Map<Integer, Tipos> TIPOS = new ConcurrentHashMap<>();

  private Map<Integer, Integer> tipos() {
    Integer cliente = ClientDatabaseContext.getCurrentClientId();
    Tipos t = cliente == null ? null : TIPOS.get(cliente);
    if (t != null && t.vence() > System.currentTimeMillis()) {
      return t.porCodigo();
    }
    Map<Integer, Integer> out = new java.util.HashMap<>();
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT Id, TipoId FROM CodigoSap WHERE TipoId IS NOT NULL");
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.put(rs.getInt(1), rs.getInt(2));
      }
    } catch (Exception e) {
      logger.warn("[Empaque] tipos de los códigos SAP: {}", e.getMessage());
    }
    if (cliente != null) {
      TIPOS.put(cliente, new Tipos(out, System.currentTimeMillis() + 60_000));
    }
    return out;
  }

  // el código que también va a Ingreso (0 = no cambia), para que Ingreso y Empaque queden con el mismo
  public int codigoParaIngreso(Integer codigoSapSerie, Integer codigoEmpaque) {
    return codigoEmpaque != null && codigoEmpaque > 0 && !codigoEmpaque.equals(codigoSapSerie) ? codigoEmpaque : 0;
  }

  public void alinearIngreso(Integer serialId, int codigoSapId) {
    if (serialId == null || codigoSapId <= 0) {
      return;
    }
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE Ingreso SET CodigoSapId = ? WHERE Id = ? AND ISNULL(CodigoSapId, 0) <> ?")) {
      ps.setInt(1, codigoSapId);
      ps.setInt(2, serialId);
      ps.setInt(3, codigoSapId);
      ps.executeUpdate();
    } catch (Exception e) {
      logger.warn("[Empaque] Ingreso {} no quedó con el código SAP {} del pallet: {}", serialId, codigoSapId, e.getMessage());
    }
  }
}
