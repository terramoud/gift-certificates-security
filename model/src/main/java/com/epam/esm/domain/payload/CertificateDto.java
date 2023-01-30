package com.epam.esm.domain.payload;

import com.epam.esm.domain.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CertificateDto extends RepresentationModel<CertificateDto> {

    @Null(message = CERTIFICATE_ON_CREATE_VIOLATION, groups = OnCreate.class)
    @NotNull(message = CERTIFICATE_ID_NULL)
    @Positive(message = CERTIFICATE_INVALID_ID)
    private Long id;

    @NotNull(message = CERTIFICATE_NAME_NULL)
    @Pattern(regexp = ENTITY_NAME_REGEXP, message = CERTIFICATE_INVALID_NAME)
    private String name;

    @NotNull(message = CERTIFICATE_DESCRIPTION_NULL)
    @Size(min = 3, max = 500, message = CERTIFICATE_INVALID_DESCRIPTION)
    private String description;

    @NotNull(message = CERTIFICATE_PRICE_NULL)
    @DecimalMin(value = "0.1", inclusive = false, message = CERTIFICATE_INVALID_PRICE)
    @Digits(integer = 9, fraction = 2, message = CERTIFICATE_INVALID_PRICE)
    private BigDecimal price;

    @NotNull(message = CERTIFICATE_DURATION_NULL)
    @Positive(message = CERTIFICATE_INVALID_DURATION)
    private Integer duration;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createDate;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime lastUpdateDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<@Valid TagDto> tags;
}
