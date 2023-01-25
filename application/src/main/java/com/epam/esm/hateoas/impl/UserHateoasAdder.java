package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;


import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserHateoasAdder implements HateoasAdder<UserDto> {
    private static final Class<UserController> CONTROLLER = UserController.class;
    private static final LinkedMultiValueMap<String, String> REQUEST_PARAMS = new LinkedMultiValueMap<>(
            Map.of("sort", List.of("+id"),
                    "search", List.of(""))
    );

    @Override
    public void addLinks(UserDto userDto) {
        userDto.add(linkTo(methodOn(CONTROLLER)
                .getUserById(userDto.getId()))
                .withSelfRel());
        userDto.add(linkTo(methodOn(CONTROLLER)
                .getAllUsers(REQUEST_PARAMS, DEFAULT_PAGE, DEFAULT_SIZE))
                .withRel("users"));
        userDto.add(linkTo(methodOn(CONTROLLER)
                .getAllOrdersByUserId(userDto.getId(), REQUEST_PARAMS, DEFAULT_PAGE, DEFAULT_SIZE))
                .withRel("orders"));
    }
}
