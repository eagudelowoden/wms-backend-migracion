package com.woden.wms_backend.services.ClienteServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.dto.clientDTO.codigosap.MultimodeloRestringidoDTO;
import com.woden.wms_backend.dto.clientDTO.codigosap.MultimodeloRestringidoDTO.SapPermitido;
import com.woden.wms_backend.exception.BusinessRuleException;
import com.woden.wms_backend.exception.EntryNotFoundException;
import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapPermitidoRepository;
import com.woden.wms_backend.util.TypeMapper;

@Service
public class CodigoSapPermitidoService {

  private static final Logger logger = LoggerFactory.getLogger(CodigoSapPermitidoService.class);

  private static final String CODIGO_VALIDACION = "MULTIMODELO_RESTRINGIDO";

  @Autowired
  private CodigoSapPermitidoRepository repository;

  @Autowired
  private DataSource dataSource;

  /**
   * Kill switch por tenant. La tabla CodigoSapPermitido y sus SPs solo se
   * instalan en WmsWdLegacyCostaRica, asi que este gate debe ejecutarse ANTES
   * de cualquier acceso a dbo.CodigoSapPermitido. Con el flag apagado el
   * servicio se comporta exactamente igual que antes de la feature.
   *
   * Usa JDBC crudo con nombre calificado a proposito: con
   * ClientDatabaseContext apuntando a la BD del cliente, una llamada JPA a
   * ClienteValidacionRepository se rutearia a la BD del cliente en vez de
   * WmsWdGeneral. Mismo patron que TruckrollRepository.tieneValidacionActiva.
   */
  private boolean featureActiva() {
    Integer clienteId = ClientDatabaseContext.getCurrentClientId();
    if (clienteId == null) {
      return false;
    }
    String sql = "SELECT cv.Activo "
        + "FROM general.ClienteValidacion cv "
        + "INNER JOIN general.ClienteValidacionTipo cvt ON cvt.Id = cv.ValidacionTipoId "
        + "WHERE cv.ClienteId = ? AND cvt.Codigo = ? AND cv.Activo = true AND cvt.Activo = true";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, clienteId);
      ps.setString(2, CODIGO_VALIDACION);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      logger.error("[CodigoSapPermitidoService] Error consultando validacion '{}' para clienteId={}: {}",
          CODIGO_VALIDACION, clienteId, e.getMessage());
      return false;
    }
  }

  private Integer clienteIdActual() {
    Integer clienteId = ClientDatabaseContext.getCurrentClientId();
    if (clienteId == null) {
      throw new BusinessRuleException("No se pudo resolver el cliente de la sesion.");
    }
    return clienteId;
  }

  public List<Map<String, Object>> getPool(Integer codigoSapId) {
    exigirCodigoSapId(codigoSapId);
    if (!featureActiva()) {
      return new ArrayList<>();
    }
    return repository.getPool(codigoSapId).stream().map(obj -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", (Integer) obj[0]);
      map.put("codigo", (String) obj[1]);
      map.put("descripcion", (String) obj[2]);
      map.put("asignado", TypeMapper.toBoolean(obj[3]));
      return map;
    }).collect(Collectors.toList());
  }

  public boolean estaHabilitado() {
    return featureActiva();
  }

  private Set<String> leerPermitidos(Integer codigoSapId) {
    try {
      Set<String> permitidos = new LinkedHashSet<>();
      for (Object[] obj : repository.getPermitidos(codigoSapId)) {
        String codigo = (String) obj[1];
        if (codigo != null && !codigo.trim().isEmpty()) {
          permitidos.add(codigo.trim().toUpperCase());
        }
      }
      return permitidos;
    } catch (Exception e) {
      logger.warn("[CodigoSapPermitidoService] No se pudo leer la lista de permitidos de codigoSapId={}: {}",
          codigoSapId, e.getMessage());
      return Set.of();
    }
  }

  public MultimodeloRestringidoDTO getConfig(Integer codigoSapId) {
    exigirCodigoSapId(codigoSapId);
    MultimodeloRestringidoDTO dto = new MultimodeloRestringidoDTO();
    dto.setCodigoSapId(codigoSapId);
    dto.setHabilitado(featureActiva());
    if (!dto.isHabilitado()) {
      dto.setRestringido(false);
      return dto;
    }
    List<SapPermitido> permitidos = repository.getPermitidos(codigoSapId).stream().map(obj -> {
      SapPermitido p = new SapPermitido();
      p.setId((Integer) obj[0]);
      p.setCodigo((String) obj[1]);
      p.setDescripcion((String) obj[2]);
      return p;
    }).collect(Collectors.toList());
    dto.setPermitidos(permitidos);
    dto.setRestringido(!permitidos.isEmpty());
    return dto;
  }

  @Transactional
  public int insertar(Integer codigoSapId, List<Integer> permitidosIds) {
    exigirCodigoSapId(codigoSapId);
    if (!featureActiva()) {
      throw new BusinessRuleException("La funcionalidad de multimodelo restringido no esta habilitada para este cliente.");
    }
    if (permitidosIds == null || permitidosIds.isEmpty()) {
      return 0;
    }
    List<Integer> validos = new ArrayList<>();
    for (Integer permitidoId : permitidosIds) {
      validar(codigoSapId, permitidoId);
      validos.add(permitidoId);
    }
    try (Connection conn = dataSource.getConnection()) {
      // Reemplaza pa_InsertCodigoSapPermitido: inserta el par solo si todavia no existe
      PreparedStatement stmt = conn.prepareStatement(
          "INSERT INTO CodigoSapPermitido (CodigoSapId, PermitidoId) SELECT ?, ? "
              + "WHERE NOT EXISTS (SELECT 1 FROM CodigoSapPermitido WHERE CodigoSapId = ? AND PermitidoId = ?)");
      for (Integer permitidoId : validos) {
        stmt.setInt(1, codigoSapId);
        stmt.setInt(2, permitidoId);
        stmt.setInt(3, codigoSapId);
        stmt.setInt(4, permitidoId);
        stmt.addBatch();
      }
      stmt.executeBatch();
      stmt.close();
    } catch (SQLException e) {
      logger.error("[CodigoSapPermitidoService] Error insertando permitidos para codigoSapId={}: {}",
          codigoSapId, e.getMessage());
      throw new BusinessRuleException("Error al asignar los codigos SAP permitidos: " + e.getMessage());
    }
    return validos.size();
  }

  @Transactional
  public int eliminar(Integer codigoSapId, List<Integer> permitidosIds) {
    exigirCodigoSapId(codigoSapId);
    if (!featureActiva()) {
      throw new BusinessRuleException("La funcionalidad de multimodelo restringido no esta habilitada para este cliente.");
    }
    if (permitidosIds == null || permitidosIds.isEmpty()) {
      return 0;
    }
    try (Connection conn = dataSource.getConnection()) {
      // Reemplaza pa_DeleteCodigoSapPermitido
      PreparedStatement stmt = conn.prepareStatement(
          "DELETE FROM CodigoSapPermitido WHERE CodigoSapId = ? AND PermitidoId = ?");
      for (Integer permitidoId : permitidosIds) {
        stmt.setInt(1, codigoSapId);
        stmt.setInt(2, permitidoId);
        stmt.addBatch();
      }
      stmt.executeBatch();
      stmt.close();
    } catch (SQLException e) {
      logger.error("[CodigoSapPermitidoService] Error eliminando permitidos para codigoSapId={}: {}",
          codigoSapId, e.getMessage());
      throw new BusinessRuleException("Error al remover los codigos SAP permitidos: " + e.getMessage());
    }
    return permitidosIds.size();
  }

  private void exigirCodigoSapId(Integer codigoSapId) {
    if (codigoSapId == null) {
      throw new BusinessRuleException("El codigo SAP padre es obligatorio.");
    }
    clienteIdActual();
    if (!repository.existsById(codigoSapId)) {
      throw new EntryNotFoundException("Codigo SAP " + codigoSapId + " no existe.");
    }
  }

  private void validar(Integer codigoSapId, Integer hijoId) {
    if (hijoId == null) {
      throw new BusinessRuleException("El codigo SAP hijo es obligatorio.");
    }
    if (codigoSapId.equals(hijoId)) {
      throw new BusinessRuleException("Un codigo SAP no puede contenerse a si mismo en su lista de permitidos.");
    }
  }
}
