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
// import org.springframework.context.annotation.Primary;
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
// @EnableJpaRepositories(
//     basePackages = "com.woden.wms_backend.repositories.WmsWdGeneral",
//     entityManagerFactoryRef = "wmsWdGeneralEntityManagerFactory",
//     transactionManagerRef = "wmsWdGeneralTransactionManager"
// )
// public class WmsWdGeneralDataSourceConfig {

//     // @Autowired
//     // private Environment env;
//     // @Primary
//     // @Bean(name = "wmsWdGeneralDataSource")
//     // public DataSource wmsWdGeneralDataSource() {
//     //     DriverManagerDataSource dataSource = new DriverManagerDataSource();
//     //     dataSource.setUrl(env.getProperty("spring.datasource.url"));
//     //     dataSource.setUsername(env.getProperty("spring.datasource.username"));
//     //     dataSource.setPassword(env.getProperty("spring.datasource.password"));
//     //     dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
//     //     return dataSource;
//     // }
//     // @Primary
//     // @Bean(name = "wmsWdGeneralEntityManagerFactory")
//     // public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//     //     LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//     //     em.setDataSource(wmsWdGeneralDataSource());
//     //     em.setPackagesToScan("com.woden.wms_backend.models.WmsWdGeneral");
//     //     HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//     //     em.setJpaVendorAdapter(vendorAdapter);
//     //     HashMap<String, Object> properties = new HashMap<>();
//     //     properties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
//     //     properties.put("hibernate.dialect", env.getProperty("spring.database-platform"));
//     //     em.setJpaPropertyMap(properties);
//     //     return em;
//     // }
//     // @Primary
//     // @Bean(name = "wmsWdGeneralTransactionManager")
//     // public PlatformTransactionManager transactionManager() {
//     //     JpaTransactionManager transactionManager = new JpaTransactionManager();
//     //     transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//     //     return transactionManager;
//     // }


//     @Primary
//     @Bean(name = "wmsWdGeneralDataSource")
//     @ConfigurationProperties("spring.datasource.wms-wd-general")
//     public DataSource dataSource() {
//         return DataSourceBuilder.create().build();
//     }

//     @Primary
//     @Bean(name = "wmsWdGeneralEntityManagerFactory")
//     public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//             EntityManagerFactoryBuilder builder,
//             @Qualifier("wmsWdGeneralDataSource") DataSource dataSource) {
//         return builder
//                 .dataSource(dataSource)
//                 .packages("com.woden.wms_backend.models.wmswdgeneral") // Asegúrate de que este paquete sea correcto
//                 .persistenceUnit("wmsWdGeneral")
//                 .build();
//     }

//     @Primary
//     @Bean(name = "wmsWdGeneralTransactionManager")
//     public PlatformTransactionManager transactionManager(
//             @Qualifier("wmsWdGeneralEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//         return new JpaTransactionManager(entityManagerFactory);
//     }
// }

