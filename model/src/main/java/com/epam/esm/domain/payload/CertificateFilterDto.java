package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateFilterDto {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer duration;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime lastUpdateDate;

    @Builder.Default
    private String nameContaining = "";

    @Builder.Default
    private String descriptionContaining = "";
}
