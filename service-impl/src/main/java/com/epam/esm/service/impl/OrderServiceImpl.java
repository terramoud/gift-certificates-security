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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.Join;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
public class OrderServiceImpl extends AbstractService<OrderDto, Long> implements OrderService {

    private static final String ID = "id";
    private static final String COST = "cost";
    private static final String CREATE_DATE = "createDate";
    private static final String JOINED_FIELD_CERTIFICATE = "certificate";
    private static final String JOINED_FIELD_USER = "user";
    private static final String CERTIFICATE_JOINED_FIELD_TAGS = "tags";
    private static final String USER_ID = "id";
    private static final String[] FIELDS_FOR_SEARCH = {};
    private static final Long MIN_ENTITY_ID = 1L;
    private static final String JOINED_FIELD_TAGS = "tags";
    private static final Map<String, Sort> sortMap = Map.of(
            "+id", Sort.by(Sort.Direction.ASC, ID),
            "-id", Sort.by(Sort.Direction.DESC, ID),
            "+cost", Sort.by(Sort.Direction.ASC, COST),
            "-cost", Sort.by(Sort.Direction.DESC, COST),
            "+createDate", Sort.by(Sort.Direction.ASC, CREATE_DATE),
            "-createDate", Sort.by(Sort.Direction.DESC, CREATE_DATE)
    );

    private static final Map<String, Function<String, Specification<Order>>> filterMap = Map.of(
            COST, filterValue -> (root, query, cb) -> cb.equal(root.get(COST), filterValue),
            CREATE_DATE, filterValue -> (root, query, cb) -> cb.equal(root.get(CREATE_DATE), filterValue)
    );

    private static final Map<String, Function<String, Specification<Order>>> searchMap = Map.of();
    private final OrderRepository orderRepository;
    private final DtoConverter<Order, OrderDto> converter;
    private final DtoConverter<User, UserDto> userConverter;
    private final DtoConverter<Certificate, CertificateDto> certificateConverter;
    private final UserService userService;
    private final CertificateService certificateService;

    @Override
    @Transactional
    public List<OrderDto> findAll(LinkedMultiValueMap<String, String> requestParams, PageDto pageDto) {
        List<Order> orders = findAllAbstract(requestParams, pageDto, orderRepository, sortMap, filterMap, searchMap);
        return converter.toDto(orders);
    }

    @Override
    @Transactional
    public List<OrderDto> findAllByUserId(LinkedMultiValueMap<String, String> requestParams,
                                          PageDto pageDto,
                                          Long id) {
        Specification<Order> whereJoinedUserIdEquals = (root, query, cb) -> {
            Join<Order, User> userJoin = root.join(JOINED_FIELD_USER);
            return cb.equal(userJoin.get(USER_ID), id);
        };
        List<Order> orders = findAllAbstract(requestParams,
                pageDto,
                orderRepository,
                sortMap,
                filterMap,
                searchMap,
                whereJoinedUserIdEquals);
        return converter.toDto(orders);
    }

    @Override
    @Transactional
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
        order.setCost(certificate.getPrice());
        order.setUser(user);
        order.setCertificate(certificate);
        Order savedOrder = orderRepository.save(order);
        return converter.toDto(savedOrder);
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
