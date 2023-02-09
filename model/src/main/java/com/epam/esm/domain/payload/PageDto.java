package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@Data
@AllArgsConstructor
public class PageDto {

    @PositiveOrZero(message = INVALID_PAGE)
    private final int page;

    @Positive(message = INVALID_SIZE)
    private final int size;
}
