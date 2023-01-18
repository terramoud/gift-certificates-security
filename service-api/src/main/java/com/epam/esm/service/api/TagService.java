package com.epam.esm.service.api;

import com.epam.esm.domain.entity.Tag;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface TagService {

    List<Tag> getAllTags(LinkedMultiValueMap<String, String> fields, int size, int page);


    List<Tag> getAllTagsByCertificateId(LinkedMultiValueMap<String, String> fields, int size, int page, Long certificateId);

    List<Tag> getAllTagsByCertificateName(LinkedMultiValueMap<String, String> fields, int size, int page, String certificateName);

    Tag getTagById(Long tagId);

    Tag addTag(Tag tag);

    Tag updateTagById(Long tagId, Tag tag);

    Tag deleteTagById(Long tagId);
}
