package com.epam.esm.domain.payload;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagDto extends RepresentationModel<TagDto> {

    private Long id;

    private String name;
}
