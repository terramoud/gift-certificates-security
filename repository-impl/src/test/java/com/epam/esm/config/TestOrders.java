package com.epam.esm.config;

import com.epam.esm.domain.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class TestOrders {

    private final TestCertificates tc = new TestCertificates();
    private final TestUsers tu = new TestUsers();
    public Order order1 = new Order(1L,
            new BigDecimal("10.10"),
            LocalDateTime.parse("2023-01-03T07:37:14.974"),
            tu.user1,
            tc.certificate1);
    public Order order2 = new Order(2L,
            new BigDecimal("30.30"),
            LocalDateTime.parse("2023-01-04T07:37:14.974"),
            tu.user1,
            tc.certificate2);
    public Order order3 = new Order(3L,
            new BigDecimal("20.20"),
            LocalDateTime.parse("2023-01-05T07:37:14.974"),
            tu.user2,
            tc.certificate1);
    public Order order4 = new Order(4L,
            new BigDecimal("2001.98"),
            LocalDateTime.parse("2023-01-05T08:37:14.974"),
            tu.user2,
            tc.certificate2);
    public Order order5 = new Order(5L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order6 = new Order(6L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order7 = new Order(7L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order8 = new Order(8L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order9 = new Order(9L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order10 = new Order(10L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
    public Order order11 = new Order(11L,
            new BigDecimal("1099.99"),
            LocalDateTime.parse("2023-01-05T09:37:14.974"),
            tu.user1,
            tc.certificate6);
}
