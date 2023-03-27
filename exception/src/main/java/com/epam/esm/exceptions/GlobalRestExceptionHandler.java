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

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    private final Translator translator;
    private final ErrorMessageFormatter messageFormatter;

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

    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(DATA_ACCESS_CONSTRAINT));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<ApiErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(NOT_FOUND_USER_RESOURCE.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getLocalizedMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(UNAUTHORIZED_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getLocalizedMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_INTEGRITY_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(VIOLATION_DATA_INTEGRITY));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<ApiErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(translator.toLocale(MYSQL_ERROR_DUPLICATE_COLUMN));
        apiErrorResponse.setErrorCode(SQL_DUPLICATE_ENTRY.stringCode());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(SQLException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(SQL_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(SQL_ERROR_DEFAULT_MESSAGE));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceUnsupportedOperationException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(ResourceUnsupportedOperationException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_TYPE_MISTMATCH.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MostPopularTagNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMostPopularTagNotFound(MostPopularTagNotFoundException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidJsonPatchException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJsonPatch(InvalidJsonPatchException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn(messageFormatter.getLocalizedMessage(ex), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(PATH_VARIABLE_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(FORBIDDEN_REQUEST.stringCode());
        apiErrorResponse.setErrorMessage(ex.getMessage() + " " + request.getDescription(false));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.FORBIDDEN);
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
