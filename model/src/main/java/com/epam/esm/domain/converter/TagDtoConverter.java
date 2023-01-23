package com.epam.esm.domain.converter;


import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagDtoConverter {

    public Tag toTag(TagDto dto) {
        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        tag.setCertificates(dto.getCertificates());
        return tag;
    }

    public TagDto toDto(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        tagDto.setCertificates(tag.getCertificates());
        return tagDto;
    }
}

