package com.epam.esm.config;

import com.epam.esm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * The DefaultSecurityConfig class provides the default
 * configuration for Spring Security in an OAuth2 authorization
 * server and for the default security filter chain.
 * This is a Java configuration class that defines security
 * settings for a web application using Spring Security.
 * The class is annotated with @EnableWebSecurity, which indicates
 * that it enables Spring Security's web security features.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@EnableWebSecurity
@AllArgsConstructor
public class DefaultSecurityConfig {

    private final UserService userDefaultService;

    /**
     * Configures the security filter chain for the OAuth2 authorization server
     *
     * @param http HttpSecurity instance
     * @return SecurityFilterChain instance
     * @throws Exception if an error occurs during the configuration process
     */
    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }

    /**
     * Configures the default security filter chain
     *
     * @param http HttpSecurity instance
     * @return SecurityFilterChain instance
     * @throws Exception if an error occurs during the configuration process
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/password").permitAll();
        http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest()
                .authenticated())
                .formLogin(withDefaults());
        return http.build();
    }

    /**
     * Provides an instance of DaoAuthenticationProvider
     * that uses custom implementation of {@link UserService}
     *
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDefaultService);
        return authenticationProvider;
    }

    /**
     * Provides an instance of PasswordEncoder.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
