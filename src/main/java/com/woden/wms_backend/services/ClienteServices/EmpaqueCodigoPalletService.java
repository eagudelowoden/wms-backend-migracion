package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Interruptor EMPAQUE_CODIGOSAP_PALLET (el arreglo de pa_InsertPacking en DirecTV Ecuador): la serie empacada queda en
 * Empaque con el código SAP del pallet de empaque y no con el suyo (el front deja empacar series de otro código).
 * Pallets MULTIMODELO conservan el código de cada serie. Ingreso no se toca. Apagado o si la lectura falla: el código
 * de la serie, como siempre.
 */
@Service
public class EmpaqueCodigoPalletService {

  private static final Logger logger = LoggerFactory.getLogger(EmpaqueCodigoPalletService.class);
  public static final String INTERRUPTOR = "EMPAQUE_CODIGOSAP_PALLET";

  @Autowired
  private InterruptorClienteService interruptores;

  @Autowired
  private DataSource dataSource;

  public Integer codigoSap(Integer codigoSapSerie, Integer palletId) {
    if (palletId == null || palletId <= 0 || !interruptores.activo(INTERRUPTOR)) {
      return codigoSapSerie;
    }
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT p.CodigoSapId, cs.Codigo FROM Pallet p "
            + "INNER JOIN CodigoSap cs ON cs.Id = p.CodigoSapId WHERE p.Id = ?")) {
      ps.setInt(1, palletId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next() && rs.getInt(1) > 0 && !"MULTIMODELO".equalsIgnoreCase(String.valueOf(rs.getString(2)).trim())) {
          return rs.getInt(1);
        }
      }
    } catch (Exception e) {
      logger.warn("[Empaque] código SAP del pallet {} no se pudo leer, queda el de la serie: {}", palletId, e.getMessage());
    }
    return codigoSapSerie;
  }
}
