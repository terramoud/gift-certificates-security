package com.epam.esm.controller;

import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDetails> create(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        UserDetails addedUserDetailsDto = userService.create(userDetailsDto);
        return new ResponseEntity<>(addedUserDetailsDto, HttpStatus.CREATED);
    }
}