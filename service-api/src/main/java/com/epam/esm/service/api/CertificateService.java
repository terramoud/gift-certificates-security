package com.epam.esm.service.api;

import com.epam.esm.domain.entity.Certificate;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface CertificateService {

    List<Certificate> getAllCertificates(LinkedMultiValueMap<String, String> fields, int size, int page);

    List<Certificate> getAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields, int size, int page, Long tagId);

    List<Certificate> getAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields, int size, int page, String tagName);

    Certificate getCertificateById(Long id);

    Certificate addCertificate(Certificate certificate);

    Certificate updateCertificateById(Long id, Certificate certificate);

    Certificate deleteCertificateById(Long certificateId);
}
