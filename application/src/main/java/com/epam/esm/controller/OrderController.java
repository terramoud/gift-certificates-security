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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final HateoasAdder<OrderDto> hateoasAdder;

    @GetMapping
    @UserReadPermission
    public ResponseEntity<List<OrderDto>> getAllOrders(OrderFilterDto orderFilterDto,
                                                       Pageable pageable) {
        List<OrderDto> orderDtos = orderService.findAll(orderFilterDto, pageable);
        hateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    @UserReadPermission
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable("order-id") @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.findById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PostMapping
    @UserWritePermission
    public ResponseEntity<OrderDto> addOrder(@RequestBody @Valid OrderDto orderDto) {
        OrderDto addedOrderDto = orderService.create(orderDto);
        hateoasAdder.addLinks(addedOrderDto);
        return new ResponseEntity<>(addedOrderDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{order-id}")
    @AdminWritePermission
    public ResponseEntity<OrderDto> deleteOrderById(@PathVariable("order-id")
                                                    @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.deleteById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.NO_CONTENT);
    }
}
