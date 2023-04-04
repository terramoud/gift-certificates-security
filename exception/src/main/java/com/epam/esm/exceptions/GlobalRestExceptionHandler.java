package com.epam.esm.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

import static com.epam.esm.exceptions.ErrorCodes.*;
import static com.epam.esm.exceptions.ExceptionConstants.*;

/**
 * Global exception handler for REST endpoints.
 * <p>
 * This class is responsible for handling exceptions thrown by
 * REST endpoints and formatting a standardized response
 * object for error messages. It extends the
 * {@link ResponseEntityExceptionHandler} class, which provides
 * default implementations for handling exceptions and defines
 * methods for overriding the defaults. This class overrides
 * the default implementations to provide customized handling
 * for various exceptions that may occur during REST endpoint
 * processing.
 * </p>
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    private final Translator translator;
    private final ErrorMessageFormatter messageFormatter;

    /**
     * Custom handling for HTTP method not supported exception
     *
     * @param ex the exception to be handled
     * @param headers the headers to be returned in the response
     * @param status the HTTP status to be returned in the response
     * @param request the request that triggered the exception
     * @return a response entity containing an error message object
     * @throws NullPointerException if the supported HTTP methods are null
     */
    @Nonnull
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         @Nonnull HttpHeaders headers,
                                                                         @Nonnull HttpStatus status,
                                                                         @Nonnull WebRequest request) {
        pageNotFoundLogger.warn(ex.getMessage());
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            Objects.requireNonNull(headers).setAllow(supportedMethods);
        }
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(UNSUPPORTED_HTTP_METHOD.stringCode());
        apiErrorResponse.setErrorMessage(
                String.format(PATTERN_UNSUPPORTED_HTTP_METHOD, supportedMethods));
        return handleExceptionInternal(ex, apiErrorResponse, headers, status, request);
    }

    /**
     * Custom handling for no handler found exception
     *
     * @param ex the exception to be handled
     * @param headers the headers to be returned in the response
     * @param status the HTTP status to be returned in the response
     * @param request the request that triggered the exception
     * @return a response entity containing an error message object
     */
    @Nonnull
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(@Nonnull NoHandlerFoundException ex,
                                                                   @Nonnull HttpHeaders headers,
                                                                   @Nonnull HttpStatus status,
                                                                   @Nonnull WebRequest request) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(NO_HANDLER_FOUND.stringCode());
        apiErrorResponse.setErrorMessage(ex.getMessage());
        if (ex.getMessage().startsWith(NO_HANDLER_FOUND_FOR)) {
            apiErrorResponse.setErrorCode(NO_HANDLER_FOUND.stringCode());
            apiErrorResponse.setErrorMessage(ex.getMessage()
                    .replace(NO_HANDLER_FOUND_FOR, translator.toLocale(ExceptionConstants.NO_HANDLER_FOUND_FOR)));
        }
        return handleExceptionInternal(ex, apiErrorResponse, headers, status, request);
    }

    /**
     * Custom handling for method argument not valid exception.
     * @param ex the exception to be handled
     * @param headers the headers to be returned in the response
     * @param status the HTTP status to be returned in the response
     * @param request the request that triggered the exception
     * @return a response entity containing an error message object
     */
    @Nonnull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
                                                                  @Nonnull HttpHeaders headers,
                                                                  @Nonnull HttpStatus status,
                                                                  @Nonnull WebRequest request) {
        log.warn(messageFormatter.getLocalizedMessage(ex), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return handleExceptionInternal(ex, apiErrorResponse, headers, status, request);
    }

    /**
     * Handles all exceptions and errors that occur during
     * the processing of requests.
     * Returns a response with a corresponding error message
     * and a status of 500 - INTERNAL_SERVER_ERROR.
     *
     * @param ex the exception or error that occurred
     * @return a ResponseEntity containing a message and status
     */
    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ApiErrorResponse> handleAllExceptionsAndErrors(Throwable ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(SERVER_ERROR_500));
        if (ex.getMessage() != null && ex.getMessage().startsWith(GET_NULL_LIST_RESOURCES)) {
            apiErrorResponse.setErrorCode(NULL_INSTEAD_LIST.stringCode());
            apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()
                    .replace(GET_NULL_LIST_RESOURCES, NULL_INSTEAD_LIST_RESOURCES)));
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all IllegalArgumentExceptions that occur
     * during the processing of requests.
     * Returns a response with a corresponding error message
     * and a status of 400 - BAD_REQUEST
     *
     * @param ex the exception that occurred
     * @return a ResponseEntity containing a message and status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ILLEGAL_ARGUMENT.stringCode());
        apiErrorResponse.setErrorMessage(ex.getLocalizedMessage());
        if (ex.getMessage() != null && ex.getMessage().startsWith(DEFAULT_ERROR_MESSAGE_INVALID_PAGE)) {
            apiErrorResponse.setErrorMessage(translator.toLocale(INVALID_PAGE_INDEX));
        }
        if (ex.getMessage() != null && ex.getMessage().startsWith(DEFAULT_ERROR_MESSAGE_INVALID_SIZE)) {
            apiErrorResponse.setErrorMessage(translator.toLocale(INVALID_PAGE_SIZE));
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all DataAccessExceptions that occur during
     * the processing of requests.
     * Returns a response with a corresponding error message
     * and a status of 400 - BAD_REQUEST
     *
     * @param ex the exception that occurred
     * @return a ResponseEntity containing a message and status
     */
    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(DATA_ACCESS_CONSTRAINT));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all UsernameNotFoundExceptions that
     * occur during the processing of requests.
     * Returns a response with a corresponding error
     * message and a status of 404 - NOT_FOUND
     *
     * @param ex the exception that occurred
     * @return a ResponseEntity containing a message and status
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<ApiErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(NOT_FOUND_USER_RESOURCE.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getLocalizedMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * This method is a handler for BadCredentialsException.
     * It returns an UNAUTHORIZED HTTP status code and
     * ApiErrorResponse object with
     * an error message "Invalid credentials" in case
     * the exception is thrown
     *
     * @param ex the BadCredentialsException object that was thrown
     * @return ResponseEntity with an error message and HTTP status code
     */
    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(UNAUTHORIZED_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getLocalizedMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * This method is a handler for DataIntegrityViolationException.
     * It returns a BAD_REQUEST HTTP status code and
     * ApiErrorResponse object with an error message
     * "Violation of data integrity" in case the
     * exception is thrown
     *
     * @param ex the DataIntegrityViolationException object that was thrown
     * @return ResponseEntity with an error message and HTTP status code
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_INTEGRITY_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(VIOLATION_DATA_INTEGRITY));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is a handler for DuplicateKeyException.
     * It returns a BAD_REQUEST HTTP status code and
     * ApiErrorResponse object with an error message
     * "Duplicate column" in case the exception is thrown
     *
     * @param ex the DuplicateKeyException object that was thrown
     * @return ResponseEntity with an error message and HTTP status code
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<ApiErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(translator.toLocale(MYSQL_ERROR_DUPLICATE_COLUMN));
        apiErrorResponse.setErrorCode(SQL_DUPLICATE_ENTRY.stringCode());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is a handler for SQLException.
     * It returns a BAD_REQUEST HTTP status code and
     * ApiErrorResponse object with an error message
     * "An error occurred while processing your request"
     * in case the exception is thrown
     *
     * @param ex the SQLException object that was thrown
     * @return ResponseEntity with an error message and HTTP status code
     */
    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(SQLException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(SQL_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(SQL_ERROR_DEFAULT_MESSAGE));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions that occur when a requested
     * resource is not supported by the REST API.
     * @param ex The exception that was thrown.
     * @return A ResponseEntity containing an ApiErrorResponse
     *          and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(ResourceUnsupportedOperationException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(ResourceUnsupportedOperationException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions that occur when a requested method
     * argument type mismatches with the expected type.
     * @param ex The exception that was thrown
     * @return A ResponseEntity containing an ApiErrorResponse
     *          and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_TYPE_MISTMATCH.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions that occur when the most popular
     * tag cannot be found.
     * @param ex The exception that was thrown.
     * @return A ResponseEntity containing an ApiErrorResponse
     *          and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(MostPopularTagNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMostPopularTagNotFound(MostPopularTagNotFoundException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions that occur when an invalid
     * JSON patch is received.
     *
     * @param ex The exception that was thrown.
     * @return A ResponseEntity containing an ApiErrorResponse
     *          and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(InvalidJsonPatchException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJsonPatch(InvalidJsonPatchException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions that occur when a constraint
     * violation occurs.
     *
     * @param ex The exception that was thrown.
     * @return A ResponseEntity containing an ApiErrorResponse
     *          and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn(messageFormatter.getLocalizedMessage(ex), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(PATH_VARIABLE_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles exceptions that occur when access is denied to a resource.
     *
     * @param ex The exception that was thrown.
     * @param request The WebRequest object containing the request information.
     * @return A ResponseEntity containing an ApiErrorResponse and
     *          HTTP status FORBIDDEN.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(FORBIDDEN_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(ex.getMessage() + " " + request.getDescription(false));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle {@link InvalidResourcePropertyException} exception.
     *
     * @param ex the exception object
     * @return ResponseEntity with HttpStatus.BAD_REQUEST
     * @throws InvalidResourcePropertyException if the resource property is invalid
     */
    @ExceptionHandler({InvalidResourcePropertyException.class})
    protected ResponseEntity<ApiErrorResponse> handleInvalidResourceProperty(InvalidResourcePropertyException ex) {
        return handleResourceException(ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link ResourceNotUpdatedException} exception.
     *
     * @param ex the exception object
     * @return ResponseEntity with HttpStatus.UNPROCESSABLE_ENTITY
     * @throws ResourceNotUpdatedException if the resource cannot be updated
     */
    @ExceptionHandler({InvalidResourceNameException.class})
    protected ResponseEntity<ApiErrorResponse> handleInvalidResourceName(InvalidResourceNameException ex) {
        return handleResourceException(ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link ResourceNotFoundException} exception.
     *
     * @param ex the exception object
     * @return ResponseEntity with HttpStatus.NOT_FOUND
     * @throws ResourceNotFoundException if the resource is not found
     */
    @ExceptionHandler({ResourceNotFoundException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return handleResourceException(ex, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle {@link ResourceNotUpdatedException} exception.
     *
     * @param ex the exception object
     * @return ResponseEntity with HttpStatus.UNPROCESSABLE_ENTITY
     * @throws ResourceNotUpdatedException if the resource cannot be updated
     */
    @ExceptionHandler({ResourceNotUpdatedException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotUpdated(ResourceNotUpdatedException ex) {
        return handleResourceException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle {@link ResourceNotSavedException} exception.
     *
     * @param ex the exception object
     * @return ResponseEntity with HttpStatus.UNPROCESSABLE_ENTITY
     * @throws ResourceNotSavedException if the resource cannot be saved
     */
    @ExceptionHandler({ResourceNotSavedException.class})
    protected ResponseEntity<ApiErrorResponse> handleResourceNotSaved(ResourceNotSavedException ex) {
        return handleResourceException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle resource exceptions.
     *
     * @param ex the exception object
     * @param httpStatus the HTTP status code to be returned
     * @return ResponseEntity with the given HTTP status code
     */
    protected ResponseEntity<ApiErrorResponse> handleResourceException(ResourceException ex, HttpStatus httpStatus) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        apiErrorResponse.setErrorMessage(
                String.format("%s %s", translator.toLocale(ex.getMessage()), ex.getDetails()));
        return new ResponseEntity<>(apiErrorResponse, httpStatus);
    }
}
