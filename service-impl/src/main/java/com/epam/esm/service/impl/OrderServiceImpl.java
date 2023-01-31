package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.OrderRepository;
import com.epam.esm.service.api.OrderService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class OrderServiceImpl extends AbstractService<OrderDto, Long> implements OrderService {


    private final OrderRepository orderRepository;
    private final DtoConverter<Order, OrderDto> converter;

    @Override
    public List<OrderDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(orderRepository.findAll(fields, pageRequest));
    }

    @Override
    public List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> fields,
                                          PageDto pageDto,
                                          Long id) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(orderRepository.findAllOrdersByUserId(fields, pageRequest, id));
    }

    @Override
    public OrderDto findById(Long id) {
        return converter.toDto(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE)));
    }

    @Override
    public OrderDto create(OrderDto orderDto) {
        return converter.toDto(orderRepository.save(converter.toEntity(orderDto)));
    }

    @Override
    public OrderDto update(Long id, OrderDto orderDto) {
        throw new ResourceUnsupportedOperationException(CHANGE_FILLED_ORDER);
    }

    @Override
    public OrderDto deleteById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE));
        orderRepository.delete(order);
        return converter.toDto(order);
    }
}
