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
    return c.codigoSapId() > 0 ? c.codigoSapId() : codigoSapSerie;
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
