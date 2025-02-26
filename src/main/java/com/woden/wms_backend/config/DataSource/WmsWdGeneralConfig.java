package com.woden.wms_backend.config.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.woden.wms_backend.repositories.WmsWdGeneral" }, entityManagerFactoryRef = "wmsWdGeneralEntityManager", transactionManagerRef = "wmsWdGeneralTransactionManager")
public class WmsWdGeneralConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "wmsGeneralDataSource")
    public DataSource wmsGeneralDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Primary
    @Bean(name = "wmsWdGeneralEntityManager")
    public LocalContainerEntityManagerFactoryBean wmsGeneralEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(wmsGeneralDataSource());
        em.setPackagesToScan(new String[] { "com.woden.wms_backend.models.WmsWdGeneral" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        properties.put("hibernate.dialect", env.getProperty("sprng.jpa.database-platform"));
        
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean(name = "wmsWdGeneralTransactionManager")
    public PlatformTransactionManager wmsGeneralTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(wmsGeneralEntityManager().getObject());
        return transactionManager;
    }
}