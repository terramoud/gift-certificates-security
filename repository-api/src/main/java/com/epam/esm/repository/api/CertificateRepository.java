package com.epam.esm.repository.api;



import com.epam.esm.domain.entity.Certificate;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface CertificateRepository extends BaseRepository<Certificate, Long> {
    List<Certificate> findAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields, Pageable pageable, Long tagId);

    List<Certificate> findAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields, Pageable pageable, String tagName);
}
