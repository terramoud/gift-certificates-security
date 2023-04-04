package com.epam.esm.repository.api;

import com.epam.esm.config.*;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.CertificateFilterDto;
import com.epam.esm.domain.payload.OrderFilterDto;
import com.epam.esm.domain.payload.UserFilterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The {@link OrderRepositoryTest} class provides integration tests
 * for {@link OrderRepository} implementation.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    /**
     * Test {@link OrderRepository#findAll(OrderFilterDto, Pageable)} method.
     *
     * @param userFilterDto filter criteria
     * @param pageable pagination criteria
     * @param expected expected results
     * @see OrderRepository#findAll(OrderFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListOrdersByPropertiesValues(OrderFilterDto userFilterDto,
                                                                   Pageable pageable,
                                                                   List<Order> expected) {
        List<Order> orders = orderRepository.findAll(userFilterDto, pageable);
        assertEquals(expected, orders);
    }

    /**
     * Test {@link OrderRepository#findAllByUserId(Long, OrderFilterDto, Pageable)} method.
     *
     * @param orderFilterDto filter criteria
     * @param pageable pagination criteria
     * @param userId user id
     * @param expected expected results
     * @see OrderRepository#findAllByUserId(Long, OrderFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllOrdersByUserId")
    void testFindAllOrdersByUserIdShouldReturnSortedListOrdersByUserId(OrderFilterDto orderFilterDto,
                                                                       Pageable pageable,
                                                                       Long userId,
                                                                       List<Order> expected) {
        List<Order> orders = orderRepository.findAllByUserId(userId, orderFilterDto, pageable);
        assertEquals(expected, orders);
    }


    /**
     * @see OrderRepository#findById(Object)
     */
    @Test
    void testFindByIdShouldReturnOrderWithId() {
        TestOrders to = new TestOrders();
        Optional<Order> expected = Optional.of(to.order1);
        Optional<Order> order = orderRepository.findById(1L);
        assertEquals(expected, order);
    }

    /**
     * @see OrderRepository#save(Object)
     */
    @Test
    void testSaveShouldCreateEntityInDB() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setLogin("new login");
        newUser.setLogin("new Email");
        newUser.setLogin("password");
        newUser.setLogin("user");
        Certificate newCertificate = new Certificate();
        newCertificate.setId(1L);
        newCertificate.setName("new gift certificate");
        newCertificate.setDescription("new gift certificate description");
        newCertificate.setPrice(new BigDecimal("99.98"));
        newCertificate.setDuration(40);
        newCertificate.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        newCertificate.setLastUpdateDate(LocalDateTime.now());
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("new tag1");
        Tag tag5 = new Tag();
        tag5.setId(5L);
        tag5.setName("new tag5");
        Tag tag11 = new Tag();
        tag11.setId(11L);
        tag11.setName("new tag11");
        newCertificate.setTags(Set.of(tag1, tag11, tag5));
        Order newOrder = new Order();
        newOrder.setCost(new BigDecimal("99999.99"));
        newOrder.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        newOrder.setUser(newUser);
        newOrder.setCertificate(newCertificate);
        Order savedOrder = orderRepository.save(newOrder);
        Order expected = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertEquals(expected, savedOrder);
    }

    /**
     * @see BaseRepository#save(Object)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        TestUsers tu = new TestUsers();
        TestCertificates tc = new TestCertificates();
        Order order = orderRepository.findById(1L).orElseThrow();
        order.setCost(new BigDecimal("99999.99"));
        order.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        tu.user1.setLogin("updated login");
        order.setUser(tu.user1);
        tc.certificate1.setName("updated Certificate");
        order.setCertificate(tc.certificate1);
        Order updatedOrder = orderRepository.save(order);
        Order expected = orderRepository.findById(1L).orElseThrow();
        assertEquals(expected, updatedOrder);
    }

    /**
     * @see OrderRepository#delete(Object)
     */
    @Test
    void testDeleteShouldDeleteEntityFromDB() {
        Optional<Order> orderToDelete = orderRepository.findById(1L);
        orderRepository.delete(orderToDelete.orElseThrow());
        Optional<Order> expected = orderRepository.findById(1L);
        assertThat(expected).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        TestOrders to = new TestOrders();
        OrderFilterDto case1 = new OrderFilterDto();
        case1.setCost(new BigDecimal("10.10"));
        OrderFilterDto case2 = new OrderFilterDto();
        case2.setCost(new BigDecimal("10.10"));
        OrderFilterDto case11 = new OrderFilterDto();
        case11.setCertificate(CertificateFilterDto.builder().name("premium").build());
        OrderFilterDto case12 = new OrderFilterDto();
        case12.setUser(UserFilterDto.builder().id(1L).build());
        case12.setCost(new BigDecimal("30.30"));
        OrderFilterDto case13 = new OrderFilterDto();
        case13.setUser(UserFilterDto.builder().id(1L).build());
        return Stream.of(
                Arguments.of(
                        case1,
                        PageRequest.of(2, 1, Sort.by("id").ascending()
                                .and(Sort.by("cost").descending())),
                        List.of()),
                Arguments.of(
                        case2,
                        PageRequest.of(0, 5, Sort.by("id").ascending()
                                .and(Sort.by("cost").descending())),
                        List.of(to.order1)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(1, 10, Sort.by("id")),
                        List.of(to.order11)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        List.of(to.order1,
                                to.order2,
                                to.order3,
                                to.order4,
                                to.order5,
                                to.order6,
                                to.order7,
                                to.order8,
                                to.order9,
                                to.order10)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(1, 2, Sort.by("id").descending()),
                        List.of(to.order9,
                                to.order8)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(1, 3, Sort.by("cost")),
                        List.of(to.order5,
                                to.order6,
                                to.order7)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(2, 4, Sort.by("cost")),
                        List.of(to.order10,
                                to.order11,
                                to.order4)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(1, 4, Sort.by("id").ascending()
                                .and(Sort.by("createDate").descending())),
                        List.of(to.order5,
                                to.order6,
                                to.order7,
                                to.order8)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        List.of(to.order1,
                                to.order2,
                                to.order3,
                                to.order4,
                                to.order5,
                                to.order6,
                                to.order7,
                                to.order8,
                                to.order9,
                                to.order10)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(0, 5, Sort.by("createDate").descending()
                                .and(Sort.by("cost").ascending())),
                        List.of(to.order5,
                                to.order6,
                                to.order7,
                                to.order8,
                                to.order9)),
                Arguments.of(
                        case11,
                        PageRequest.of(0, 5, Sort.by("createDate").descending()
                                .and(Sort.by("cost").ascending())),
                        List.of(to.order5,
                                to.order6,
                                to.order7,
                                to.order8,
                                to.order9)),
                Arguments.of(
                        case12,
                        PageRequest.of(0, 5, Sort.by("createDate").descending()
                                .and(Sort.by("cost").ascending())),
                        List.of(to.order2)),
                Arguments.of(
                        case13,
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("cost").descending())),
                        List.of(to.order1,
                                to.order2,
                                to.order5,
                                to.order6,
                                to.order7))
        );
    }

    private static Stream<Arguments> testCasesForFindAllOrdersByUserId() {
        TestOrders to = new TestOrders();
        return Stream.of(
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(0, 5, Sort.by("createDate")),
                        1L,
                        List.of(to.order1,
                                to.order2,
                                to.order5,
                                to.order6,
                                to.order7)),
                Arguments.of(
                        new OrderFilterDto(),
                        PageRequest.of(0, 5, Sort.by("createDate").descending()
                                .and(Sort.by("cost").ascending())),
                        2L,
                        List.of(to.order4,
                                to.order3))
        );
    }
}
