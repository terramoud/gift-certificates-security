package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@Data
@AllArgsConstructor
public class PageDto {

    @Value("${pagination.defaultPage}")
    @PositiveOrZero(message = INVALID_PAGE)
    private final int page;

    @Value("${pagination.defaultSize}")
    @Positive(message = INVALID_LIMIT_SIZE)
    private final int size;
}
