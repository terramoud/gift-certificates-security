package com.epam.esm.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.sql.SQLException;

import static com.epam.esm.exceptions.ErrorCodes.METHOD_ARGUMENT_CONSTRAINT_VIOLATION;
import static com.epam.esm.exceptions.ErrorCodes.SUFFIX_RESPONSE_ENTITY_EXCEPTIONS;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {


    private final Translator translator;
    private final ErrorMessageFormatter messageFormatter;

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ApiErrorResponse> handleAllExceptionsAndErrors(Throwable ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ErrorCodes.INTERNAL_SERVER_ERROR.stringCode());
        apiErrorResponse.setErrorMessage("internal.server.error");
        if (ex.getMessage() != null && ex.getMessage().startsWith("get null list resources")) {
            apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage().replace(
                    "get null list resources", "get.null.list.resources")));
            apiErrorResponse.setErrorCode(ErrorCodes.NULL_INSTEAD_LIST.stringCode());
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ErrorCodes.DATA_ACCESS_EXCEPTION.stringCode());
        apiErrorResponse.setErrorMessage("data.access.exception");
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ApiErrorResponse> handleDataAccessException(DataIntegrityViolationException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ErrorCodes.DATA_INTEGRITY_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage("data.integrity.violation");
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<ApiErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(translator.toLocale("mysql.error.duplicate.column"));
        apiErrorResponse.setErrorCode(ErrorCodes.SQL_DUPLICATE_ENTRY.stringCode());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ApiErrorResponse> customerNotFound(SQLException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ErrorCodes.SQL_ERROR.stringCode());
        apiErrorResponse.setErrorMessage("sql.error.default.message");
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(METHOD_ARGUMENT_CONSTRAINT_VIOLATION.stringCode());
        apiErrorResponse.setErrorMessage(messageFormatter.getLocalizedMessage(ex));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ex.printStackTrace();
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status))
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(ex.getMessage());
        apiErrorResponse.setErrorCode(status.value() + SUFFIX_RESPONSE_ENTITY_EXCEPTIONS.stringCode());
        if (ex instanceof NoHandlerFoundException && ex.getMessage().startsWith("No handler found for")) {
            status = HttpStatus.NOT_FOUND;
            apiErrorResponse.setErrorCode(ErrorCodes.NO_HANDLER_FOUND.stringCode());
            apiErrorResponse.setErrorMessage(ex.getMessage().replace(
                    "No handler found for", translator.toLocale("no.handler.found.for")));
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
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        apiErrorResponse.setErrorMessage(translator.toLocale(ex.getMessage()) + " " + ex.getDetails());
        return new ResponseEntity<>(apiErrorResponse, httpStatus);
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public final ResponseEntity<ApiErrorResponse> handleInvalidInputParameter(InvalidPaginationParameterException ex) {
        ex.printStackTrace();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setErrorMessage(
                translator.toLocale("error.invalid.pagination.parameter")
                        .concat(" " + ex.getParameterName())
                        .concat(translator.toLocale("cannot.be"))
                        .concat(" " + ex.getParameterValue()));
        apiErrorResponse.setErrorCode(ex.getErrorCode());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }
}