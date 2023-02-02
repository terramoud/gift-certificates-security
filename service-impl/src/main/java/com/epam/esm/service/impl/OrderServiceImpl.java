package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.OrderRepository;
import com.epam.esm.service.api.CertificateService;
import com.epam.esm.service.api.OrderService;
import com.epam.esm.service.api.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
public class OrderServiceImpl extends AbstractService<OrderDto, Long> implements OrderService {

    private static final Long MIN_ENTITY_ID = 1L;
    private final OrderRepository orderRepository;
    private final DtoConverter<Order, OrderDto> converter;
    private final DtoConverter<User, UserDto> userConverter;
    private final DtoConverter<Certificate, CertificateDto> certificateConverter;
    private final UserService userService;
    private final CertificateService certificateService;

    @Override
    public List<OrderDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        List<Order> orders = orderRepository.findAll(fields, pageRequest);
        return converter.toDto(orders);
    }

    @Override
    public List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> fields,
                                          PageDto pageDto,
                                          Long id) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        List<Order> orders = orderRepository.findAllOrdersByUserId(fields, pageRequest, id);
        return converter.toDto(orders);
    }

    @Override
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE));
        return converter.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto create(OrderDto orderDto) {
        Order order = converter.toEntity(orderDto);
        Long userId = order.getUser().getId();
        Long certificateId = order.getCertificate().getId();
        User user = getUserById(userId);
        Certificate certificate = getCertificateById(certificateId);
        order.setUser(user);
        order.setCertificate(certificate);
        orderRepository.save(order);
        return converter.toDto(order);
    }

    private User getUserById(Long userId) {
        if (userId == null || userId < MIN_ENTITY_ID) {
            throw new InvalidResourcePropertyException(USER_INVALID_ID, userId, INVALID_ID_PROPERTY);
        }
        UserDto userDto = userService.findById(userId);
        return userConverter.toEntity(userDto);
    }

    private Certificate getCertificateById(Long certificateId) {
        if (certificateId == null || certificateId < MIN_ENTITY_ID) {
            throw new InvalidResourcePropertyException(CERTIFICATE_INVALID_ID, certificateId, INVALID_ID_PROPERTY);
        }
        CertificateDto certificateDto = certificateService.findById(certificateId);
        return certificateConverter.toEntity(certificateDto);
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderDto orderDto) {
        throw new ResourceUnsupportedOperationException(CHANGE_FILLED_ORDER);
    }

    @Override
    @Transactional
    public OrderDto deleteById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ORDER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_ORDER_RESOURCE));
        orderRepository.delete(order);
        return converter.toDto(order);
    }
}
