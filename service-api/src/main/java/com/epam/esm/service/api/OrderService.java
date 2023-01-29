package com.epam.esm.service.api;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface OrderService {
    List<OrderDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto);

    List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> fields, PageDto pageDto, Long userId);

    OrderDto findById(Long id);

    OrderDto create(OrderDto orderDto);

    OrderDto update(OrderDto order, Long id);

    OrderDto deleteById(Long id);
}
