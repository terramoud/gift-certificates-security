package com.epam.esm.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class contains constants for OAuth2 properties used
 * in the application. These properties include client id,
 * client secret, redirect URIs, scopes, JWT algorithm name,
 * key size, user roles claim, and issuer.
 *
 *  @author Oleksandr Koreshev
 *  @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2Properties {
    public static final String CLIENT_ID = "${oauth2.client.id}";
    public static final String CLIENT_SECRET = "${oauth2.client.secret}";
    public static final String REDIRECT_URI_LOGIN = "${oauth2.client.redirect-uri-login}";
    public static final String REDIRECT_URI_AUTHORIZED = "${oauth2.client.redirect-uri-authorized}";
    public static final String SCOPE_READ = "${oauth2.client.scope-read}";
    public static final String SCOPE_WRITE = "${oauth2.client.scope-write}";
    public static final String JWT_ALGORITHM_NAME = "${jwt.algorithm.name}";
    public static final String JWT_ALGORITHM_KEY_SIZE = "${jwt.algorithm.key.size}";
    public static final String JWT_CLAIM_USER_ROLES = "${jwt.claim.user.roles}";
    public static final String OAUTH2_ISSUER = "${oauth2.issuer}";
}
