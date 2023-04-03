package com.epam.esm.service.impl;

import com.epam.esm.config.ServiceTestConfig;
import com.epam.esm.domain.converter.impl.*;
import com.epam.esm.domain.converter.impl.OrderDtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.*;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.exceptions.ResourceUnsupportedOperationException;
import com.epam.esm.repository.api.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestConfig.class})
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderServiceImplTest {

    private static final OrderDto EXPECTED = new OrderDto(
            1L,
            new BigDecimal("10.10"),
            LocalDateTime.parse("2023-01-03T07:37:15"),
            new UserDto(),
            new CertificateDto());

    private final Order orderToCreate = new Order(1L,
            new BigDecimal("10.10"),
            LocalDateTime.parse("2023-01-03T07:37:15"),
            new User(1L, null, null, null, null),
            new Certificate(1L, null, null, null, null, null, null));

    private final List<Order> orders = LongStream.range(0, 9L)
            .mapToObj(i -> new Order(i,
                    new BigDecimal(i + "10.10"),
                    LocalDateTime.parse("2023-01-03T07:37:15"),
                    new User(),
                    new Certificate()))
            .sorted(Comparator.comparing(Order::getCost)
                    .thenComparing(Order::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());

    private final List<OrderDto> expectedOrders = LongStream.range(0, 9L)
            .mapToObj(i -> new OrderDto(i,
                    new BigDecimal(i + "10.10"),
                    LocalDateTime.parse("2023-01-03T07:37:15"),
                    new UserDto(),
                    new CertificateDto()))
            .sorted(Comparator.comparing(OrderDto::getCost)
                    .thenComparing(OrderDto::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());

    @Mock
    public final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

    @Mock
    private final OrderDtoConverter converter = Mockito.mock(OrderDtoConverter.class);

    @Mock
    private final UserDtoConverter userConverter = Mockito.mock(UserDtoConverter.class);

    @Mock
    private final CertificateDtoConverter certificateConverter = Mockito.mock(CertificateDtoConverter.class);

    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);

    @Mock
    private final CertificateServiceImpl certificateService = Mockito.mock(CertificateServiceImpl.class);

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository,
                converter,
                userConverter,
                certificateConverter,
                userService,
                certificateService);
    }

    @AfterEach
    void tearDown() {
        reset(orderRepository);
        reset(converter);
        reset(userConverter);
        reset(userService);
        reset(certificateConverter);
        reset(certificateService);
    }

    /**
     * @see OrderServiceImpl#findAll(OrderFilterDto, Pageable)
     */
    @Test
    void testFindAllShouldReturnSortedListOrdersByCostAndId() {
        when(orderRepository.findAll(any(OrderFilterDto.class), any(Pageable.class)))
                .thenReturn(orders);
        when(converter.toDto(anyList())).thenReturn(expectedOrders);
        assertEquals(expectedOrders,
                orderService.findAll(new OrderFilterDto(), Pageable.unpaged()));
    }

    /**
     * @see OrderServiceImpl#findAllByUserId(Long, OrderFilterDto, Pageable)
     */
    @Test
    void testFindAllByUserIdShouldReturnSortedListOrdersByUserId() {
        when(orderRepository.findAllByUserId(anyLong(), any(OrderFilterDto.class), any(Pageable.class)))
                .thenReturn(orders);
        when(converter.toDto(anyList())).thenReturn(expectedOrders);
        assertEquals(expectedOrders,
                orderService.findAllByUserId(1L, new OrderFilterDto(), Pageable.unpaged()));
    }

    /**
     * @see OrderServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnOrderDto() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(new Order()));
        when(converter.toDto(any(Order.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, orderService.findById(anyLong()));
    }

    /**
     * @see OrderServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldThrowExceptionWhenOrderIsNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.findById(1L));
    }

    /**
     * @see OrderServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldDeleteOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(new Order()));
        when(converter.toDto(any(Order.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, orderService.deleteById(anyLong()));
    }

    /**
     * @see OrderServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldThrowExceptionWhenOrderIsNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteById(1L));
    }

    /**
     * @see OrderServiceImpl#update(Long, OrderDto)
     */
    @Test
    void testUpdateShouldThrowException() {
        OrderDto orderDto = new OrderDto(1L,
                new BigDecimal("10.10"),
                LocalDateTime.parse("2023-01-03T07:37:15"),
                new UserDto(),
                new CertificateDto());
        assertThrows(ResourceUnsupportedOperationException.class, () -> orderService.update(1L, orderDto));
    }

    /**
     * @see OrderServiceImpl#create(OrderDto)
     */
    @Test
    void testCreateShouldCreateNewOrderAndReturnOrderDto() {
        when(converter.toEntity(any(OrderDto.class))).thenReturn(orderToCreate);
        when(userService.findById(any())).thenReturn(new UserDto());
        when(userConverter.toEntity(any(UserDto.class))).thenReturn(new User());
        when(certificateService.findById(any())).thenReturn(new CertificateDto());
        when(certificateConverter.toEntity(any(CertificateDto.class))).thenReturn(new Certificate());
        when(orderRepository.save(any())).thenReturn(new Order());
        when(converter.toDto(any(Order.class))).thenReturn(EXPECTED);
        assertEquals(EXPECTED, orderService.create(new OrderDto()));
    }

    /**
     * @see OrderServiceImpl#create(OrderDto)
     */
    @Test
    void testCreateShouldThrowExceptionWhenObtainOrderIsNullOrUserIsNullOrCertificateIsNull() {
        OrderDto orderDto = new OrderDto();
        when(converter.toEntity(any(OrderDto.class))).thenReturn(null);
        assertThrows(NullPointerException.class, () -> orderService.create(orderDto));
        reset(converter);
        Order order = new Order();
        order.setUser(null);
        order.setCertificate(new Certificate());
        when(converter.toEntity(any(OrderDto.class))).thenReturn(order);
        assertThrows(NullPointerException.class, () -> orderService.create(orderDto));
        reset(converter);
        order.setUser(new User());
        order.setCertificate(null);
        when(converter.toEntity(any(OrderDto.class))).thenReturn(order);
        assertThrows(NullPointerException.class, () -> orderService.create(orderDto));
    }

    /**
     * @see OrderServiceImpl#create(OrderDto)
     */
    @Test
    void testCreateShouldThrowExceptionWhenUserOrCertificateIsNotExists() {
        OrderDto orderDto = new OrderDto();
        when(converter.toEntity(any(OrderDto.class))).thenThrow(ResourceNotFoundException.class);
        when(userService.findById(any())).thenReturn(new UserDto());
        assertThrows(ResourceNotFoundException.class, () -> orderService.create(orderDto));
        reset(userService);
        when(certificateService.findById(any())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> orderService.create(orderDto));
    }

    /**
     * @see OrderServiceImpl#create(OrderDto)
     */
    @ParameterizedTest
    @MethodSource("exceptionalTestCasesFroCreateMethod")
    void testCreateShouldThrowExceptionWhenOrderHasUserThatHasInvalidIdOrCertificateHasInvalidId(
            Long userId,
            Long certificateId) {
        OrderDto orderDto = new OrderDto();
        orderToCreate.getUser().setId(userId);
        orderToCreate.getCertificate().setId(certificateId);
        when(converter.toEntity(any(OrderDto.class))).thenReturn(orderToCreate);
        assertThrows(InvalidResourcePropertyException.class, () -> orderService.create(orderDto));
        reset(converter);
        orderToCreate.getUser().setId(1L);
        orderToCreate.getCertificate().setId(1L);
    }

    private static Stream<Arguments> exceptionalTestCasesFroCreateMethod() {
        return Stream.of(
                Arguments.of(null, 1L),
                Arguments.of(0L, 1L),
                Arguments.of(-999L, 1L),
                Arguments.of(1L, null),
                Arguments.of(1L, 0L),
                Arguments.of(1L, -999L)
        );
    }
}