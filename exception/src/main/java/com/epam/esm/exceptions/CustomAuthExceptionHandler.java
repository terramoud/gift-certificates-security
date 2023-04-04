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

/**
 * The CustomAuthExceptionHandler class is responsible for
 * handling authentication and access denied exceptions
 * by returning an appropriate error response in JSON
 * format with the error message and code.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles the unauthorized request and returns a JSON formatted
     * error response with the error message and code
     *
     * @param request the request that resulted in an authentication exception
     * @param response the response to be populated with the error message and code
     * @param authException the exception that was thrown during authentication
     * @throws IOException if an error occurs while processing the request/response
     */
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

    /**
     * Handles the forbidden request and returns a JSON formatted
     * error response with the error message and code
     *
     * @param request the request that resulted in an access denied exception
     * @param response the response to be populated with the error message and code
     * @param ex the exception that was thrown during authorization
     * @throws IOException if an error occurs while processing the request/response
     */
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
