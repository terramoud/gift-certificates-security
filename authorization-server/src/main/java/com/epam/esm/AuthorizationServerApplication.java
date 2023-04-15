package com.epam.esm;

import com.epam.esm.exceptions.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * This class is the entry point of the application.
 * It configures the Spring Boot application, and sets up
 * a message source bean and a translator bean.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@SpringBootApplication
@EnableJpaRepositories
public class AuthorizationServerApplication extends SpringBootServletInitializer {

    @Value("${message.source}")
    private String messagesBundle;

    @Value("${message.source.default-encoding}")
    private String defaultEncoding;

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AuthorizationServerApplication.class);
    }

    /**
     * Configures a ResourceBundleMessageSource bean to be
     * used as the message source for the internationalization
     *
     * @return the message source bean
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
     * Configures a Translator bean that uses the message
     * source bean to translate messages
     *
     * @return the translator bean
     */
    @Bean
    public Translator translator() {
        return new Translator(messageSource());
    }
}
