package com.epam.esm.domain.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationConstants {
    public static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String CERTIFICATE_ON_CREATE_VIOLATION = "certificate.id.on-create.violation";
    public static final String CERTIFICATE_INVALID_ID = "certificate.invalid.id";
    public static final String CERTIFICATE_INVALID_NAME = "certificate.invalid.name";
    public static final String CERTIFICATE_INVALID_DESCRIPTION = "certificate.invalid.description";
    public static final String CERTIFICATE_INVALID_PRICE = "certificate.invalid.price";
    public static final String CERTIFICATE_INVALID_DURATION = "certificate.invalid.duration";
    public static final String CERTIFICATE_ID_NULL = "certificate.id.null";
    public static final String CERTIFICATE_NAME_NULL = "certificate.name.null";
    public static final String CERTIFICATE_DESCRIPTION_NULL = "certificate.description.null";
    public static final String CERTIFICATE_PRICE_NULL = "certificate.price.null";
    public static final String CERTIFICATE_DURATION_NULL = "certificate.duration.null";
    public static final String CERTIFICATE_NOT_FOUND = "certificate.not.found";
    public static final String CERTIFICATE_ID_NOT_MAPPED = "certificate.id.not.mapped";

    public static final String ORDER_ON_CREATE_VIOLATION = "order.id.on-create.violation";
    public static final String ORDER_INVALID_ID = "order.invalid.id";
    public static final String ORDER_INVALID_COST = "order.invalid.cost";
    public static final String ORDER_COST_NULL = "order.cost.null";
    public static final String ORDER_ID_NULL = "order.id.null";
    public static final String ORDER_NOT_FOUND = "order.not.found";
    public static final String CHANGE_FILLED_ORDER = "forbidden.change.filled.order";

    public static final String TAG_ON_CREATE_VIOLATION = "tag.id.on-create.violation";
    public static final String TAG_INVALID_ID = "tag.invalid.id";
    public static final String TAG_INVALID_NAME = "tag.invalid.name";
    public static final String TAG_ID_NULL = "tag.id.null";
    public static final String TAG_NAME_NULL = "tag.name.null";
    public static final String TAG_NOT_FOUND = "tag.not.found";
    public static final String TAG_ID_NOT_MAPPED = "tag.id.not.mapped";

    public static final String USER_ON_CREATE_VIOLATION = "user.id.on-create.violation";
    public static final String USER_INVALID_ID = "user.invalid.id";
    public static final String USER_INVALID_LOGIN = "user.invalid.login";
    public static final String USER_INVALID_EMAIL = "user.invalid.email";
    public static final String USER_INVALID_PASSWORD = "user.invalid.password";
    public static final String USER_ID_NULL = "user.id.null";
    public static final String USER_LOGIN_NULL = "user.login.null";
    public static final String USER_EMAIL_NULL = "user.email.null";
    public static final String USER_PASSWORD_NULL = "user.password.null";
    public static final String USER_ROLE_NULL = "user.role.null";
    public static final String USER_ID_NOT_MAPPED = "user.id.not.mapped";
    public static final String USER_NOT_FOUND = "user.not.found";

    public static final String INVALID_PAGE = "invalid.page.value";
    public static final String INVALID_SIZE = "invalid.size.limit";

    public static final String ENTITY_NAME_REGEXP = "^[\\p{L}][\\p{L} \\-']{0,30}[\\p{L}]$";
    public static final String EMAIL_REGEXP = "^\\w+([.-]?\\w+){0,249}@\\w+([.-]?\\w+){0,249}(\\.\\w{2,3}){1,249}$";
    public static final String LOGIN_REGEXP = "^[\\p{L}][\\p{L}0-9]{2,31}$";
    public static final String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,31}$";
}
