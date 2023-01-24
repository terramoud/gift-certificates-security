package com.epam.esm.domain.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class EntityValidator {
    private static final Logger LOG = LogManager.getLogger(EntityValidator.class);
    public static final String ENTITY_NAME_REGEXP = "^[\\p{L}][\\p{L} \\-']{0,30}[\\p{L}]$";

    protected boolean validateField(String field) {
        try {
            return field != null && Pattern.matches(ENTITY_NAME_REGEXP, field) && !field.equals("null");
        } catch (NullPointerException e) {
            return false;
        } catch (PatternSyntaxException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean validateId(Long id) {
        return id != null && id >= 0 && id <= Integer.MAX_VALUE;
    }

    public boolean validateId(Integer id) {
        return id != null && id >= 0;
    }
}
