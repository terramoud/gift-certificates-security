package com.epam.esm.domain.converter;


import com.epam.esm.domain.dto.TagDto;
import com.epam.esm.domain.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagDtoConverter {

    public Tag toTag(TagDto dto) {
        return new Tag(dto.getId(), dto.getName());
    }

    public TagDto toDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }
}

