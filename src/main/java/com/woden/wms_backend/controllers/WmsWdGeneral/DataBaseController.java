package com.woden.wms_backend.controllers.WmsWdGeneral;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woden.wms_backend.config.DataSource.ClientDatabaseContext;
import com.woden.wms_backend.config.DataSource.DynamicDataSourceConfig;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteDataAccessService;
import com.woden.wms_backend.services.WmsWdGeneral.ClienteService;

@RestController
@RequestMapping("/api")
public class DataBaseController {

  private final DynamicDataSourceConfig dynamicDataSourceConfig;

  public DataBaseController(DynamicDataSourceConfig dynamicDataSourceConfig,
      ClienteDataAccessService clienteDataAccesService, ClienteService clienteService) {
    this.dynamicDataSourceConfig = dynamicDataSourceConfig;
  }

  @GetMapping("/current-connections")
  public ResponseEntity<Map<String, Object>> getCurrentConnections() {
    Map<String, Object> response = new HashMap<>();

    // Información del contexto actual
    response.put("currentContext", getContextInfo());

    // Información de la BD general
    response.put("generalDatabase", getDatabaseInfo("WmsWdGeneral"));

    // Información de la BD del cliente actual
    String clientDb = ClientDatabaseContext.getCurrentClientDb();
    if (!"WmsWdGeneral".equals(clientDb)) {
      try {
        response.put("clientDatabase", getDatabaseInfo(clientDb));
      } catch (Exception e) {
        response.put("clientDatabase", "Error al obtener BD del cliente: " + e.getMessage());
      }
    } else {
      response.put("clientDatabase", "No hay cliente seleccionado");
    }

    // Lista de DataSources activos
    response.put("activeDataSources", dynamicDataSourceConfig.getActiveDataSources());
    return ResponseEntity.ok(response);
  }

  private Map<String, Object> getContextInfo() {
    Map<String, Object> contextInfo = new HashMap<>();
    String currentDb = ClientDatabaseContext.getCurrentClientDb();

    contextInfo.put("clientName", ClientDatabaseContext.getCurrentClientName());
    contextInfo.put("clientDb", currentDb);
    contextInfo.put("clientId", ClientDatabaseContext.getCurrentClientId());

    // ✅ Corrección para el flag
    boolean isGeneralDb = currentDb == null || "WmsWdGeneral".equalsIgnoreCase(currentDb);
    contextInfo.put("usingGeneralDb", isGeneralDb);

    return contextInfo;
  }

  private Map<String, Object> getDatabaseInfo(String dbName) {
    Map<String, Object> info = new HashMap<>();
    info.put("databaseName", dbName);
    info.put("connectionUrl", buildConnectionUrl(dbName));

    // Obtener información detallada del estado
    Map<String, String> dsStatus = dynamicDataSourceConfig.getDataSourceStatus(dbName);
    info.putAll(dsStatus);

    return info;
  }

  private String buildConnectionUrl(String dbName) {
    return "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;" +
        "databaseName=" + dbName + ";trustServerCertificate=true";
  }
}