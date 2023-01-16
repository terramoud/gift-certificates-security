package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface TagRepository extends BaseRepository<Tag, Long> {
    List<Tag> findAllTagsByCertificateId(MultiValueMap<String, String> fields, Pageable pageable, Long certificateId);

    List<Tag> findAllTagsByCertificateName(MultiValueMap<String, String> fields, Pageable pageable, String tagName);

    List<Tag> findAllTagsByQuery(MultiValueMap<String, String> fields, Pageable pageable, String searchQuery);
}
