package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.validation.CertificateValidator;
import com.epam.esm.domain.validation.UserValidator;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.OrderRepository;
import com.epam.esm.service.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    public static final String ORDER_ERROR_INVALID_ID = "order.error.invalid.id";
    public static final String ORDER_NOT_FOUND = "order.not.found";
    private final OrderRepository orderRepository;
    private final UserValidator userValidator;
    private final CertificateValidator certificateValidator;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserValidator userValidator,
                            CertificateValidator certificateValidator) {
        this.orderRepository = orderRepository;
        this.userValidator = userValidator;
        this.certificateValidator = certificateValidator;
    }

    @Override
    public List<Order> getAllOrders(LinkedMultiValueMap<String, String> fields,
                                    int size,
                                    int page) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return orderRepository.findAll(fields, pageRequest);
    }

    @Override
    public List<Order> getAllOrdersByUserId(LinkedMultiValueMap<String, String> fields,
                                            int size,
                                            int page,
                                            Long userId) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return orderRepository.findAllOrdersByUserId(fields, pageRequest, userId);
    }

    @Override
    public Order getOrderById(Long id) {
        if (id == null || id < 1) throw new InvalidResourcePropertyException(
                ORDER_ERROR_INVALID_ID, id, ErrorCodes.INVALID_ORDER_ID_PROPERTY);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE));
    }

    @Override
    public Order addOrder(Order order) {
        BigDecimal cost = order.getCost();
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) throw new InvalidResourcePropertyException(
                "order.error.invalid.cost", cost + "", ErrorCodes.INVALID_ORDER_ID_PROPERTY);
        if (!userValidator.validate(order.getUser())) throw new InvalidResourcePropertyException(
                "order.error.invalid.joined.user", cost + "", ErrorCodes.INVALID_ORDER_PROPERTY);
        order.setCreateDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderById(Order order, Long id) {
        if (id == null || id < 1) throw new InvalidResourcePropertyException(
                ORDER_ERROR_INVALID_ID, id, ErrorCodes.INVALID_ORDER_ID_PROPERTY);
        if (order.getId() == null ||
                order.getId() < 1 ||
                !order.getId().equals(id)) throw new InvalidResourcePropertyException(
                ORDER_ERROR_INVALID_ID, id, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        Order orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE));
        if (order.getCost() != null) orderToUpdate.setCost(order.getCost());
        if (order.getUser() != null) orderToUpdate.setUser(order.getUser());
        if (order.getCertificate() != null) orderToUpdate.setCertificate(order.getCertificate());
        BigDecimal cost = orderToUpdate.getCost();
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) throw new InvalidResourcePropertyException(
                "order.error.invalid.cost", cost + "", ErrorCodes.INVALID_ORDER_ID_PROPERTY);
        if (!userValidator.validate(orderToUpdate.getUser())) throw new InvalidResourcePropertyException(
                "order.error.invalid.joined.user",
                orderToUpdate.getUser().getLogin() + "",
                ErrorCodes.INVALID_ORDER_PROPERTY);
        if (!certificateValidator.validate(orderToUpdate.getCertificate())) throw new InvalidResourcePropertyException(
                "order.error.invalid.joined.certificate",
                orderToUpdate.getCertificate().getName() + "",
                ErrorCodes.INVALID_ORDER_PROPERTY);
        return orderRepository.update(orderToUpdate, id);
    }

    @Override
    public Order deleteOrderById(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
        return order;
    }
}
