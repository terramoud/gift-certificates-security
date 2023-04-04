package com.epam.esm.dto;

import com.epam.esm.model.entity.Role;
import com.epam.esm.model.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.epam.esm.config.ConstantsValidationMessages.*;

/**
 * The UserDetailsDto class represents the user
 * details data transfer object.
 * It includes user's login, email, password, and role.
 * It also provides conversion methods to convert
 * the DTO to a User entity and vice versa.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
public class UserDetailsDto {

    @NotNull(message = USER_LOGIN_NULL)
    @Pattern(regexp = LOGIN_REGEXP, message = USER_INVALID_LOGIN)
    private String login;

    @NotNull(message = USER_EMAIL_NULL)
    @Pattern(regexp = EMAIL_REGEXP, message = USER_INVALID_EMAIL)
    private String email;

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = USER_ROLE_NULL)
    private Role role;

    /**
     * This method converts the DTO to a User entity
     *
     * @return The User entity.
     */
    public User toUser() {
        User user = new User();
        user.setLogin(this.login);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(role);
        return user;
    }

    /**
     * This method converts a User entity to a UserDetailsDto
     *
     * @param user The User entity.
     * @return The UserDetailsDto object.
     */
    public static UserDetailsDto toDto(User user) {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        return dto;
    }
}
