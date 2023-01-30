package com.epam.esm.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

import static com.epam.esm.exceptions.ErrorCodes.*;
import static com.epam.esm.exceptions.ExceptionConstants.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestExceptionHandler {


    private final Translator translator;
    private final ErrorMessageFormatter messageFormatter;

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ApiErrorResponse> handleAllExceptionsAndErrors(Throwable ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(SERVER_ERROR_500));
        if (ex.getMessage() != null && ex.getMessage().startsWith("get null list resources")) {
            apiErrorResponse.setErrorCode(NULL_INSTEAD_LIST.stringCode());
            apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()
                    .replace("get null list resources", NULL_INSTEAD_LIST_RESOURCES)));
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataIntegrityViolationException ex) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn(messageFormatter.getLocalizedMessage(ex), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn(messageFormatter.getLocalizedMessage(ex), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(PATH_VARIABLE_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn(ex.getMessage(), ex);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(NO_HANDLER_FOUND.stringCode());
        apiErrorResponse.setErrorMessage(ex.getMessage());
        if (ex.getMessage().startsWith("No handler found for")) {
            apiErrorResponse.setErrorCode(NO_HANDLER_FOUND.stringCode());
            apiErrorResponse.setErrorMessage(ex.getMessage()
                    .replace("No handler found for", translator.toLocale(NO_HANDLER_FOUND_FOR)));
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
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