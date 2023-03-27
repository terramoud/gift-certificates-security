package com.epam.esm.controller;

import com.epam.esm.security.annotations.AdminWritePermission;
import com.epam.esm.security.annotations.UserReadPermission;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.validation.OnUpdate;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.OrderService;
import com.epam.esm.service.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Validated
public class UserController {

    private static final String PAGE_DEFAULT = "0";
    private static final String SIZE_DEFAULT = "5";
    private final UserService userService;
    private final OrderService orderService;
    private final HateoasAdder<UserDto> hateoasAdder;
    private final HateoasAdder<OrderDto> orderHateoasAdder;

    @GetMapping
    @UserReadPermission
    public ResponseEntity<List<UserDto>> findAll(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = PAGE_DEFAULT)
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = SIZE_DEFAULT)
            @Positive(message = INVALID_SIZE) int size) {
        List<UserDto> userDtos = userService.findAll(allRequestParameters, new PageDto(page, size));
        userDtos.forEach(hateoasAdder::addLinks);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    @UserReadPermission
    public ResponseEntity<UserDto> findById(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId) {
        UserDto userDto = userService.findById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/{user-id}/orders")
    @UserReadPermission
    public ResponseEntity<List<OrderDto>> findAllByUserId(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = PAGE_DEFAULT)
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = SIZE_DEFAULT)
            @Positive(message = INVALID_SIZE) int size) {
        List<OrderDto> orderDtos = orderService
                .findAllByUserId(allRequestParameters, new PageDto(page, size), userId);
        orderHateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
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
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
