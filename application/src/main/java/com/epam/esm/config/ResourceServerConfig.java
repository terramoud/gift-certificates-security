package com.epam.esm.config;

import com.epam.esm.exceptions.CustomAuthExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for the resource server, which defines the security filter
 * chain and JWT authentication converter.
 * This class is annotated with @Configuration to indicate that it is a configuration class,
 * and with @EnableWebSecurity and @EnableGlobalMethodSecurity(prePostEnabled = true) to enable
 * web security and global method security with pre/post annotations, respectively.
 * The class defines the following beans:
 * securityFilterChain: a SecurityFilterChain bean that configures the authentication
 * entry point, access denied handler, authorization rules for API endpoints,
 * and the OAuth2 resource server with JWT authentication.
 * The bean returns the configured HttpSecurity object.
 * jwtAuthenticationConverter: a JwtAuthenticationConverter bean that converts
 * the JWT token to an authentication object with granted authorities.
 * roleHierarchy: a RoleHierarchy bean that defines the role hierarchy.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig {

    /**
     * Configures the security filter chain for the resource server.
     *
     * @param http the HttpSecurity object to configure
     * @return the SecurityFilterChain object with the configured filters
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthExceptionHandler())
                .accessDeniedHandler(new CustomAuthExceptionHandler())
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/v1/tags").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/v1/tags/*").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/v1/tags/{tagId}/gift-certificates").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/v1/tags/name/{tagName}/gift-certificates").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/v1/gift-certificates").permitAll()
            .mvcMatchers(HttpMethod.GET, "/api/v1/gift-certificates/*").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt();
        return http.build();
    }

    /**
     * Creates a JwtAuthenticationConverter bean that converts the JWT token to an authentication object with granted authorities.
     *
     * @return the JwtAuthenticationConverter object with the configured JwtGrantedAuthoritiesConverter object
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();
        roleConverter.setAuthoritiesClaimName("roles");
        roleConverter.setAuthorityPrefix("ROLE_");
        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter =
                new DelegatingJwtGrantedAuthoritiesConverter(roleConverter, defaultConverter);
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtConverter;
    }

    /**
     * Creates a RoleHierarchy bean that defines the role hierarchy.
     *
     * @return the RoleHierarchy object with the configured role hierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}