package com.epam.esm.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code ApiErrorResponse} class represents a simple error
 * response message returned by an API.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class ApiErrorResponse {
    private String errorMessage;
    private String errorCode;
}