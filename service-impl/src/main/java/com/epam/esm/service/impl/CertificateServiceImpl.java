package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.CertificateRepository;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.CertificateService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Set;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class CertificateServiceImpl extends AbstractService<CertificateDto, Long> implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final DtoConverter<Certificate, CertificateDto> converter;
    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> tagConverter;

    @Override
    public List<CertificateDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        List<Certificate> certificates = certificateRepository.findAll(fields, pageRequest);
        return converter.toDto(certificates);
    }

    @Override
    public List<CertificateDto> findAllByTagId(LinkedMultiValueMap<String, String> fields,
                                               PageDto pageDto,
                                               Long id) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        List<Certificate> certificates = certificateRepository.findAllCertificatesByTagId(fields, pageRequest, id);
        return converter.toDto(certificates);
    }

    @Override
    public List<CertificateDto> findAllByTagName(LinkedMultiValueMap<String, String> fields,
                                                 PageDto pageDto,
                                                 String tagName) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        List<Certificate> allByTag = certificateRepository.findAllCertificatesByTagName(fields, pageRequest, tagName);
        return converter.toDto(allByTag);
    }

    @Override
    public CertificateDto findById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        return converter.toDto(certificate);
    }


    @Override
    public CertificateDto create(CertificateDto certificateDto) {
        Certificate certificate = converter.toEntity(certificateDto);
        certificateRepository.save(certificate);
        return converter.toDto(certificate);
    }

    @Override
    public CertificateDto deleteById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        certificateRepository.delete(certificate);
        return converter.toDto(certificate);
    }

    @Override
    public CertificateDto update(Long id, CertificateDto certificateDto) {
        if (!isEqualsIds(certificateDto.getId(), id)) {
            throw new InvalidResourcePropertyException(CERTIFICATE_ID_NOT_MAPPED, id, ErrorCodes.INVALID_ID_PROPERTY);
        }
        Certificate sourceCertificate = converter.toEntity(findById(id));
        Certificate certificateToUpdate = converter.toEntity(certificateDto);
        Set<Tag> tagsToUpdate = Set.copyOf(certificateToUpdate.getTags());
        certificateToUpdate.mergeTags(sourceCertificate.getTags());
        Certificate updated = certificateRepository.update(certificateToUpdate, id);
        tagsToUpdate.forEach(tag -> tagRepository.update(tag, tag.getId()));
        return converter.toDto(updated);
    }
}
