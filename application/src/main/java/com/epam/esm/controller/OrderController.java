package com.epam.esm.controller;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.OrderService;
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
@RequestMapping("api/v1/orders")
@AllArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final HateoasAdder<OrderDto> hateoasAdder;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0")
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = "5")
            @Positive(message = INVALID_SIZE) int size) {
        List<OrderDto> orderDtos = orderService.findAll(allRequestParameters, new PageDto(page, size));
        hateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable("order-id") @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.findById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody @Valid OrderDto orderDto) {
        OrderDto addedOrderDto = orderService.create(orderDto);
        hateoasAdder.addLinks(addedOrderDto);
        return new ResponseEntity<>(addedOrderDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<OrderDto> deleteOrderById(@PathVariable("order-id")
                                                    @Positive(message = ORDER_INVALID_ID) Long orderId) {
        OrderDto orderDto = orderService.deleteById(orderId);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}
