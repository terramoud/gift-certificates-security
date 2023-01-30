package com.epam.esm.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {
    public static final String SERVER_ERROR_500 = "internal.server.error";
    public static final String NULL_INSTEAD_LIST_RESOURCES = "get.null.list.resources";
    public static final String DATA_ACCESS_CONSTRAINT = "data.access.exception";
    public static final String VIOLATION_DATA_INTEGRITY = "violation.data.integrity";
    public static final String MYSQL_ERROR_DUPLICATE_COLUMN = "mysql.error.duplicate.column";
    public static final String SQL_ERROR_DEFAULT_MESSAGE = "sql.error.default.message";
    public static final String NO_HANDLER_FOUND_FOR = "no.handler.found.for";
}
