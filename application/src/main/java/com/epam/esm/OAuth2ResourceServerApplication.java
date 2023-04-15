package com.epam.esm;

import com.epam.esm.exceptions.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This class is the main entry point for the
 * OAuth2 Resource Server application. It initializes
 * the Spring Boot application and sets up the necessary
 * configurations to enable web MVC and JPA repositories.
 *
 * author: Oleksandr Koreshev
 * since: 1.0
 */
@EnableWebMvc
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "com.epam.esm")
public class OAuth2ResourceServerApplication extends SpringBootServletInitializer {

    @Value("${sort.default.entity-field}")
    public String id;

    @Value("${message.source}")
    private String messagesBundle;

    @Value("${message.source.default-encoding}")
    private String defaultEncoding;

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OAuth2ResourceServerApplication.class);
    }

    /**
     * Creates a ResourceBundleMessageSource bean that
     * is used for internationalization.
     *
     * @return resourceBundleMessageSource
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasenames(messagesBundle);
        rs.setDefaultEncoding(defaultEncoding);
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }

    /**
     * Creates a Translator bean that is used for translating messages.
     * @return the Translator bean
     */
    @Bean
    public Translator translator() {
        return new Translator(messageSource());
    }

    /**
     * Creates a SortHandlerMethodArgumentResolverCustomizer
     * bean that is used for sorting results.
     * @return the SortHandlerMethodArgumentResolverCustomizer bean
     */
    @Bean
    SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
        return s -> s.setFallbackSort(Sort.by(id));
    }
}
