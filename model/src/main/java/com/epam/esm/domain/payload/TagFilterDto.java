package com.epam.esm.domain.payload;

import lombok.Data;

@Data
public class TagFilterDto {

    private String name;

    private String nameContaining = "";
}
