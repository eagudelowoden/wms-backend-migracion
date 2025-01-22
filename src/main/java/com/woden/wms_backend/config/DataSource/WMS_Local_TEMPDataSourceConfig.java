// package com.woden.wms_backend.config.DataSource;

// import java.util.HashMap;

// import javax.sql.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.jdbc.DataSourceBuilder;
// import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;
// import org.springframework.orm.jpa.JpaTransactionManager;
// import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
// import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.annotation.EnableTransactionManagement;

// import jakarta.persistence.EntityManagerFactory;

// @Configuration
// @EnableTransactionManagement
// @EnableJpaRepositories(basePackages = "com.woden.wms_backend.repositories.wmslocaltemp", entityManagerFactoryRef = "wmsLocalTempEntityManagerFactory", transactionManagerRef = "wmsLocalTempTransactionManager")
// public class WMS_Local_TEMPDataSourceConfig {

//     // @Autowired
//     // private Environment env;
//     // @Bean(name = "wmsLocalTempDataSource")
//     // public DataSource wmsLocalTempDataSource() {
//     // DriverManagerDataSource dataSource = new DriverManagerDataSource();
//     // dataSource.setUrl(env.getProperty("db2.datasource.url"));
//     // dataSource.setUsername(env.getProperty("db2.datasource.username"));
//     // dataSource.setPassword(env.getProperty("db2.datasource.password"));
//     // dataSource.setDriverClassName(env.getProperty("db2.datasource.driver-class-name"));
//     // return dataSource;
//     // }
//     // @Bean(name = "wmsLocalTempEntityManagerFactory")
//     // public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//     // LocalContainerEntityManagerFactoryBean em = new
//     // LocalContainerEntityManagerFactoryBean();
//     // em.setDataSource(wmsLocalTempDataSource());
//     // em.setPackagesToScan("com.woden.wms_backend.models.WmsLocalTemp");
//     // HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//     // em.setJpaVendorAdapter(vendorAdapter);
//     // HashMap<String, Object> properties = new HashMap<>();
//     // properties.put("hibernate.show_sql", env.getProperty("db2.jpa.show-sql"));
//     // properties.put("hibernate.dialect",
//     // env.getProperty("db2.database-platform"));
//     // em.setJpaPropertyMap(properties);
//     // return em;
//     // }
//     // @Bean(name = "wmsLocalTempTransactionManager")
//     // public PlatformTransactionManager transactionManager() {
//     // JpaTransactionManager transactionManager = new JpaTransactionManager();
//     // transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//     // return transactionManager;
//     // }

//     @Bean(name = "wmsLocalTempDataSource11")
//     @ConfigurationProperties("spring.datasource.wms-local-temp")
//     public DataSource dataSource() {
//         return DataSourceBuilder.create().build();
//     }

//     @Bean(name = "wmsLocalTempEntityManagerFactory1")
//     public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//             EntityManagerFactoryBuilder builder,
//             @Qualifier("wmsLocalTempDataSource") DataSource dataSource) {
//         return builder
//                 .dataSource(dataSource)
//                 .packages("com.woden.wms_backend.models.wmslocaltemp") // Asegúrate de que este paquete sea correcto
//                 .persistenceUnit("wmsLocalTemp")
//                 .build();
//     }

//     @Bean(name = "wmsLocalTempTransactionManager1")
//     public PlatformTransactionManager transactionManager(
//             @Qualifier("wmsLocalTempEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//         return new JpaTransactionManager(entityManagerFactory);
//     }
// }
