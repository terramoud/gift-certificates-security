package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.validation.CertificateValidator;
import com.epam.esm.domain.validation.TagValidator;
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
import java.util.List;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private static final String CERTIFICATE_NOT_FOUND = "certificate.not.found";
    private static final String CERTIFICATE_ERROR_INVALID_ID = "certificate.error.invalid.id";
    private final CertificateRepository certificateRepository;
    private final CertificateValidator validator;
    private final TagValidator tagValidator;
    private final TagRepository tagRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  CertificateValidator certificateValidator,
                                  TagValidator tagValidator,
                                  TagRepository tagRepository) {
        this.certificateRepository = certificateRepository;
        this.validator = certificateValidator;
        this.tagValidator = tagValidator;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Certificate> getAllCertificates(LinkedMultiValueMap<String, String> fields,
                                                int size,
                                                int page) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAll(fields, pageRequest);
    }

    @Override
    public List<Certificate> getAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields,
                                                       int size,
                                                       int page,
                                                       Long tagId) {
        if (!tagValidator.validateId(tagId)) throw new InvalidResourcePropertyException(
                "tag.error.invalid.id", tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        if (size < 1) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAllCertificatesByTagId(fields, pageRequest, tagId);
    }

    @Override
    public List<Certificate> getAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields,
                                                         int size,
                                                         int page,
                                                         String tagName) {
        if (!tagValidator.validateName(tagName)) throw new InvalidResourceNameException(
                    "tag.error.invalid.name", tagName, ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        if (size < 1) throw new InvalidPaginationParameterException(
                "size", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "page", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return certificateRepository.findAllCertificatesByTagName(fields, pageRequest, tagName);
    }

    @Override
    public Certificate getCertificateById(Long id) {
        if (!validator.validateId(id)) throw new InvalidResourcePropertyException(
                CERTIFICATE_ERROR_INVALID_ID, id, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        return certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
    }

    @Override
    public Certificate addCertificate(Certificate certificate) {
        String name = certificate.getName();
        String description = certificate.getDescription();
        BigDecimal price = certificate.getPrice();
        Integer duration = certificate.getDuration();
        if (!validator.validateName(name)) throw new InvalidResourceNameException(
                    "certificate.error.invalid.name", name, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validateBigTextField(description)) throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.description", description, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validatePrice(price)) throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.price", price + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validateDuration(duration)) throw new InvalidResourcePropertyException(
                    "certificate.error.invalid.duration", duration + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate updateCertificateById(Long id, Certificate certificate) {
        if (!validator.validateId(id)) throw new InvalidResourcePropertyException(
                CERTIFICATE_ERROR_INVALID_ID, id, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        if (!validator.validateId(id) || !certificate.getId().equals(id)) throw new InvalidResourcePropertyException(
                CERTIFICATE_ERROR_INVALID_ID, id, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        Certificate certificateToUpdate = getCertificateById(id);
        if (certificate.getName() != null) certificateToUpdate.setName(certificate.getName());
        if (certificate.getDescription() != null) certificateToUpdate.setDescription(certificate.getDescription());
        if (certificate.getPrice() != null) certificateToUpdate.setPrice(certificate.getPrice());
        if (certificate.getDuration() != null) certificateToUpdate.setDuration(certificate.getDuration());
        if (certificate.getTags() != null) certificateToUpdate.addTags(certificate.getTags());
        String name = certificateToUpdate.getName();
        String description = certificateToUpdate.getDescription();
        BigDecimal price = certificateToUpdate.getPrice();
        Integer duration = certificateToUpdate.getDuration();
        if (!validator.validateName(name)) throw new InvalidResourceNameException(
                "certificate.error.invalid.name", name, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validateName(description)) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.description", description, ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validatePrice(price)) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.price", price + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        if (!validator.validateDuration(duration)) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.duration", duration + "", ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
        certificateToUpdate.getTags().forEach(tag -> {
            if (!tagValidator.validate(tag)) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.joined.tag", tag.getId(), ErrorCodes.INVALID_CERTIFICATE_PROPERTY);
            tagRepository.findById(tag.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "tag.error.update.not.found", tag.getId(), ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        });
        return certificateRepository.update(certificateToUpdate, id);
    }

    @Override
    public Certificate deleteCertificateById(Long id) {
        Certificate certificate = getCertificateById(id);
        certificateRepository.delete(certificate);
        return certificate;
    }
}
