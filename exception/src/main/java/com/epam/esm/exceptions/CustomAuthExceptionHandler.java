package com.epam.esm.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.esm.exceptions.ErrorCodes.FORBIDDEN_REQUEST;
import static com.epam.esm.exceptions.ErrorCodes.UNAUTHORIZED_REQUEST;

public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(UNAUTHORIZED_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(authException.getLocalizedMessage());
        String json = objectMapper.writeValueAsString(apiErrorResponse);
        response.getWriter().write(json);
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(FORBIDDEN_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(ex.getLocalizedMessage());
        String json = objectMapper.writeValueAsString(apiErrorResponse);
        response.getWriter().write(json);
    }
}
