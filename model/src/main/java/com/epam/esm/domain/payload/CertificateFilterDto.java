package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Certificate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.TIME_PATTERN;

/**
 * A data transfer object for filtering {@link Certificate}.
 * Contains fields for filtering by name, description, price,
 * duration, and creation/update dates,
 * as well as fields for filtering by name and
 * description substring matching.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
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

    /**
     * A substring to match the name field of certificates against.
     * Only certificates whose names contain this substring will
     * be returned. Defaults to an empty string, meaning no filtering
     * by name will be performed.
     */
    @Builder.Default
    private String nameContaining = "";

    /**
     * A substring to match the description field of certificates
     * against. Only certificates whose descriptions contain this
     * substring will be returned. Defaults to an empty string,
     * meaning no filtering by description will be performed.
     */
    @Builder.Default
    private String descriptionContaining = "";
}
