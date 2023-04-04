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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

/**
 * This class provides REST API endpoints to manage users and their orders.
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
    private final OrderService orderService;
    private final HateoasAdder<UserDto> hateoasAdder;
    private final HateoasAdder<OrderDto> orderHateoasAdder;

    /**
     * Retrieve a list of user DTOs based on the provided filter and pageable parameters.
     *
     * @param userFilterDto filter criteria for retrieving users
     * @param pageable page and sorting criteria
     * @return list of user DTOs with added HATEOAS links
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws AccessDeniedException if the requesting user does not have appropriate permissions
     */
    @GetMapping
    @AdminReadPermission
    public ResponseEntity<List<UserDto>> findAll(UserFilterDto userFilterDto, Pageable pageable) {
        List<UserDto> userDtos = userService.findAll(userFilterDto, pageable);
        userDtos.forEach(hateoasAdder::addLinks);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    /**
     * Retrieve a list of order DTOs associated with a specified user ID.
     *
     * @param userId ID of the user whose orders to retrieve
     * @param orderFilterDto filter criteria for retrieving orders
     * @param pageable page and sorting criteria
     * @return list of order DTOs with added HATEOAS links
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the provided user ID
     *          is not a positive integer
     */
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

    /**
     * Retrieve a user by ID.
     * @param userId the ID of the user to retrieve
     * @return the user DTO
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the user ID is not positive
     */
    @GetMapping("/{user-id}")
    @UserReadPermission
    public ResponseEntity<UserDto> findById(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId) {
        UserDto userDto = userService.findById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Update a user by ID.
     *
     * @param userId the ID of the user to update
     * @param userDto the updated user data
     * @return the updated user DTO
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the user ID is not positive
     */
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

    /**
     * Endpoint for deleting a user by ID.
     * @param userId the ID of the user to delete
     * @return the deleted user DTO with added HATEOAS links
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the user ID is not positive
     */
    @DeleteMapping("/{user-id}")
    @AdminWritePermission
    public ResponseEntity<UserDto> deleteById(
            @PathVariable("user-id") @Positive(message = USER_INVALID_ID) Long userId) {
        UserDto userDto = userService.deleteById(userId);
        hateoasAdder.addLinks(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.NO_CONTENT);
    }
}
