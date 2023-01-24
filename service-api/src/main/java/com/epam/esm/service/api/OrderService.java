package com.epam.esm.service.api;

import com.epam.esm.domain.entity.Order;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders(LinkedMultiValueMap<String, String> fields, int size, int page);

    List<Order> getAllOrdersByUserId(LinkedMultiValueMap<String, String> fields,
                                     int size,
                                     int page,
                                     Long userId);

    Order getOrderById(Long id);

    Order addOrder(Order order);

    Order updateOrderById(Order order, Long id);

    Order deleteOrderById(Long id);
}
