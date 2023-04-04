package com.epam.esm.exceptions;

import lombok.Getter;

/**
 * Exception thrown when the most popular tag cannot be found.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Getter
public class MostPopularTagNotFoundException extends RuntimeException {
    private final String errorCode;

    public MostPopularTagNotFoundException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.stringCode();
    }
}
