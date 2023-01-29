package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class PageDto implements Serializable {
    private static final long serialVersionUID = -3319772390593018460L;

    @Value("${pagination.defaultPage}")
//    @PositiveOrZero(message = "pagination.invalid.page")
    @PositiveOrZero()
    private final int page;


    private final int size;

    @NotNull
    private String licensePlate;

}
