package com.epam.esm.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code TagFilterDto} class represents a data transfer object
 * for filtering {@code Tag} entities.
 * It contains fields used for filtering by name and name containing.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagFilterDto {

    private String name;

    /**
     * A substring to match the name field of tags against.
     * Only tags whose names contain this substring will
     * be returned. Defaults to an empty string, meaning no filtering
     * by name will be performed.
     */
    @Builder.Default
    private String nameContaining = "";
}
