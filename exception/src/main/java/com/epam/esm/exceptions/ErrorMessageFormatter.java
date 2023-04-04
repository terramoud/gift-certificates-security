package com.epam.esm.exceptions;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;


/**
 * ErrorMessageFormatter is a Spring component class for formatting
 * error messages in a specific way.
 * This class provides methods to format error messages for
 * different types of exceptions.
 * It depends on the Translator interface to translate messages
 * to different locales.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Component
@Data
public class ErrorMessageFormatter {

    /**
     * The key pattern for the message template of a MethodArgumentTypeMismatchException.
     */
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_PATTERN = "method.argument.type.mismatch.pattern";
    private final Translator translator;

    /**
     * Returns the localized error message for a
     * MethodArgumentNotValidException
     *
     * @param ex a MethodArgumentNotValidException instance
     *           that occurred
     * @return a formatted error message with the invalid field,
     *          its value and the error message
     */
    public String getLocalizedMessage(MethodArgumentNotValidException ex) {
        FieldError fieldError = Optional.ofNullable(ex.getFieldError())
                .orElseThrow(NoSuchFieldError::new);
        return String.format("%s (%s = '%s')",
                translator.toLocale(fieldError.getDefaultMessage()),
                fieldError.getField(),
                fieldError.getRejectedValue());
    }

    /**
     * Returns the localized error message for a ConstraintViolationException
     *
     * @param ex a ConstraintViolationException instance that occurred
     * @return a formatted error message with the invalid field,
     *          its value and the error message
     */
    public String getLocalizedMessage(ConstraintViolationException ex) {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        constraintViolations.forEach(violation -> result.append(String.format("%s (%s = '%s') | ",
                translator.toLocale(violation.getMessage()),
                violation.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                violation.getInvalidValue())));
        return result.toString();
    }

    /**
     * Returns the localized error message for a MethodArgumentTypeMismatchException
     *
     * @param ex a MethodArgumentTypeMismatchException
     *           instance that occurred
     * @return a formatted error message with the argument name,
     *          its value, and the required type
     */
    public String getLocalizedMessage(MethodArgumentTypeMismatchException ex) {
        String pattern = translator.toLocale(METHOD_ARGUMENT_TYPE_MISMATCH_PATTERN);
        return MessageFormat.format(pattern, ex.getName(), ex.getValue(), ex.getRequiredType());
    }
}
