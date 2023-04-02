package com.epam.esm.service.api;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.OrderFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService extends BaseService<OrderDto, Long> {

    List<OrderDto> findAllByUserId(Long userId, OrderFilterDto orderFilterDto, Pageable pageable);

    List<OrderDto> findAll(OrderFilterDto orderFilterDto, Pageable pageable);
}
