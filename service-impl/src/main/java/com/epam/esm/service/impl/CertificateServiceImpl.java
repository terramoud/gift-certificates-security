package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.CertificateRepository;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private static final String CERTIFICATE_NOT_FOUND = "certificate.not.found";
    private static final String CERTIFICATE_ERROR_INVALID_ID = "certificate.error.invalid.id";
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  TagRepository tagRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Override
    public List<Certificate> getAllCertificates(LinkedMultiValueMap<String, String> fields, int size, int page) {
        if (size < 0) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAll(fields, pageRequest);
    }

    @Override
    public List<Certificate> getAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields, int size, int page,
                                                       Long tagId) {
        if (tagId == null || tagId < 1) throw new InvalidResourcePropertyException(
                "tag.error.invalid.id", tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        if (size < 0) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAllCertificatesByTagId(fields, pageRequest, tagId);
    }

    @Override
    public List<Certificate> getAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields, int size, int page,
                                                         String tagName) {
        if (tagName == null || tagName.isEmpty() || tagName.equals("null"))
            throw new InvalidResourceNameException(
                    "tag.error.invalid.name", tagName, ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        if (size < 0) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAllCertificatesByTagName(fields, pageRequest, tagName);
    }

    @Override
    public Certificate getCertificateById(Long certificateId) {
        if (certificateId == null || certificateId < 1) throw new InvalidResourcePropertyException(
                CERTIFICATE_ERROR_INVALID_ID, certificateId, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, certificateId, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
    }

    @Override
    public Certificate addCertificate(Certificate certificate) {
        String name = certificate.getName();
        String description = certificate.getDescription();
        BigDecimal price = certificate.getPrice();
        Integer duration = certificate.getDuration();
        if (name == null || name.isEmpty() || name.equals("null"))
            throw new InvalidResourceNameException(
                    "certificate.error.invalid.name", name, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (description == null || description.isEmpty() || description.equals("null"))
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.description", description, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.price", price + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (duration == null || duration < 1)
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.duration", duration + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        certificate.setCreateDate(LocalDateTime.now());
        certificate.setLastUpdateDate(certificate.getCreateDate());
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate updateCertificateById(Long certificateId, Certificate certificate) {
        if (certificateId == null || certificateId < 1)
            throw new InvalidResourcePropertyException(
                    CERTIFICATE_ERROR_INVALID_ID, certificateId, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, certificateId, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        certificate.setLastUpdateDate(LocalDateTime.now());
        String name = certificate.getName();
        String description = certificate.getDescription();
        BigDecimal price = certificate.getPrice();
        Integer duration = certificate.getDuration();
        if (name == null || name.isEmpty() || name.equals("null"))
            throw new InvalidResourceNameException(
                    "certificate.error.invalid.name", name, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (description == null || description.isEmpty() || description.equals("null"))
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.description", description, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.price", price + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (duration == null || duration < 1)
            throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.duration", duration + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate deleteCertificateById(Long certificateId) {
        if (certificateId == null || certificateId < 1) throw new InvalidResourcePropertyException(
                CERTIFICATE_ERROR_INVALID_ID, certificateId, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        Certificate deletedCertificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, certificateId, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        certificateRepository.delete(certificateId);
        return deletedCertificate;
    }
}
