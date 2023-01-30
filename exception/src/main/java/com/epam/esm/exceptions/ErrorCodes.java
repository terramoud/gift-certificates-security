package com.epam.esm.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {
    SUFFIX_RESPONSE_ENTITY_EXCEPTIONS(
            99,
            "Suffix for response request exception to create error custom code"),
    NOT_CHANGE_ROW(42201, "Any row of database isn't changed by query"),
    NOT_CREATE_ROW(42202, "Any row of database isn't created by query"),
    NOT_FOUND_TAG_RESOURCE(40401, "Unable to find tag in database"),
    NOT_FOUND_USER_RESOURCE(40405, "Unable to find user in database"),
    NOT_FOUND_CERTIFICATE_RESOURCE(40402, "Unable to find gift certificate in database"),
    NO_HANDLER_FOUND(40403, "Unable to find handler for api request"),
    NOT_FOUND_ORDER_RESOURCE(40404, "Unable to find order in database"),
    SQL_DUPLICATE_ENTRY(40001, "Attempting to create a duplicate entry in database"),
    SQL_NULL_ENTRY(40002, "Attempting to create a null row in database"),
    INVALID_CERTIFICATE_PROPERTY(40003, "Invalid gift certificate's property"),
    INVALID_TAG_PROPERTY(40004, "Invalid gift tag's property"),
    INVALID_ID_PROPERTY(40005, "Invalid gift certificate's id property"),
    INVALID_ORDER_ID_PROPERTY(400013, "Invalid order's id property"),
    INVALID_ORDER_PROPERTY(400014, "Invalid order's property"),
    INVALID_TAG_ID_PROPERTY(40006, "Invalid tag's id property"),
    INVALID_USER_ID_PROPERTY(400015, "Invalid user's id property"),
    INVALID_TAG_NAME_PROPERTY(40007, "Invalid tag's name property"),
    DATA_ACCESS_EXCEPTION(40009, "Unable get or put data to database"),
    DATA_INTEGRITY_VIOLATION(40010, "Cannot add or update row: a foreign key constraint fails"),
    INVALID_PAGINATION_PARAMETER(40011, "Invalid value of SQL parameters: LIMIT or OFFSET or ORDER BY"),
    INVALID_CERTIFICATE_NAME_PROPERTY(40012, "Invalid certificate's name property"),
    METHOD_ARGUMENT_CONSTRAINT_VIOLATION(40016, "Method argument constraint violation"),
    PATH_VARIABLE_CONSTRAINT_VIOLATION(40017, "Path variable constraint violation"),
    METHOD_ARGUMENT_TYPE_MISTMATCH(40018, "Cannot cast @PathVariable to necessary type"),
    INTERNAL_SERVER_ERROR(50001, "An error or exception occurred on the server side"),
    NULL_INSTEAD_LIST(50002, "Returns null instead of List<>"),
    SQL_ERROR(50003, "Default code for all sql exceptions");

    private final int code;
    private final String reasonPhrase;

    /**
     * Return the String value of this status code.
     */
    public String stringCode() {
        return String.valueOf(this.code);
    }
}
