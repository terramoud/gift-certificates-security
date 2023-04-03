package com.epam.esm.controller;

import com.epam.esm.domain.payload.*;
import com.epam.esm.security.annotations.AdminReadPermission;
import com.epam.esm.security.annotations.AdminWritePermission;
import com.epam.esm.security.annotations.UserReadPermission;
import com.epam.esm.domain.validation.OnUpdate;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.OrderService;
import com.epam.esm.service.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final HateoasAdder<UserDto> hateoasAdder;
    private final HateoasAdder<OrderDto> orderHateoasAdder;

    @GetMapping
    @AdminReadPermission
    public ResponseEntity<List<UserDto>> findAll(UserFilterDto userFilterDto, Pageable pageable) {
        List<UserDto> userDtos = userService.findAll(userFilterDto, pageable);
        userDtos.forEach(hateoasAdder::addLinks);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{user-id}/orders")
    @UserReadPermission
    public ResponseEntity<List<OrderDto>> findAllByUserId(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId,
            OrderFilterDto orderFilterDto,
            Pageable pageable) {
        List<OrderDto> orderDtos = orderService.findAllByUserId(userId, orderFilterDto, pageable);
        orderHateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    @UserReadPermission
    public ResponseEntity<UserDto> findById(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId) {
        UserDto userDto = userService.findById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{user-id}")
    @Validated({OnUpdate.class})
    @AdminWritePermission
    public ResponseEntity<UserDto> update(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId,
            @RequestBody @Valid UserDto userDto) {
        UserDto updatedUserDto = userService.update(userId, userDto);
        hateoasAdder.addLinks(updatedUserDto);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/{user-id}")
    @AdminWritePermission
    public ResponseEntity<UserDto> deleteById(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId) {
        UserDto userDto = userService.deleteById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.NO_CONTENT);
    }
}
