package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.validation.CertificateValidator;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.UserValidator;
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
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
@Validated
public class OrderServiceImpl extends AbstractService<OrderDto, Long> implements OrderService {

    private static final String ORDER_INVALID_ID = "order.error.invalid.id";
    private static final String ORDER_NOT_FOUND = "order.not.found";
    private static final String WRONG_USER_ID = "wrong.tag.id";
    private static final String USER_ID_NOT_MAPPED = "tag.id.not.mapped";
    private static final String CHANGE_FILLED_ORDER = "forbidden.change.filled.order";

    private final OrderRepository orderRepository;
    private final UserValidator userValidator;
    private final CertificateValidator certificateValidator;
    private final DtoConverter<Order, OrderDto> converter;

    @Override
    public List<OrderDto> findAll(LinkedMultiValueMap<String, String> fields, @Valid PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(orderRepository.findAll(fields, pageRequest));
    }

    @Override
    public List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> fields,
                                          @Valid PageDto pageDto,
                                          @Positive(message = WRONG_USER_ID) Long id) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(orderRepository.findAllOrdersByUserId(fields, pageRequest, id));
    }

    @Override
    public OrderDto findById(@Positive(message = WRONG_USER_ID) Long id) {
        return converter.toDto(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE)));
    }

    @Validated(OnCreate.class)
    @Override
    public OrderDto create(@Valid OrderDto orderDto) {
        return converter.toDto(orderRepository.save(converter.toEntity(orderDto)));
    }

    @Override
    public OrderDto update(@Positive(message = WRONG_USER_ID) Long id,
                           @Valid OrderDto orderDto) {
        throw new ResourceUnsupportedOperationException(CHANGE_FILLED_ORDER);
    }

    @Override
    public OrderDto deleteById(@Positive(message = WRONG_USER_ID) Long id) {
        OrderDto orderDto = findById(id);
        orderRepository.delete(converter.toEntity(orderDto));
        return orderDto;
    }
}
