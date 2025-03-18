// package com.woden.wms_backend.config.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.orm.jpa.JpaTransactionManager;
// import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
// import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.annotation.EnableTransactionManagement;

// import javax.sql.DataSource;
// import java.util.HashMap;
// import java.util.Map;

// @Configuration
// @EnableTransactionManagement
// @EnableJpaRepositories(basePackages = "com.woden.wms_backend.repositories.ClienteRepositories", // Repositorios comunes
//     entityManagerFactoryRef = "wmsWdEntityManager", transactionManagerRef = "wmsWdTransactionManager")
// public class WmsWdJpaConfig {

//   @Autowired
//   private DataSource wmsWdDynamicDataSource;
//   @Autowired
//   private Environment env;

//   @Bean
//   public LocalContainerEntityManagerFactoryBean wmsWdEntityManager() {
//     LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//     em.setDataSource(wmsWdDynamicDataSource);
//     em.setPackagesToScan("com.woden.wms_backend.models.Entity"); // Entidades comunes

//     HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//     em.setJpaVendorAdapter(vendorAdapter);

//     Map<String, Object> properties = new HashMap<>();
//     properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
//     properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));

//     em.setJpaPropertyMap(properties);
//     return em;
//   }

//   @Bean
//   public PlatformTransactionManager wmsWdTransactionManager() {
//     JpaTransactionManager transactionManager = new JpaTransactionManager();
//     transactionManager.setEntityManagerFactory(wmsWdEntityManager().getObject());
//     return transactionManager;
//   }
// }