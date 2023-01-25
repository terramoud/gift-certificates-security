package com.epam.esm.controller;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.hateoas.HateoasAdder;
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
    private final HateoasAdder<OrderDto> hateoasAdder;

    @Autowired
    public OrderController(OrderService orderService, DtoConverter<Order, OrderDto> converter, HateoasAdder<OrderDto> hateoasAdder) {
        this.orderService = orderService;
        this.converter = converter;
        this.hateoasAdder = hateoasAdder;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Order> orders = orderService.getAllOrders(allRequestParameters, size, page);
        List<OrderDto> orderDtos = converter.listToDtos(orders);
        hateoasAdder.addLinks(orderDtos);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("order-id") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderDto orderDto = converter.toDto(order);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
        Order addedOrder = orderService.addOrder(converter.toEntity(orderDto));
        OrderDto addedOrderDto = converter.toDto(addedOrder);
        hateoasAdder.addLinks(addedOrderDto);
        return new ResponseEntity<>(addedOrderDto, HttpStatus.CREATED);
    }

    @PutMapping("/{order-id}")
    public ResponseEntity<OrderDto> updateOrderById(@PathVariable("order-id") Long orderId,
                                                    @RequestBody OrderDto orderDto) {
        Order updatedOrder = orderService.updateOrderById(converter.toEntity(orderDto), orderId);
        OrderDto updatedOrderDto = converter.toDto(updatedOrder);
        hateoasAdder.addLinks(updatedOrderDto);
        return new ResponseEntity<>(updatedOrderDto, HttpStatus.OK);
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<OrderDto> deleteOrderById(@PathVariable("order-id") Long orderId) {
        Order order = orderService.deleteOrderById(orderId);
        OrderDto orderDto = converter.toDto(order);
        hateoasAdder.addLinks(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}
