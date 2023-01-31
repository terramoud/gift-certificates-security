package com.epam.esm.service.api;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface CertificateService extends BaseService<CertificateDto, Long> {

    List<CertificateDto> findAllByTagId(LinkedMultiValueMap<String, String> fields,
                                        PageDto pageDto,
                                        Long tagId);

    List<CertificateDto> findAllByTagName(LinkedMultiValueMap<String, String> fields,
                                          PageDto pageDto,
                                          String tagName);
}
