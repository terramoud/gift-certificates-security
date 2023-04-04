package com.epam.esm.domain.payload;

import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.OnUpdate;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import static com.epam.esm.domain.validation.ValidationConstants.*;

/**
 * Represents a tag DTO which is used for representing tag
 * data in the REST API.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagDto extends RepresentationModel<TagDto> {

    @Null(message = TAG_ON_CREATE_VIOLATION, groups = OnCreate.class)
    @NotNull(message = TAG_ID_NULL, groups = OnUpdate.class)
    @Positive(message = TAG_INVALID_ID, groups = OnUpdate.class)
    private Long id;

    @NotNull(message = TAG_NAME_NULL)
    @Pattern(regexp = ENTITY_NAME_REGEXP, message = TAG_INVALID_NAME)
    private String name;
}
