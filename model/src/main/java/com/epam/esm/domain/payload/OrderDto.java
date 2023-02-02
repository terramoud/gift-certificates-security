package com.epam.esm.domain.payload;

import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends RepresentationModel<OrderDto> {

    @Null(message = ORDER_ON_CREATE_VIOLATION, groups = OnCreate.class)
    @NotNull(message = ORDER_ID_NULL, groups = OnUpdate.class)
    @Positive(message = ORDER_INVALID_ID, groups = OnUpdate.class)
    private Long id;

    @NotNull(message = ORDER_COST_NULL)
    @DecimalMin(value = "0.1", inclusive = false, message = ORDER_INVALID_COST)
    @Digits(integer = 9, fraction = 2, message = ORDER_INVALID_COST)
    private BigDecimal cost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    @NotNull(message = USER_NULL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Valid UserDto user;

    @NotNull(message = CERTIFICATE_NULL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Valid CertificateDto certificate;
}
