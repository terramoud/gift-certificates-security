package com.epam.esm.domain.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationConstants {
    public static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String CERTIFICATE_ON_CREATE_VIOLATION = "certificate.id.on-create.violation";
    public static final String CERTIFICATE_INVALID_ID = "certificate id must be positive integer";
    public static final String CERTIFICATE_INVALID_NAME = "";
    public static final String CERTIFICATE_INVALID_DESCRIPTION = "";
    public static final String CERTIFICATE_INVALID_PRICE = "";
    public static final String CERTIFICATE_INVALID_DURATION = "";
    public static final String CERTIFICATE_ID_NULL = "";
    public static final String CERTIFICATE_NAME_NULL = "";
    public static final String CERTIFICATE_DESCRIPTION_NULL = "";
    public static final String CERTIFICATE_PRICE_NULL = "";
    public static final String CERTIFICATE_SMALL_PRICE = "";
    public static final String CERTIFICATE_DURATION_NULL = "";

    public static final String ORDER_ON_CREATE_VIOLATION = "";
    public static final String ORDER_INVALID_ID = "";
    public static final String ORDER_INVALID_COST = "";
    public static final String ORDER_SMALL_COST = "";
    public static final String ORDER_ID_NULL = "";
    public static final String ORDER_COST_NULL = "";

    public static final String TAG_ON_CREATE_VIOLATION = "";
    public static final String TAG_INVALID_ID = "";
    public static final String TAG_INVALID_NAME = "";
    public static final String TAG_ID_NULL = "";
    public static final String TAG_NAME_NULL = "";

    public static final String USER_ON_CREATE_VIOLATION = "";
    public static final String USER_INVALID_ID = "";
    public static final String USER_INVALID_LOGIN = "";
    public static final String USER_INVALID_EMAIL = "";
    public static final String USER_INVALID_PASSWORD = "";
    public static final String USER_ID_NULL = "";
    public static final String USER_LOGIN_NULL = "";
    public static final String USER_EMAIL_NULL = "";
    public static final String USER_PASSWORD_NULL = "";
    public static final String USER_ROLE_NULL = "";

    public static final String INVALID_PAGE = "";
    public static final String INVALID_LIMIT_SIZE = "";

    public static final String ENTITY_NAME_REGEXP = "^[\\p{L}][\\p{L} \\-']{0,30}[\\p{L}]$";
    public static final String ENTITY_BIG_TEXT_REGEXP = "^[\\p{L}]{3}[^<>]{0,500}";
    public static final String EMAIL_REGEXP = "^\\w+([.-]?\\w+){0,249}@\\w+([.-]?\\w+){0,249}(\\.\\w{2,3}){1,249}$";
    public static final String LOGIN_REGEXP = "^[\\p{L}][\\p{L}0-9]{2,31}$";
    public static final String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,31}$";
}
