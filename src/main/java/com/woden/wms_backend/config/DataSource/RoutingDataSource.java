package com.woden.wms_backend.config.DataSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

  private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
  private final Environment env;

  public RoutingDataSource(DataSource defaultDataSource, Environment env) {
    this.env = env;
    this.targetDataSources.put("WmsWdGeneral", defaultDataSource);
    this.setDefaultTargetDataSource(defaultDataSource);
    this.setTargetDataSources(targetDataSources);
  }

  @Override
  protected Object determineCurrentLookupKey() {
    String clientDb = ClientDatabaseContext.getCurrentClientDb();
    System.out.println("RoutingDataSource - clientDb: " + clientDb);
    System.out.println("RoutingDataSource - clientName: " + ClientDatabaseContext.getCurrentClientName());
    System.out.println("RoutingDataSource - clientId: " + ClientDatabaseContext.getCurrentClientId());
    System.out.println("RoutingDataSource - isUsingGeneralDb: " + ClientDatabaseContext.isUsingGeneralDb());

    System.out.println("clientDb: " + clientDb);
    if (clientDb != null && !targetDataSources.containsKey(clientDb)) {
      synchronized (this) {
        if (!targetDataSources.containsKey(clientDb)) {
          DataSource newDataSource = createDataSource(clientDb);
          targetDataSources.put(clientDb, newDataSource);
          this.setTargetDataSources(targetDataSources);
          this.afterPropertiesSet();
        }
      }
    }

    return clientDb;
  }

  @SuppressWarnings("null")
  private DataSource createDataSource(String databaseName) {
    HikariConfig config = new HikariConfig();
    String baseUrl = env.getProperty("spring.datasource.url")
        .replace("databaseName=WmsWdGeneral", "databaseName=" + databaseName);

    config.setJdbcUrl(baseUrl);
    config.setUsername(env.getProperty("spring.datasource.username"));
    config.setPassword(env.getProperty("spring.datasource.password"));
    config.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));

    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(30000);
    config.setIdleTimeout(600000);
    config.setMaxLifetime(1800000);
    // config.setConnectionTestQuery("SELECT 1");

    return new HikariDataSource(config);
  }

  public boolean isDataSourceActive(String dataSourceName) {
    if (targetDataSources.containsKey(dataSourceName)) {
      DataSource ds = (DataSource) targetDataSources.get(dataSourceName);
      return ds instanceof HikariDataSource && !((HikariDataSource) ds).isClosed();
    }
    return false;
  }

  public List<String> getActiveDataSources() {
    return targetDataSources.keySet().stream()
        .map(Object::toString)
        .collect(Collectors.toList());
  }
}
