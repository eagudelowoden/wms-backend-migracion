package com.woden.wms_backend.services.ClienteServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.woden.wms_backend.dto.clientDTO.loads.UpdateNotAvailableRowDTO;
import com.woden.wms_backend.exception.BusinessRuleException;

import jakarta.transaction.Transactional;

@Service
public class LoadsService {

  private static final Set<String> ALLOWED_BASES = Set.of(
      "BaseDespacho", "BaseEmpaque", "BaseInventario",
      "BaseSeparacion", "BaseIngreso", "BaseEtiquetado"
  );

  private static final Set<String> BASES_WITH_LOTE = Set.of(
      "BaseDespacho", "BaseEmpaque", "BaseIngreso"
  );

  private static final int BATCH_SIZE = 500;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DataSource dataSource;

  private void validateBase(String base) {
    if (!ALLOWED_BASES.contains(base)) {
      throw new BusinessRuleException("Base no válida: " + base);
    }
  }

  public void deleteBase(String base) {
    validateBase(base);
    jdbcTemplate.execute("EXEC pa_DeleteBase '" + base + "'");
  }

  public List<Map<String, Object>> searchBase(String base) {
    validateBase(base);
    return jdbcTemplate.queryForList("EXEC pa_SearchBase '" + base + "'");
  }

  public Map<String, Object> searchBasePaged(String base, int page, int size) {
    validateBase(base);
    Map<String, Object> result = new LinkedHashMap<>();
    String tmpTable = "#tmpCargues_" + System.currentTimeMillis();
    boolean hasLote = BASES_WITH_LOTE.contains(base);
    String createSql = hasLote
        ? "CREATE TABLE " + tmpTable + " (Id int, Serial varchar(255), CodigoSap varchar(255), EstadoSap varchar(255), EstadoRR varchar(255), Lote varchar(255))"
        : "CREATE TABLE " + tmpTable + " (Id int, Serial varchar(255), CodigoSap varchar(255), EstadoSap varchar(255), EstadoRR varchar(255))";

    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {

      stmt.execute(createSql);
      stmt.execute("INSERT INTO " + tmpTable + " EXEC pa_SearchBase '" + base + "'");

      int total = 0;
      try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tmpTable)) {
        if (rs.next()) total = rs.getInt(1);
      }

      List<Map<String, Object>> data = new ArrayList<>();
      String pageSql = "SELECT * FROM " + tmpTable + " ORDER BY Id OFFSET " + (page * size) + " ROWS FETCH NEXT " + size + " ROWS ONLY";
      try (ResultSet rs = stmt.executeQuery(pageSql)) {
        java.sql.ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        while (rs.next()) {
          Map<String, Object> row = new LinkedHashMap<>();
          for (int i = 1; i <= cols; i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
          }
          data.add(row);
        }
      }

      stmt.execute("DROP TABLE " + tmpTable);

