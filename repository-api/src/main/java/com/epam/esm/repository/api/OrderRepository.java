package com.epam.esm.repository.api;


import com.epam.esm.domain.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface OrderRepository extends BaseRepository<Order, Long> {
    List<Order> findAllOrdersByUserId(LinkedMultiValueMap<String, String> fields,
                                      Pageable pageable,
                                      Long userId);
}
