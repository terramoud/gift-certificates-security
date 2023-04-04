package com.epam.esm.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for service layer tests.
 * Used to configure Spring Boot and scan required packages.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.epam.esm.repository")
@EntityScan("com.epam.esm.domain")
@EnableJpaRepositories("com.epam.esm.repository.api")
public class ServiceTestConfig {

}
