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

@Component
@Data
public class ErrorMessageFormatter {

    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_PATTERN = "method.argument.type.mismatch.pattern";
    private final Translator translator;

    public String getLocalizedMessage(MethodArgumentNotValidException ex) {
        FieldError fieldError = Optional.ofNullable(ex.getFieldError())
                .orElseThrow(NoSuchFieldError::new);
        return String.format("%s (%s = '%s')",
                translator.toLocale(fieldError.getDefaultMessage()),
                fieldError.getField(),
                fieldError.getRejectedValue());
    }

    public String getLocalizedMessage(ConstraintViolationException ex) {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        constraintViolations.forEach(violation -> result.append(String.format("%s (%s = '%s') | ",
                translator.toLocale(violation.getMessage()),
                violation.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                violation.getInvalidValue())));
        return result.toString();
    }

    public String getLocalizedMessage(MethodArgumentTypeMismatchException ex) {
        String pattern = translator.toLocale(METHOD_ARGUMENT_TYPE_MISMATCH_PATTERN);
        return MessageFormat.format(pattern, ex.getName(), ex.getValue(), ex.getRequiredType());
    }
}
