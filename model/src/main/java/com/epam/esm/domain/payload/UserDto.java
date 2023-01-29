package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends RepresentationModel<UserDto> {
    private Long id;
    private String login;

    @Email
    private String email;
    private String password;
    private int roleId;
}
