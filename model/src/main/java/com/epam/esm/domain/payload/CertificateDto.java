package com.epam.esm.domain.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CertificateDto extends RepresentationModel<CertificateDto> {
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;

    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createDate;

    @JsonFormat(pattern = PATTERN)
    private LocalDateTime lastUpdateDate;

    private Set<TagDto> tags;
}
