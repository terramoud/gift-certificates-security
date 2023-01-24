package com.epam.esm.domain.converter;


import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoConverter implements DtoConverter<Order, OrderDto> {

    private final UserDtoConverter userConverter;
    private final CertificateDtoConverter certificateConverter;

    @Autowired
    public OrderDtoConverter(UserDtoConverter userConverter,
                             CertificateDtoConverter certificateConverter) {
        this.userConverter = userConverter;
        this.certificateConverter = certificateConverter;
    }

    @Override
    public Order toEntity(OrderDto dto) {
        return new Order(
                dto.getId(),
                dto.getCost(),
                dto.getCreateDate(),
                userConverter.toEntity(dto.getUser()),
                certificateConverter.toEntity(dto.getCertificate())
        );
    }

    @Override
    public OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCost(),
                order.getCreateDate(),
                userConverter.toDto(order.getUser()),
                certificateConverter.toDto(order.getCertificate())
        );
    }
}
