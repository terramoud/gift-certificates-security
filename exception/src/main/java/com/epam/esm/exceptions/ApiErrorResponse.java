package com.epam.esm.exceptions;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse {

    private String errorMessage;
    private String errorCode;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ApiErrorResponse{" +
                "errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}