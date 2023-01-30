package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Role;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.OnUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends RepresentationModel<UserDto> {

    @Null(message = USER_ON_CREATE_VIOLATION, groups = OnCreate.class)
    @NotNull(message = USER_ID_NULL, groups = OnUpdate.class)
    @Positive(message = USER_INVALID_ID, groups = OnUpdate.class)
    private Long id;

    @NotNull(message = USER_LOGIN_NULL)
    @Pattern(regexp = LOGIN_REGEXP, message = USER_INVALID_LOGIN)
    private String login;

    @NotNull(message = USER_EMAIL_NULL)
    @Pattern(regexp = EMAIL_REGEXP, message = USER_INVALID_EMAIL)
    private String email;

    @NotNull(message = USER_PASSWORD_NULL)
    @Pattern(regexp = PASSWORD_REGEXP, message = USER_INVALID_PASSWORD)
    private String password;

    @NotNull(message = USER_ROLE_NULL)
    private Role role;
}
