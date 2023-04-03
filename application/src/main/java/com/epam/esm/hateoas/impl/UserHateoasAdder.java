package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.domain.payload.OrderFilterDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.payload.UserFilterDto;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserHateoasAdder implements HateoasAdder<UserDto> {

    @Value("${page-size.default}")
    private int defaultSize;
    private static final Class<UserController> CONTROLLER = UserController.class;

    @Override
    public void addLinks(UserDto userDto) {
        userDto.add(linkTo(methodOn(CONTROLLER)
                .findById(userDto.getId()))
                .withSelfRel());
        userDto.add(linkTo(methodOn(CONTROLLER)
                .findAll(new UserFilterDto(), Pageable.ofSize(defaultSize)))
                .withRel("users"));
        userDto.add(linkTo(methodOn(CONTROLLER)
                .findAllByUserId(
                        userDto.getId(),
                        new OrderFilterDto(),
                        Pageable.ofSize(defaultSize)))
                .withRel("orders"));
    }
}
