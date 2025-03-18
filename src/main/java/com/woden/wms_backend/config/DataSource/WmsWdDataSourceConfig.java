// package com.woden.wms_backend.config.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;

// import javax.sql.DataSource;
// import java.util.HashMap;
// import java.util.Map;

// @Configuration
// public class WmsWdDataSourceConfig {

//   @Autowired
//   private Environment env;

//   @Bean
//   public DataSource wmsWdDynamicDataSource() {
//     WmsWdDynamicDataSource dataSource = new WmsWdDynamicDataSource();

//     // Configura los DataSources disponibles
//     Map<Object, Object> targetDataSources = new HashMap<>();
//     targetDataSources.put("WmsWdGeneral", wmsWdGeneralDataSource());
//     targetDataSources.put("WmsWdLegacyCostaRica", wmsWdLegacyCostaRicaDataSource());
//     targetDataSources.put("WmsWdTigoColombiaBOG", wmsWdTigoColombiaBogDataSource());
//     targetDataSources.put("WmsWdTigoColombiaMED", wmsWdTigoColombiaMedDataSource());
//     targetDataSources.put("WmsWdTelefonicaColombiaBOG", wmsWdTelefonicaColombiaBogDataSource());
//     targetDataSources.put("WmsWdTelefonicaColombiaRework", wmsWdTelefonicaColombiaReworkDataSource());

//     dataSource.setDefaultTargetDataSource(wmsWdGeneralDataSource()); // DataSource por defecto
//     dataSource.setTargetDataSources(targetDataSources);
//     dataSource.afterPropertiesSet();
//     return dataSource;
//   }

//   // Métodos para crear cada DataSource
//   @Bean
//   public DataSource wmsWdGeneralDataSource() {
//     return createDataSource("wms.datasource.general");
//   }

//   @Bean
//   public DataSource wmsWdLegacyCostaRicaDataSource() {
//     return createDataSource("wms.datasource.legacy-cr");
//   }

//   @Bean
//   public DataSource wmsWdTigoColombiaBogDataSource() {
//     return createDataSource("wms.datasource.tigo-bog");
//   }

//   @Bean
//   public DataSource wmsWdTigoColombiaMedDataSource() {
//     return createDataSource("wms.datasource.tigo-med");
//   }

//   @Bean
//   public DataSource wmsWdTelefonicaColombiaBogDataSource() {
//     return createDataSource("wms.datasource.telefonica-bog");
//   }

//   @Bean
//   public DataSource wmsWdTelefonicaColombiaReworkDataSource() {
//     return createDataSource("wms.datasource.telefonica-rework");
//   }

//   // Método auxiliar para crear un DataSource
//   private DataSource createDataSource(String prefix) {
//     DriverManagerDataSource dataSource = new DriverManagerDataSource();
//     dataSource.setDriverClassName(env.getProperty(prefix + ".driver-class-name"));
//     dataSource.setUrl(env.getProperty(prefix + ".url"));
//     dataSource.setUsername(env.getProperty(prefix + ".username"));
//     dataSource.setPassword(env.getProperty(prefix + ".password"));
//     return dataSource;
//   }
// }