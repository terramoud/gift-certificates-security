package com.epam.esm.service.api;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.CertificateFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CertificateService extends BaseService<CertificateDto, Long> {

    List<CertificateDto> findAll(CertificateFilterDto filter, Pageable pageable);

    List<CertificateDto> findAllByTagId(Long tagId, CertificateFilterDto filter, Pageable pageable);

    List<CertificateDto> findAllByTagName(String tagName, CertificateFilterDto filter, Pageable pageable);
}
