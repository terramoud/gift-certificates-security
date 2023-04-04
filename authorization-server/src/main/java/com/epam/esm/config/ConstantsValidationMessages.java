package com.epam.esm.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *  This class contains constant validation
 *  messages used throughout the application.
 *
 *  @author Oleksandr Koreshev
 *  @since 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantsValidationMessages {
    public static final String BAD_PASSWORD_CREDENTIALS = "bad.password.credentials";
    public static final String USER_INVALID_ID = "user.invalid.id";
    public static final String USER_INVALID_LOGIN = "user.invalid.login";
    public static final String USER_INVALID_EMAIL = "user.invalid.email";
    public static final String USER_INVALID_PASSWORD = "user.invalid.password";
    public static final String USER_ID_NULL = "user.id.null";
    public static final String USER_LOGIN_NULL = "user.login.null";
    public static final String USER_EMAIL_NULL = "user.email.null";
    public static final String USER_PASSWORD_NULL = "user.password.null";
    public static final String USER_ROLE_NULL = "user.role.null";
    public static final String USER_NOT_FOUND = "user.not.found";
    public static final String EMAIL_REGEXP = "^\\w+([.-]?\\w+){0,249}@\\w+([.-]?\\w+){0,249}(\\.\\w{2,3}){1,249}$";
    public static final String LOGIN_REGEXP = "^[\\p{L}][\\p{L}0-9]{2,31}$";
    public static final String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,31}$";
}
