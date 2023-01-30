package com.epam.esm.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.sql.SQLException;

import static com.epam.esm.exceptions.ErrorCodes.*;
import static com.epam.esm.exceptions.ExceptionConstants.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {


    private final Translator translator;
    private final ErrorMessageFormatter messageFormatter;

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ApiErrorResponse> handleAllExceptionsAndErrors(Throwable ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(SERVER_ERROR_500);
        if (ex.getMessage() != null && ex.getMessage().startsWith("get null list resources")) {
            apiErrorResponse.setErrorCode(NULL_INSTEAD_LIST.stringCode());
            apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()
                    .replace("get null list resources", NULL_INSTEAD_LIST_RESOURCES)));
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(DATA_ACCESS_CONSTRAINT);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataIntegrityViolationException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_INTEGRITY_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(VIOLATION_DATA_INTEGRITY);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<ApiErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(translator.toLocale(MYSQL_ERROR_DUPLICATE_COLUMN));
        apiErrorResponse.setErrorCode(SQL_DUPLICATE_ENTRY.stringCode());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(SQLException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(SQL_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(SQL_ERROR_DEFAULT_MESSAGE);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceUnsupportedOperationException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(ResourceUnsupportedOperationException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex,
                                                             @Nullable Object body,
                                                             @NonNull HttpHeaders headers,
                                                             @NonNull HttpStatus status,
                                                             @NonNull WebRequest request) {
        log.warn(ex.getMessage(), ex);
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(ex.getMessage());
        apiErrorResponse.setErrorCode(status.value() + SUFFIX_RESPONSE_ENTITY_EXCEPTIONS.stringCode());
        if (ex instanceof NoHandlerFoundException && ex.getMessage().startsWith("No handler found for")) {
            status = HttpStatus.NOT_FOUND;
            apiErrorResponse.setErrorCode(NO_HANDLER_FOUND.stringCode());
            apiErrorResponse.setErrorMessage(ex.getMessage()
                    .replace("No handler found for", translator.toLocale(NO_HANDLER_FOUND_FOR)));
        }
        return new ResponseEntity<>(apiErrorResponse, headers, status);
    }

    @ExceptionHandler({InvalidResourcePropertyException.class})
    protected ResponseEntity<ApiErrorResponse> handleInvalidResourceProperty(InvalidResourcePropertyException ex) {
        return handleResourceException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidResourceNameException.class})
    protected ResponseEntity<ApiErrorResponse> handleInvalidResourceName(InvalidResourceNameException ex) {
        return handleResourceException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return handleResourceException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ResourceNotUpdatedException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotUpdated(ResourceNotUpdatedException ex) {
        return handleResourceException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ResourceNotSavedException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotSaved(ResourceNotSavedException ex) {
        return handleResourceException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    protected ResponseEntity<ApiErrorResponse> handleResourceException(ResourceException ex, HttpStatus httpStatus) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        apiErrorResponse.setErrorMessage(
                String.format("%s %s", translator.toLocale(ex.getMessage()), ex.getDetails()));
        return new ResponseEntity<>(apiErrorResponse, httpStatus);
    }
}