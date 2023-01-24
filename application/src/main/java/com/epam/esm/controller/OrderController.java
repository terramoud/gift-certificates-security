package com.epam.esm.controller;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.service.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final DtoConverter<Order, OrderDto> converter;

    @Autowired
    public OrderController(OrderService orderService, DtoConverter<Order, OrderDto> converter) {
        this.orderService = orderService;
        this.converter = converter;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Order> orders = orderService.getAllOrders(allRequestParameters, size, page);
        return new ResponseEntity<>(converter.listToDtos(orders), HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("order-id") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(converter.toDto(order), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
        Order order = converter.toEntity(orderDto);
        Order addedOrder = orderService.addOrder(order);
        return new ResponseEntity<>(converter.toDto(addedOrder), HttpStatus.CREATED);
    }

    @PutMapping("/{order-id}")
    public ResponseEntity<OrderDto> updateOrderById(@PathVariable("order-id") Long orderId,
                                                    @RequestBody OrderDto tagDto) {
        Order updatedOrder = orderService.updateOrderById(converter.toEntity(tagDto), orderId);
        return new ResponseEntity<>(converter.toDto(updatedOrder), HttpStatus.OK);
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<OrderDto> deleteOrderById(@PathVariable("order-id") Long orderId) {
        Order order = orderService.deleteOrderById(orderId);
        return new ResponseEntity<>(converter.toDto(order), HttpStatus.OK);
    }
}
