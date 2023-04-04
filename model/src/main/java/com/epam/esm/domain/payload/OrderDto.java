package com.epam.esm.domain.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.*;

/**
 * The {@code OrderDto} class represents a DTO for an order entity.
 * <p>
 * It contains fields related to the order's properties, including
 * its ID, cost, creation date, user, and gift certificate.
 * The class also extends the {@link RepresentationModel} class
 * to support the creation of self-described RESTful applications.
 *
 * </p>
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends RepresentationModel<OrderDto> {

    @Null(message = ORDER_ON_CREATE_VIOLATION)
    private Long id;

    private BigDecimal cost;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    @NotNull(message = USER_NULL)
    private UserDto user;

    @NotNull(message = CERTIFICATE_NULL)
    private CertificateDto certificate;
}
