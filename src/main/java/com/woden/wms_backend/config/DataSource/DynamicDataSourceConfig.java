package com.woden.wms_backend.config.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.woden.wms_backend.repositories", entityManagerFactoryRef = "dynamicEntityManagerFactory", transactionManagerRef = "dynamicTransactionManager")
public class DynamicDataSourceConfig {
  @Autowired
  private Environment env;

  private final ConcurrentHashMap<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();

  @Bean
  @Primary
  public DataSource dataSource() {
    // 1. Crear data source principal
    HikariDataSource defaultDataSource = createDataSource("WmsWdGeneral");
    dataSourceMap.put("WmsWdGeneral", defaultDataSource);

    // 2. Configurar routing data source
    AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        String clientDb = ClientDatabaseContext.getCurrentClientDb();
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
          String uri = request.getRequestURI();
          if (uri.contains("/current-connections") || uri.contains("/switch-client")) {
            return "WmsWdGeneral";
          }
        }
        return clientDb;
      }

      private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
          return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
      }

      @SuppressWarnings("null")
      @Override
      protected DataSource determineTargetDataSource() {
        String dbName = (String) determineCurrentLookupKey();
        if (dbName == null || "WmsWdGeneral".equals(dbName)) {
          return defaultDataSource;
        }

        return dataSourceMap.computeIfAbsent(dbName, this::createAndRegisterDataSource);
      }

      private HikariDataSource createAndRegisterDataSource(String dbName) {
        HikariDataSource newDataSource = createDataSource(dbName);
        // Actualizar los data sources conocidos
        setTargetDataSources(new HashMap<>(dataSourceMap));
        afterPropertiesSet();
        return newDataSource;
      }
    };

    routingDataSource.setDefaultTargetDataSource(defaultDataSource);
    routingDataSource.setTargetDataSources(new HashMap<>(dataSourceMap));
    routingDataSource.afterPropertiesSet();

    return routingDataSource;
  }

  @SuppressWarnings("null")
  private HikariDataSource createDataSource(String databaseName) {
    HikariConfig config = new HikariConfig();

    // Configuración básica de conexión
    String baseUrl = env.getProperty("spring.datasource.url")
        .replace("databaseName=WmsWdGeneral", "databaseName=" + databaseName);
    config.setJdbcUrl(baseUrl);
    config.setUsername(env.getProperty("spring.datasource.username"));
    config.setPassword(env.getProperty("spring.datasource.password"));
    config.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));

    // Configuración del pool de conexiones
    config.setPoolName("HikariPool-" + databaseName); // Nombre identificativo
    config.setMaximumPoolSize(env.getProperty("spring.datasource.hikari.maximum-pool-size", Integer.class, 10));
    config.setMinimumIdle(env.getProperty("spring.datasource.hikari.minimum-idle", Integer.class, 2));
    config.setConnectionTimeout(env.getProperty("spring.datasource.hikari.connection-timeout", Long.class, 30000L));
    config.setIdleTimeout(env.getProperty("spring.datasource.hikari.idle-timeout", Long.class, 600000L)); // 10 minutos
    config.setMaxLifetime(env.getProperty("spring.datasource.hikari.max-lifetime", Long.class, 1800000L)); // 30 minutos

    // Configuración para SQL Server optimizada
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    config.addDataSourceProperty("useLocalSessionState", "true");
    config.addDataSourceProperty("rewriteBatchedStatements", "true");
    config.addDataSourceProperty("cacheResultSetMetadata", "true");
    config.addDataSourceProperty("cacheServerConfiguration", "true");
    config.addDataSourceProperty("elideSetAutoCommits", "true");
    config.addDataSourceProperty("maintainTimeStats", "false");

    // Validación de conexiones
    config.setConnectionTestQuery("SELECT 1");
    config.setInitializationFailTimeout(30000); // 30 segundos para fallo inicial
    config.setLeakDetectionThreshold(60000); // Detección de leaks a 60 segundos

    return new HikariDataSource(config);
  }

  @Bean(name = "dynamicEntityManagerFactory")
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("com.woden.wms_backend.models.WmsWdGeneral",
        "com.woden.wms_backend.models.Entity", "com.woden.wms_backend.models.WmsWdAplicaciones");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
    properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));

    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean(name = "dynamicTransactionManager")
  @Primary
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
    return transactionManager;
  }

  // Métodos para verificar estado
  public boolean isDataSourceActive(String dataSourceName) {
    HikariDataSource ds = dataSourceMap.get(dataSourceName);
    return ds != null && !ds.isClosed();
  }

  public Map<String, String> getDataSourceStatus(String dataSourceName) {
    Map<String, String> status = new HashMap<>();
    HikariDataSource ds = dataSourceMap.get(dataSourceName);

    if (ds != null) {
      status.put("status", ds.isClosed() ? "INACTIVE" : "READY");
      status.put("poolName", ds.getPoolName());
      status.put("activeConnections", String.valueOf(ds.getHikariPoolMXBean().getActiveConnections()));
      status.put("idleConnections", String.valueOf(ds.getHikariPoolMXBean().getIdleConnections()));
    } else {
      status.put("status", "NOT_CREATED");
    }

    return status;
  }

  public List<String> getActiveDataSources() {
    return dataSourceMap.keySet().stream()
        .filter(key -> isDataSourceActive(key))
        .collect(Collectors.toList());
  }

  public void initializeClientDataSource(String databaseName) {
    if (!dataSourceMap.containsKey(databaseName)) {
      synchronized (this) {
        if (!dataSourceMap.containsKey(databaseName)) {
          HikariDataSource ds = createDataSource(databaseName);
          dataSourceMap.put(databaseName, ds);
        }
      }
    }
  }
}