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

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDetailsDto> create(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        UserDetailsDto addedUserDetailsDto = userService.create(userDetailsDto);
        return new ResponseEntity<>(addedUserDetailsDto, HttpStatus.CREATED);
    }

    @PutMapping("{user-id}/password")
    public ResponseEntity<UserDetailsDto> changePassword(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId,
            @RequestBody @Valid PasswordDto changePasswordDto) {
        UserDetailsDto addedUserDetailsDto = userService.changePassword(userId, changePasswordDto);
        return new ResponseEntity<>(addedUserDetailsDto, HttpStatus.CREATED);
    }
}