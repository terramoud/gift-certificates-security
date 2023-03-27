package com.epam.esm.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.epam.esm.config.ConstantsValidationMessages.*;

@Data
public class PasswordDto {

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String currentPassword;

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String newPassword;
}
