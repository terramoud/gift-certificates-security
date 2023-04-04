package com.epam.esm.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.epam.esm.config.ConstantsValidationMessages.*;

/**
 * A data transfer object (DTO) for a user's password
 * Contains fields for the user's current and new passwords
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
public class PasswordDto {

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String currentPassword;

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String newPassword;
}
