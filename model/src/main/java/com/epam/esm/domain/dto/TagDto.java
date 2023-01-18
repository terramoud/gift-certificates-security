package com.epam.esm.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class TagDto {
    private Long id;
    private String name;

    public TagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
