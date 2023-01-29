package com.epam.esm.service.api;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface OrderService extends BaseService<OrderDto, Long> {

    List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> fields, PageDto pageDto, Long userId);

}
