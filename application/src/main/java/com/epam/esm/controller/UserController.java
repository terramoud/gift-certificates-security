package com.epam.esm.controller;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.OrderService;
import com.epam.esm.service.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final HateoasAdder<UserDto> hateoasAdder;
    private final HateoasAdder<OrderDto> orderHateoasAdder;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<UserDto> userDtos = userService.findAll(allRequestParameters, size, page);
        userDtos.forEach(hateoasAdder::addLinks);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserDto> findById(@PathVariable("user-id") Long userId) {
        UserDto userDto = userService.findById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/{user-id}/orders")
    public ResponseEntity<List<OrderDto>> findAllByUserId(
            @PathVariable("user-id") Long userId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<OrderDto> orderDtos = orderService.findAllByUserId(allRequestParameters, size, page, userId);
        orderHateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        UserDto addedUserDto = userService.create(userDto);
        hateoasAdder.addLinks(addedUserDto);
        return new ResponseEntity<>(addedUserDto, HttpStatus.CREATED);
    }

    @PutMapping("/{tag-id}")
    public ResponseEntity<UserDto> update(@PathVariable("tag-id") Long tagId,
                                          @RequestBody UserDto userDto) {
        UserDto updatedUserDto = userService.update(tagId, userDto);
        hateoasAdder.addLinks(updatedUserDto);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/{tag-id}")
    public ResponseEntity<UserDto> deleteById(@PathVariable("tag-id") Long tagId) {
        UserDto userDto = userService.deleteById(tagId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
