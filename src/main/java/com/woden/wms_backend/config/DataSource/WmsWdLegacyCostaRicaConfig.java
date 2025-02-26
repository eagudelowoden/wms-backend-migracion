package com.woden.wms_backend.config.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        "com.woden.wms_backend.repositories.ClienteRepositories" }, entityManagerFactoryRef = "wmsLegacyCostaRicaEntityManager", transactionManagerRef = "wmsLegacyCostaRicaTransactionManager")
public class WmsWdLegacyCostaRicaConfig {

    @Autowired
    private Environment env;

    @Bean(name = "wmsLegacyCostaRicaDataSource")
    public DataSource wmsLegacyCostaRicaDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("cliente.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("cliente.datasource.url"));
        dataSource.setUsername(env.getProperty("cliente.datasource.username"));
        dataSource.setPassword(env.getProperty("cliente.datasource.password"));
        return dataSource;
    }

    @Bean(name = "wmsLegacyCostaRicaEntityManager")
    public LocalContainerEntityManagerFactoryBean wmsLegacyCostaRicaEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(wmsLegacyCostaRicaDataSource());
        em.setPackagesToScan(new String[] { "com.woden.wms_backend.models.Entity" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        properties.put("hibernate.dialect", env.getProperty("sprng.jpa.database-platform"));
        
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean(name = "wmsLegacyCostaRicaTransactionManager")
    public PlatformTransactionManager wmsLegacyCostaRicaTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(wmsLegacyCostaRicaEntityManager().getObject());
        return transactionManager;
    }
}