package com.epam.esm.domain.payload;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.TIME_PATTERN;

@Data
public class CertificateFilterDto {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer duration;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime lastUpdateDate;

    private String nameContaining = "";

    private String descriptionContaining = "";
}
