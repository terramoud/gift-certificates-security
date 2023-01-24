package com.epam.esm.service.api;

import com.epam.esm.domain.entity.Tag;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface TagService {

    List<Tag> getAllTags(LinkedMultiValueMap<String, String> fields, int size, int page);

    Tag getTagById(Long tagId);

    Tag addTag(Tag tag);

    Tag updateTagById(Long tagId, Tag tag);

    Tag deleteTagById(Long tagId);
}
