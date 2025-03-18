// package com.woden.wms_backend.config.DataSource;

// import org.springframework.core.env.Environment;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;
// import org.springframework.orm.jpa.JpaTransactionManager;
// import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
// import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.annotation.EnableTransactionManagement;

// import java.util.HashMap;
// import java.util.Map;

// import javax.sql.DataSource;

// @Configuration
// @EnableTransactionManagement
// @EnableJpaRepositories(basePackages = {
//         "com.woden.wms_backend.repositories.ClienteRepositories" }, entityManagerFactoryRef = "tigoColombiaBogEntityManager", transactionManagerRef = "tigoColombiaBogTransactionManager")
// public class WmsWdTigoColombiaBogConfig  {

//     @Autowired
//     private Environment env;

//     @Bean(name = "tigoColombiaBogDataSource")
//     public DataSource tigoColombiaBogDataSource() {
//         DriverManagerDataSource dataSource = new DriverManagerDataSource();
//         dataSource.setDriverClassName(env.getProperty("tigo.colombia.bog.datasource.driver-class-name"));
//         dataSource.setUrl(env.getProperty("tigo.colombia.bog.datasource.url"));
//         dataSource.setUsername(env.getProperty("tigo.colombia.bog.datasource.username"));
//         dataSource.setPassword(env.getProperty("tigo.colombia.bog.datasource.password"));
//         return dataSource;
//     }

//     @Bean(name = "tigoColombiaBogEntityManager")
//     public LocalContainerEntityManagerFactoryBean tigoColombiaBogEntityManager() {
//         LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//         em.setDataSource(tigoColombiaBogDataSource());
//         em.setPackagesToScan(new String[] { "com.woden.wms_backend.models.Entity" });

//         HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//         em.setJpaVendorAdapter(vendorAdapter);

//         Map<String, Object> properties = new HashMap<>();
//         properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
//         properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        
//         em.setJpaPropertyMap(properties);
//         return em;
//     }

//     @Bean(name = "tigoColombiaBogTransactionManager")
//     public PlatformTransactionManager tigoColombiaBogTransactionManager() {
//         JpaTransactionManager transactionManager = new JpaTransactionManager();
//         transactionManager.setEntityManagerFactory(tigoColombiaBogEntityManager().getObject());
//         return transactionManager;
//     }
// }