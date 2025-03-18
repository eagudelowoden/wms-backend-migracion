// package com.woden.wms_backend.config.DataSource;

// import java.util.HashMap;
// import java.util.Map;

// import javax.sql.DataSource;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;

// @Configuration
// public class DynamicDataSourceConfig {

//   @Bean
//   @Primary
//   public DataSource dataSource() {
//     Map<Object, Object> targetDataSources = new HashMap<>();

//     // Cargar todos los datasources que necesitas
//     targetDataSources.put("GENERAL",
//         buildDataSource(
//             "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdGeneral",
//             "readermssql", "@e1aec2bc69bd4b1e"));
//     targetDataSources.put("LEGACY_CR",
//         buildDataSource(
//             "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdLegacyCostaRica",
//             "readermssql", "@e1aec2bc69bd4b1e"));
//     targetDataSources.put("TIGO_BOG",
//         buildDataSource(
//             "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdTigoColombiaBOG",
//             "readermssql", "@e1aec2bc69bd4b1e"));
//     targetDataSources.put("TIGO_MED",
//         buildDataSource(
//             "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdTigoColombiaMED",
//             "readermssql", "@e1aec2bc69bd4b1e"));
//     targetDataSources.put("TELEFONICA_BOG", buildDataSource(
//         "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdTelefonicaColombiaBOG",
//         "readermssql", "@e1aec2bc69bd4b1e"));
//     targetDataSources.put("TELEFONICA_REWORK", buildDataSource(
//         "jdbc:sqlserver://wd-wms-prd.cyeyhpu1wzr0.us-east-2.rds.amazonaws.com:14331;databaseName=WmsWdTelefonicaColombiaRework",
//         "readermssql", "@e1aec2bc69bd4b1e"));

//     DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
//     dataSource.setDefaultTargetDataSource(targetDataSources.get("GENERAL"));
//     dataSource.setTargetDataSources(targetDataSources);
//     dataSource.afterPropertiesSet();
//     return dataSource;
//   }

//   private DataSource buildDataSource(String url, String username, String password) {
//     DriverManagerDataSource ds = new DriverManagerDataSource();
//     ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

//     ds.setUrl(url + ";trustServerCertificate=true");
//     ds.setUsername(username);
//     ds.setPassword(password);
//     return ds;
//   }
// }
