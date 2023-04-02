package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Role;
import lombok.Data;

@Data
public class UserFilterDto {

    private Long id;

    private String login;

    private String email;

    private Role role;

    private String loginContaining = "";

    private String emailContaining = "";

    private String roleContaining = "";
}
