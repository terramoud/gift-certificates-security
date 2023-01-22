package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface TagRepository extends BaseRepository<Tag, Long> {
    List<Tag> findAllTagsByCertificateId(LinkedMultiValueMap<String, String> fields,
                                         Pageable pageable,
                                         Long certificateId);

    List<Tag> findAllTagsByCertificateName(LinkedMultiValueMap<String, String> fields,
                                           Pageable pageable,
                                           String certificateName);
}
