package com.epam.esm.exceptions;

import lombok.Getter;

@Getter
public class MostPopularTagNotFoundException extends RuntimeException {
    private final String errorCode;

    public MostPopularTagNotFoundException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.stringCode();
    }
}
