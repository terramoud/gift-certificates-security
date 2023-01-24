package com.epam.esm.domain.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private Long id;
    private BigDecimal cost;

    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createDate;
    private UserDto user;
    private CertificateDto certificate;
}
