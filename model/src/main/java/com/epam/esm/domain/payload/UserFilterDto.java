package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO class that represents a filter for querying users.
 * It contains fields that can be used to filter users by ID,
 * login, email, and role.
 * The class also includes fields with the
 * "Containing" suffix that allow for searching users with
 * substrings of the corresponding fields.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFilterDto {

    private Long id;

    private String login;

    private String email;

    private Role role;

    /**
     * A substring to match the login field of users against.
     * Only users whose logins contain this substring will
     * be returned. Defaults to an empty string, meaning no filtering
     * by login will be performed.
     */
    @Builder.Default
    private String loginContaining = "";

    /**
     * A substring to match the email field of users against.
     * Only users whose emails contain this substring will
     * be returned. Defaults to an empty string, meaning no filtering
     * by email will be performed.
     */
    @Builder.Default
    private String emailContaining = "";

    /**
     * A substring to match the role field of users against.
     * Only users whose roles contain this substring will
     * be returned. Defaults to an empty string, meaning no filtering
     * by role will be performed.
     */
    @Builder.Default
    private String roleContaining = "";
}