      result.put("data", data);
      result.put("total", total);
      result.put("page", page);
      result.put("size", size);
    } catch (SQLException e) {
      throw new BusinessRuleException("Error al consultar base: " + e.getMessage());
    }
    return result;
  }

  public int countBase(String base) {
    validateBase(base);
    String tmpTable = "#tmpCarguesCount_" + System.currentTimeMillis();
    boolean hasLote = BASES_WITH_LOTE.contains(base);
    String createSql = hasLote
        ? "CREATE TABLE " + tmpTable + " (Id int, Serial varchar(255), CodigoSap varchar(255), EstadoSap varchar(255), EstadoRR varchar(255), Lote varchar(255))"
        : "CREATE TABLE " + tmpTable + " (Id int, Serial varchar(255), CodigoSap varchar(255), EstadoSap varchar(255), EstadoRR varchar(255))";

    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {

      stmt.execute(createSql);
      stmt.execute("INSERT INTO " + tmpTable + " EXEC pa_SearchBase '" + base + "'");

      int total = 0;
      try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tmpTable)) {
        if (rs.next()) total = rs.getInt(1);
      }

      stmt.execute("DROP TABLE " + tmpTable);
      return total;
    } catch (SQLException e) {
      throw new BusinessRuleException("Error al contar registros: " + e.getMessage());
    }
  }

  public Map<String, Object> uploadFile(String base, MultipartFile file) {
    validateBase(base);
    Map<String, Object> result = new LinkedHashMap<>();
    boolean hasLote = BASES_WITH_LOTE.contains(base);
    int totalInsertados = 0;

    String insertSql = buildInsertSql(base, hasLote);

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

      List<String[]> batch = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) continue;
        String[] values = line.split("\t", -1);
        batch.add(values);
        if (batch.size() >= BATCH_SIZE) {
          totalInsertados += executeBatch(insertSql, batch, hasLote);
          batch.clear();
        }
      }
      if (!batch.isEmpty()) {
        totalInsertados += executeBatch(insertSql, batch, hasLote);
      }

      result.put("ok", true);
      result.put("registros", totalInsertados);
    } catch (Exception e) {
      result.put("ok", false);
      result.put("error", e.getMessage());
    }
    return result;
  }

  private String buildInsertSql(String base, boolean hasLote) {
    if (hasLote) {
      return "INSERT INTO " + base + " (Serial, CodigoSap, EstadoSap, EstadoRR, Lote) VALUES (?, ?, ?, ?, ?)";
    }
    return "INSERT INTO " + base + " (Serial, CodigoSap, EstadoSap, EstadoRR) VALUES (?, ?, ?, ?)";
  }

  private int executeBatch(String sql, List<String[]> batch, boolean hasLote) {
    int count = 0;
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      for (String[] values : batch) {
        ps.setString(1, safeGet(values, 1));
        ps.setString(2, safeGet(values, 2));
        ps.setString(3, safeGet(values, 3));
        ps.setString(4, safeGet(values, 4));
        if (hasLote) {
          ps.setString(5, safeGet(values, 5));
        }
        ps.addBatch();
        count++;
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new BusinessRuleException("Error al insertar registros: " + e.getMessage());
    }
    return count;
  }

  private String safeGet(String[] values, int index) {
    if (index < values.length && values[index] != null) {
      return values[index].trim();
    }
    return null;
  }

  @Transactional
  public Map<String, Object> bulkUpload(String base, List<Map<String, Object>> records) {
    validateBase(base);
    deleteBase(base);

    boolean hasLote = BASES_WITH_LOTE.contains(base);
    String insertSql = buildInsertSql(base, hasLote);
    int total = executeBatchFromMap(insertSql, records, hasLote);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("ok", true);
    result.put("registros", total);
    return result;
  }

  private int executeBatchFromMap(String sql, List<Map<String, Object>> records, boolean hasLote) {
    int count = 0;
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      for (Map<String, Object> row : records) {
        ps.setString(1, safeMapGet(row, "serial"));
        ps.setString(2, safeMapGet(row, "codigoSap"));
        ps.setString(3, safeMapGet(row, "estadoSap"));
        ps.setString(4, safeMapGet(row, "estadoRR"));
        if (hasLote) {
          ps.setString(5, safeMapGet(row, "lote"));
        }
        ps.addBatch();
        count++;
        if (count % BATCH_SIZE == 0) {
          ps.executeBatch();
        }
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new BusinessRuleException("Error al insertar registros: " + e.getMessage());
    }
    return count;
  }

  private String safeMapGet(Map<String, Object> row, String key) {
    Object val = row.get(key);
    return val != null ? String.valueOf(val).trim() : null;
  }

  @Transactional
  public Map<String, Object> updateNotAvailable(List<UpdateNotAvailableRowDTO> rows) {
    Map<String, Object> result = new LinkedHashMap<>();
    int procesados = 0;
    List<String> errores = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
         CallableStatement cs = conn.prepareCall(
             "{call pa_UpdateEntryNotAvailable(?,?,?,?,?,?,?,?,?,?,?)}")) {

      for (UpdateNotAvailableRowDTO row : rows) {
        try {
          cs.setString(1, row.getSerial());
          cs.setString(2, row.getMac());
          if (row.getModeloId() != 0) {
            cs.setInt(3, row.getModeloId());
          } else {
            cs.setNull(3, java.sql.Types.INTEGER);
          }
          cs.setInt(4, row.getCodigoSapId());
          cs.setInt(5, row.getTipologiaId());
          if (row.getLoteId() != 0) {
            cs.setInt(6, row.getLoteId());
          } else {
            cs.setNull(6, java.sql.Types.INTEGER);
          }
          cs.setInt(7, row.getTipoOrigenId());
          cs.setInt(8, row.getOrigenId());
          cs.setString(9, row.getFecha());
          cs.setInt(10, row.getUsuarioIdMovimiento());
          cs.registerOutParameter(11, java.sql.Types.INTEGER);

          cs.execute();
          int filas = cs.getInt(11);
          if (filas > 0) {
            procesados++;
          } else {
            errores.add("No se pudo actualizar serial: " + row.getSerial());
          }
        } catch (Exception e) {
          errores.add("Error en serial " + row.getSerial() + ": " + e.getMessage());
        }
      }

      result.put("ok", errores.isEmpty());
      result.put("procesados", procesados);
      result.put("errores", errores);
    } catch (SQLException e) {
      throw new BusinessRuleException("Error al procesar actualización: " + e.getMessage());
    }
    return result;
  }
}
