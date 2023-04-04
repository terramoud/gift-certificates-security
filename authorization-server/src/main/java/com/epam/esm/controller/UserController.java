package com.epam.esm.controller;

import com.epam.esm.dto.PasswordDto;
import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static com.epam.esm.config.ConstantsValidationMessages.USER_INVALID_ID;

/**
 * This class represents a RESFull controller for managing users.
 * Provides endpoints for creating a new user and changing the user password.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * Create a new user
     *
     * @param userDetailsDto an object containing the user details to create
     * @return ResponseEntity containing the newly created UserDetailsDto object with a HTTP status of 201 (Created)
     */
    @PostMapping
    public ResponseEntity<UserDetailsDto> create(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        UserDetailsDto addedUserDetailsDto = userService.create(userDetailsDto);
        return new ResponseEntity<>(addedUserDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Change the password for a user with the specified ID
     *
     * @param userId the ID of the user to change the password for
     * @param changePasswordDto an object containing the new password details
     * @return ResponseEntity containing the updated UserDetailsDto object with a HTTP status of 201 (Created)
     */
    @PutMapping("{user-id}/password")
    public ResponseEntity<UserDetailsDto> changePassword(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId,
            @RequestBody @Valid PasswordDto changePasswordDto) {
        UserDetailsDto addedUserDetailsDto = userService.changePassword(userId, changePasswordDto);
        return new ResponseEntity<>(addedUserDetailsDto, HttpStatus.CREATED);
    }
}