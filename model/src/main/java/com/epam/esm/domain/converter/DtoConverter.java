package com.epam.esm.domain.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The interface DtoConverter
 *
 * @param <S> SourceEntity type
 * @param <D> DtoEntity type
 */
public interface DtoConverter<S, D> {
    /**
     * Converts Dto to source entity
     *
     * @param dto Dto entity to convert
     * @return converted source entity
     */
    S toEntity(D dto);

    /**
     * Converts list Dtos to list source entities
     *
     * @param dtos list source entities to convert
     * @return converted list dto entities
     */
    default List<S> toEntity(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts list Dtos to list source entities
     *
     * @param dtos list source entities to convert
     * @return converted list dto entities
     */
    default Set<S> toEntity(Set<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    /**
     * Converts source entity to Dto
     *
     * @param entity source entity to convert
     * @return converted dto entity
     */
    D toDto(S entity);

    /**
     * Converts list source entities to Dto
     *
     * @param entities list source entities to convert
     * @return converted list dto entities
     */
    default List<D> toDto(List<S> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts list source entities to Dto
     *
     * @param entities list source entities to convert
     * @return converted list dto entities
     */
    default Set<D> toDto(Set<S> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
