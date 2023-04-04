package com.epam.esm.service.api;

import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.OrderFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface represents the service layer for orders.
 * Provides methods to perform CRUD operations on OrderDto objects.
 * Additionally, provides methods to retrieve a list of orders
 * filtered by user ID or with pagination.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface OrderService extends BaseService<OrderDto, Long> {

    /**
     * Finds all orders by the filter.
     *
     * @param orderFilterDto the filter for orders.
     * @param pageable the pagination information.
     * @return the list of found orders.
     */
    List<OrderDto> findAll(OrderFilterDto orderFilterDto, Pageable pageable);

    /**
     * Finds all orders by the user ID and filter.
     *
     * @param userId the ID of the user whose orders need to be found.
     * @param orderFilterDto the filter for orders.
     * @param pageable the pagination information.
     * @return the list of found orders.
     */
    List<OrderDto> findAllByUserId(Long userId, OrderFilterDto orderFilterDto, Pageable pageable);
}
