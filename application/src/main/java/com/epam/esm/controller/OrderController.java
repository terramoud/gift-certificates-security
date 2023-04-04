package com.epam.esm.controller;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.OrderFilterDto;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.security.annotations.AdminWritePermission;
import com.epam.esm.security.annotations.UserReadPermission;
import com.epam.esm.security.annotations.UserWritePermission;
import com.epam.esm.service.api.OrderService;
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
 * The {@code OrderController} class is responsible for handling
 * HTTP requests related to orders.
 * It contains methods for retrieving all orders, retrieving an order by its ID,
 * adding an order, and deleting an order by its ID.
 * <p>
 * <strong>Constraints:</strong>
 * </p>
 * <ul>
 * <li>The {@link #getAllOrders(OrderFilterDto, Pageable)} method requires User read permission.</li>
 * <li>The {@link #getOrderById(Long)} method requires User read permission.</li>
 * <li>The {@link #addOrder(OrderDto)} method requires User write permission.</li>
 * <li>The {@link #deleteOrderById(Long)} method requires Admin write permission.</li>
 * </ul>
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;
    private final HateoasAdder<OrderDto> hateoasAdder;

    /**
     * Retrieves all orders with the given filter and pagination parameters.
     * @param orderFilterDto a {@link OrderFilterDto} object containing filter
     *                      parameters for the orders
     * @param pageable a {@link Pageable} object containing pagination parameters
     * @return a {@link ResponseEntity} object containing a list of {@link OrderDto}
     *          objects and an HTTP status code
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the given orderFilterDto is invalid
     */
    @GetMapping
    @UserReadPermission
    public ResponseEntity<List<OrderDto>> getAllOrders(OrderFilterDto orderFilterDto,
                                                       Pageable pageable) {
        List<OrderDto> orderDtos = orderService.findAll(orderFilterDto, pageable);
        hateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    /**
     * Retrieves an order with the given ID.
     * @param orderId the ID of the order to retrieve
     * @return a {@link ResponseEntity} object containing
     *         the {@link OrderDto} object and an HTTP status code
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the given orderId is invalid
     */
    @GetMapping("/{order-id}")
    @UserReadPermission
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable("order-id") @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.findById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    /**
     * Adds a new order.
     * @param orderDto the {@link OrderDto} object containing the order details
     * @return a {@link ResponseEntity} object containing the added
     *         {@link OrderDto} object and an HTTP status code
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the given orderDto is invalid
     */
    @PostMapping
    @UserWritePermission
    public ResponseEntity<OrderDto> addOrder(@RequestBody @Valid OrderDto orderDto) {
        OrderDto addedOrderDto = orderService.create(orderDto);
        hateoasAdder.addLinks(addedOrderDto);
        return new ResponseEntity<>(addedOrderDto, HttpStatus.CREATED);
    }

    /**
     * Deletes an order with the given ID.
     * @param orderId the ID of the order to delete
     * @return a {@link ResponseEntity} object containing the deleted
     *         {@link OrderDto} object and an HTTP status code
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws IllegalArgumentException if the given orderId is invalid
     */
    @DeleteMapping("/{order-id}")
    @AdminWritePermission
    public ResponseEntity<OrderDto> deleteOrderById(@PathVariable("order-id")
                                                    @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.deleteById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.NO_CONTENT);
    }
}
