package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFilterDto {

    private Long id;

    private String login;

    private String email;

    private Role role;

    @Builder.Default
    private String loginContaining = "";

    @Builder.Default
    private String emailContaining = "";

    @Builder.Default
    private String roleContaining = "";
}
