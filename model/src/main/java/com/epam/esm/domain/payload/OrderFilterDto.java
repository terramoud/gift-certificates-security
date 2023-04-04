package com.epam.esm.domain.payload;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.TIME_PATTERN;

/**
 * The {@code OrderFilterDto} class represents a filter object for searching orders.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
public class OrderFilterDto {

    private BigDecimal cost;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    private UserFilterDto user = new UserFilterDto();

    private CertificateFilterDto certificate = new CertificateFilterDto();
}
