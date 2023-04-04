package com.epam.esm.domain.converter.impl;


import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.entity.Tag;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Tag} and {@link TagDto} objects.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Component
public class TagDtoConverter implements DtoConverter<Tag, TagDto> {

    @Override
    public Tag toEntity(TagDto dto) {
        return new Tag(
                dto.getId(),
                dto.getName()
        );
    }

    @Override
    public TagDto toDto(Tag tag) {
        return new TagDto(
                tag.getId(),
                tag.getName()
        );
    }
}

