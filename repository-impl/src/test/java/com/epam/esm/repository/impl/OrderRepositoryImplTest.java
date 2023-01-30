package com.epam.esm.repository.impl;

import com.epam.esm.config.*;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.OrderRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class OrderRepositoryImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * @see OrderRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListOrdersByPropertiesValues(LinkedMultiValueMap<String, String> fields,
                                                                   Pageable pageable,
                                                                   List<Order> expected) {
        List<Order> orders = orderRepository.findAll(fields, pageable);
        assertEquals(expected, orders);
    }

    /**
     * @see OrderRepositoryImpl#findAllOrdersByUserId(LinkedMultiValueMap, Pageable, Long)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllOrdersByUserId")
    void testFindAllOrdersByUserIdShouldReturnSortedListOrdersByUserId(LinkedMultiValueMap<String, String> fields,
                                                                     Pageable pageable,
                                                                     Long tagId,
                                                                     List<Order> expected) {
        List<Order> orders = orderRepository.findAllOrdersByUserId(fields, pageable, tagId);
        assertEquals(expected, orders);
    }


    /**
     * @see OrderRepositoryImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnOrderWithId() {
        TestOrders to = new TestOrders();
        Optional<Order> expected = Optional.of(to.order1);
        Optional<Order> order = orderRepository.findById(1L);
        assertEquals(expected, order);
    }

    /**
     * @see OrderRepositoryImpl#save(Order)
     */
    @Test
    void testSaveShouldCreateEntityInDB() {
        User newUser = new User();
        newUser.setLogin("new login");
        newUser.setLogin("new Email");
        newUser.setLogin("passwor");
        newUser.setLogin("user");
        Certificate newCertificate = new Certificate();
        newCertificate.setName("new gift certificate");
        newCertificate.setDescription("new gift certificate description");
        newCertificate.setPrice(new BigDecimal("99.98"));
        newCertificate.setDuration(40);
        newCertificate.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        newCertificate.setLastUpdateDate(LocalDateTime.now());
        Tag tag1 = new Tag();
        tag1.setName("new tag1");
        Tag tag5 = new Tag();
        tag5.setName("new tag5");
        Tag tag11 = new Tag();
        tag11.setName("new tag11");
        newCertificate.setTags(Set.of(tag1, tag11, tag5));
        Order newOrder = new Order();
        newOrder.setCost(new BigDecimal("99999.99"));
        newOrder.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        newOrder.setUser(newUser);
        newOrder.setCertificate(newCertificate);
        Order savedOrder = orderRepository.save(newOrder);
        System.out.println("savedOrder.getCertificate().getTags() = " + savedOrder.getCertificate().getTags());
        System.out.println("savedOrder.getCertificate() = " + savedOrder.getCertificate());
//        Order expected = orderRepository.findById(savedOrder.getId()).get();
        assertEquals(newOrder, savedOrder);
    }

    /**
     * @see OrderRepositoryImpl#update(Order, Long)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        TestUsers tu = new TestUsers();
        TestCertificates tc = new TestCertificates();
        Order order = orderRepository.findById(1L).get();
        order.setCost(new BigDecimal("99999.99"));
        order.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        tu.user1.setLogin("updated login");
        order.setUser(tu.user1);
        tc.certificate1.setName("updated Certificate");
        order.setCertificate(tc.certificate1);
        Order updatedOrder = orderRepository.update(order, 1L);
        Order expected = orderRepository.findById(1L).get();
        System.out.println("expected = " + expected);
        assertEquals(expected, updatedOrder);
    }

    /**
     * @see OrderRepositoryImpl#delete(Order)
     */
    @Test
    void testDeleteShouldDeleteEntityFromDB() {
        Optional<Order> certificateToDelete = orderRepository.findById(1L);
        orderRepository.delete(certificateToDelete.get());
        Optional<Order> expected = orderRepository.findById(1L);
        assertThat(expected).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        TestOrders to = new TestOrders();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+id, -cost"))),
                        PageRequest.of(2, 1),
                        List.of(to.order3)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(1, 10),
                        List.of()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(0, 10),
                        List.of(to.order1,
                                to.order2,
                                to.order3,
                                to.order4,
                                to.order5)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id"))),
                        PageRequest.of(1, 2),
                        List.of(to.order3,
                                to.order2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+cost"))),
                        PageRequest.of(1, 3),
                        List.of(to.order5,
                                to.order4)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+cost"))),
                        PageRequest.of(2, 4),
                        List.of()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id, -create_date"))),
                        PageRequest.of(1, 4),
                        List.of(to.order1)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(0, 10),
                        List.of(to.order1,
                                to.order2,
                                to.order3,
                                to.order4,
                                to.order5)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-create_date, +cost"))),
                        PageRequest.of(0, 5),
                        List.of(to.order5,
                                to.order4,
                                to.order3,
                                to.order2,
                                to.order1))
        );
    }

    private static Stream<Arguments> testCasesForFindAllOrdersByUserId() {
        TestOrders to = new TestOrders();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+create_date"))),
                        PageRequest.of(0, 5),
                        1L,
                        List.of(to.order1,
                                to.order2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-create_date, +cost"))),
                        PageRequest.of(0, 5),
                        2L,
                        List.of(to.order5,
                                to.order4,
                                to.order3))
        );
    }
}