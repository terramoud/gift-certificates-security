package com.epam.esm.controller;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.service.api.OrderService;
import com.epam.esm.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final DtoConverter<User, UserDto> converter;
    private final DtoConverter<Order, OrderDto> orderConverter;

    @Autowired
    public UserController(UserService userService,
                          DtoConverter<User, UserDto> converter,
                          OrderService orderService,
                          DtoConverter<Order, OrderDto> orderConverter) {
        this.userService = userService;
        this.converter = converter;
        this.orderService = orderService;
        this.orderConverter = orderConverter;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<User> users = userService.getAllUsers(allRequestParameters, size, page);
        return new ResponseEntity<>(converter.listToDtos(users), HttpStatus.OK);
    }

    @GetMapping("/{user-id}/orders")
    public ResponseEntity<List<OrderDto>> getAllOrdersByUserId(
            @PathVariable("user-id") Long userId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Order> orders = orderService.getAllOrdersByUserId(allRequestParameters, size, page, userId);
        return new ResponseEntity<>(orderConverter.listToDtos(orders), HttpStatus.OK);
    }
}
