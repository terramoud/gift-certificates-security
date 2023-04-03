package com.epam.esm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.epam.esm.repository")
@EntityScan("com.epam.esm.domain")
@EnableJpaRepositories("com.epam.esm.repository.api")
@PropertySource("classpath:test.properties")
public class RepositoryTestConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.hikari.driver-class-name}") String mysqlDriver,
            @Value("${spring.datasource.username}") String userName,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.url}") String url) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(mysqlDriver);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }
}
