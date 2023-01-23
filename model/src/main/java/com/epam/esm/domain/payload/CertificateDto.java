package com.epam.esm.domain.payload;

import com.epam.esm.domain.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class CertificateDto {
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

    @JsonIgnore
    private Set<Tag> tags;

    public CertificateDto(Long id, String name, String description, BigDecimal price, Integer duration, LocalDateTime createDate, LocalDateTime lastUpdateDate, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }
}
