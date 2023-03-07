package com.epam.esm.dto;

import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserDetailsDto {

    public static final String USER_INVALID_LOGIN = "user.invalid.login";
    public static final String USER_INVALID_EMAIL = "user.invalid.email";
    public static final String USER_INVALID_PASSWORD = "user.invalid.password";
    public static final String USER_LOGIN_NULL = "user.login.null";
    public static final String USER_EMAIL_NULL = "user.email.null";
    public static final String USER_PASSWORD_NULL = "user.password.null";
    public static final String USER_ROLE_NULL = "user.role.null";
    public static final String EMAIL_REGEXP = "^\\w+([.-]?\\w+){0,249}@\\w+([.-]?\\w+){0,249}(\\.\\w{2,3}){1,249}$";
    public static final String LOGIN_REGEXP = "^[\\p{L}][\\p{L}0-9]{2,31}$";
    public static final String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,31}$";

    @NotNull(message = USER_LOGIN_NULL)
    @Pattern(regexp = LOGIN_REGEXP, message = USER_INVALID_LOGIN)
    private String login;

    @NotNull(message = USER_EMAIL_NULL)
    @Pattern(regexp = EMAIL_REGEXP, message = USER_INVALID_EMAIL)
    private String email;

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = USER_ROLE_NULL)
    private Role role;

    public User toUser() {
        User user = new User();
        user.setLogin(this.login);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(role);
        return user;
    }
}
