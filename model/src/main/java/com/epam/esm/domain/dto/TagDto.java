package com.epam.esm.domain.dto;

import com.epam.esm.domain.entity.Certificate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(value= {"tags"})
public class TagDto {

    private Long id;

    private String name;

    @JsonIgnore
    private Set<Certificate> certificates;
}
