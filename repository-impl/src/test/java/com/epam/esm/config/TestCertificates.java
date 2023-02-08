package com.epam.esm.config;

import com.epam.esm.domain.entity.Certificate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestCertificates {
    public Certificate certificate1 = new Certificate(1L,
            "standard",
            "standard level gift certificate",
            new BigDecimal("999.99"),
            180,
            LocalDateTime.parse("2023-01-02T07:37:14.974"),
            LocalDateTime.parse("2023-01-02T07:37:14.974"));

    public Certificate certificate2 = new Certificate(2L,
            "standard plus",
            "standard plus level gift certificate",
            new BigDecimal("699.99"),
            120,
            LocalDateTime.parse("2023-01-03T07:37:14.974"),
            LocalDateTime.parse("2023-01-03T07:37:14.974"));

    public Certificate certificate3 = new Certificate(3L,
            "standard extra",
            "standard extra level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-04T07:37:14.974"),
            LocalDateTime.parse("2023-01-04T07:37:14.974"));

    public Certificate certificate4 = new Certificate(4L,
            "VIP",
            "VIP level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-05T07:37:14.974"),
            LocalDateTime.parse("2023-01-05T07:37:14.974"));

    public Certificate certificate5 = new Certificate(5L,
            "base",
            "base level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-06T07:37:14.974"),
            LocalDateTime.parse("2023-01-06T07:37:14.974"));

    public Certificate certificate6 = new Certificate(6L,
            "premium",
            "premium level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-07T07:37:14.974"),
            LocalDateTime.parse("2023-01-07T07:37:14.974"));

    public Certificate certificate7 = new Certificate(7L,
            "gold",
            "gold level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-08T07:37:14.974"),
            LocalDateTime.parse("2023-01-08T07:37:14.974"));

    public Certificate certificate8 = new Certificate(8L,
            "platinum",
            "platinum level gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-09T07:37:14.974"),
            LocalDateTime.parse("2023-01-09T07:37:14.974"));

    public Certificate certificate9 = new Certificate(9L,
            "New Year edition",
            "New Year edition gift certificate",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-01T07:37:14.974"),
            LocalDateTime.parse("2023-01-01T07:37:14.974"));

    public Certificate certificate10 = new Certificate(10L,
            "some certificate",
            "some certificate for some days",
            new BigDecimal("1099.99"),
            120,
            LocalDateTime.parse("2023-01-10T07:37:14.974"),
            LocalDateTime.parse("2023-01-10T07:37:14.974"));
}

