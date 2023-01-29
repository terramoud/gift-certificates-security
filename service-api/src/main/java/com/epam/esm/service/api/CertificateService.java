package com.epam.esm.service.api;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface CertificateService {

    List<CertificateDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto);

    List<CertificateDto> findAllByTagId(LinkedMultiValueMap<String, String> fields, PageDto pageDto, Long tagId);

    List<CertificateDto> findAllByTagName(LinkedMultiValueMap<String, String> fields, PageDto pageDto, String tagName);

    CertificateDto findById(Long id);

    CertificateDto create(CertificateDto certificateDto);

    CertificateDto update(Long id, CertificateDto certificateDto);

    CertificateDto deleteById(Long certificateId);
}
