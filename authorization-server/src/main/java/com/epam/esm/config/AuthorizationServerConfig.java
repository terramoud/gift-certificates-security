package com.epam.esm.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@Import(DefaultSecurityConfig.class)
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            @Value(OAuth2Properties.CLIENT_ID) String clientId,
            @Value(OAuth2Properties.CLIENT_SECRET) String clientSecret,
            @Value(OAuth2Properties.REDIRECT_URI_LOGIN) String redirectUriLogin,
            @Value(OAuth2Properties.REDIRECT_URI_AUTHORIZED) String redirectUriAuthorized,
            @Value(OAuth2Properties.SCOPE_READ) String scopeRead,
            @Value(OAuth2Properties.SCOPE_WRITE) String scopeWrite) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(redirectUriLogin)
                .redirectUri(redirectUriAuthorized)
                .scope(OidcScopes.OPENID)
                .scope(scopeRead)
                .scope(scopeWrite)
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@Value(OAuth2Properties.JWT_ALGORITHM_NAME) String algorithmName,
                                                @Value(OAuth2Properties.JWT_ALGORITHM_KEY_SIZE) int keySize) {
        RSAKey rsaKey = generateRsa(algorithmName, keySize);
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa(String algorithmName, int keySize) {
        KeyPair keyPair = generateRsaKey(algorithmName, keySize);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey(String algName, int keySize) {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algName);
            keyPairGenerator.initialize(keySize);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public ProviderSettings providerSettings(@Value(OAuth2Properties.OAUTH2_ISSUER) String issuer) {
        return ProviderSettings.builder()
                .issuer(issuer)
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(
            @Value(OAuth2Properties.JWT_CLAIM_USER_ROLES) String userRoles) {
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                Authentication principal = context.getPrincipal();
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                context.getClaims().claim(userRoles, authorities);
            }
        };
    }
}