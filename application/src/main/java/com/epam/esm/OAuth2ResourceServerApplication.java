package com.epam.esm;

import com.epam.esm.exceptions.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "com.epam.esm")
public class OAuth2ResourceServerApplication {

    @Value("${sort.default.entity-field}")
    public String id;

    @Value("${message.source}")
    private String messagesBundle;

    @Value("${message.source.default-encoding}")
    private String defaultEncoding;

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApplication.class, args);
    }

    /**
     * Set the list of resource files here
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

    @Bean
    public Translator translator() {
        return new Translator(messageSource());
    }

    @Bean
    SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
        return s -> s.setFallbackSort(Sort.by(id));
    }
}
