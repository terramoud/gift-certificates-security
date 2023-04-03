package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagFilterDto {

    private String name;

    @Builder.Default
    private String nameContaining = "";
}
