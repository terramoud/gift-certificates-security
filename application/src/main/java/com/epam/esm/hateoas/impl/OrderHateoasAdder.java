package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.OrderController;
import com.epam.esm.domain.payload.*;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderHateoasAdder implements HateoasAdder<OrderDto> {

    private static final Class<OrderController> CONTROLLER = OrderController.class;
    private final HateoasAdder<UserDto> userHateoasAdder;
    private final HateoasAdder<CertificateDto> certificateHateoasAdder;

    @Autowired
    public OrderHateoasAdder(HateoasAdder<UserDto> userHateoasAdder,
                             HateoasAdder<CertificateDto> certificateHateoasAdder) {
        this.userHateoasAdder = userHateoasAdder;
        this.certificateHateoasAdder = certificateHateoasAdder;
    }

    @Override
    public void addLinks(OrderDto orderDto) {
        orderDto.add(linkTo(methodOn(CONTROLLER)
                .getOrderById(orderDto.getId()))
                .withSelfRel());
        orderDto.add(linkTo(methodOn(CONTROLLER)
                .deleteOrderById(orderDto.getId()))
                .withRel(DELETE));
        orderDto.add(linkTo(methodOn(CONTROLLER)
                .addOrder(orderDto))
                .withRel(CREATE));
        orderDto.add(linkTo(methodOn(CONTROLLER)
                .getAllOrders(new OrderFilterDto(), Pageable.ofSize(20)))
                .withRel("orders"));
        userHateoasAdder.addLinks(orderDto.getUser());
        certificateHateoasAdder.addLinks(orderDto.getCertificate());
    }
}
