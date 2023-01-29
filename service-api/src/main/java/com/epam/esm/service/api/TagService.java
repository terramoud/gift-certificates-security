package com.epam.esm.service.api;

import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface TagService {

    List<TagDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto);

    TagDto findById(Long tagId);

    TagDto create(TagDto tagDto);

    TagDto update(Long tagId, TagDto tag);

    TagDto deleteById(Long tagId);
}
