package com.epam.esm.config;

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
