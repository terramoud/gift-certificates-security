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

/**
 * Configuration class for Spring Boot testing of repositories.
 * <p>
 * Configures the necessary components for repository testing,
 * including: enabling auto-configuration, scanning
 * for repository components in the specified package,
 * entity scan for domain objects, enabling JPA repositories,
 * and loading properties from the test properties file.
 * </p>
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 * */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.epam.esm.repository")
@EntityScan("com.epam.esm.domain")
@EnableJpaRepositories("com.epam.esm.repository.api")
@PropertySource("classpath:test.properties")
public class RepositoryTestConfig {

    /**
     * Creates a data source for the test environment.
     *
     * @param mysqlDriver the MySQL driver class name
     * @param userName the username for the test database
     * @param password the password for the test database
     * @param url the JDBC URL for the test database
     * @return a data source for the test environment
     */
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
