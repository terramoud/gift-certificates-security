package com.epam.esm.exceptions;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

@Component
@Data
public class ErrorMessageFormatter {

    private final Translator translator;

    public String getLocalizedMessage(MethodArgumentNotValidException ex) {
        FieldError fieldError = Optional.ofNullable(ex.getFieldError())
                .orElseThrow(NoSuchFieldError::new);
        return String.format("%s ('%s' = '%s')",
                translator.toLocale(fieldError.getDefaultMessage()),
                fieldError.getField(),
                fieldError.getRejectedValue());
    }
}
