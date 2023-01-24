package com.epam.esm.domain.validation;

import com.epam.esm.domain.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class UserValidator extends EntityValidator {
    private static final Logger LOG = LogManager.getLogger(UserValidator.class);
    private static final String EMAIL_REGEXP = "^\\w+([.-]?\\w+){0,249}@\\w+([.-]?\\w+){0,249}(\\.\\w{2,3}){1,249}$";
    private static final String LOGIN_REGEXP = "^[\\p{L}][\\p{L}0-9]{2,31}$";
    private static final String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,31}$";

    public boolean validateRoleId(int roleId) {
        return roleId > 0;
    }

    public boolean validateLogin(String login) {
        return validateField(login, LOGIN_REGEXP) && login.length() <= 32;
    }

    public boolean validateEmail(String email) {
        return validateField(email, EMAIL_REGEXP) && email.length() <= 255;
    }

    public boolean validatePassword(String password) {
        return validateField(password, PASSWORD_REGEXP) && password.length() <= 32;
    }

    public boolean validate(User user) {
        return validateId(user.getId())
                && validateLogin(user.getLogin());
    }

    private boolean validateField(String field, String regexp) {
        try {
            return field != null && Pattern.matches(regexp, field);
        } catch (NullPointerException e) {
            return false;
        } catch (PatternSyntaxException e) {
            LOG.error(e);
            return false;
        }
    }
}
