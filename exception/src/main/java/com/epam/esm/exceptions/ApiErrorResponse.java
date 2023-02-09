package com.epam.esm.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiErrorResponse {

    private String errorMessage;
    private String errorCode;
}