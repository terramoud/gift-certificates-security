package com.epam.esm.domain.converter;

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
     * Converts source entity to Dto
     *
     * @param entity source entity to convert
     * @return converted dto entity
     */
    D toDto(S entity);
}
