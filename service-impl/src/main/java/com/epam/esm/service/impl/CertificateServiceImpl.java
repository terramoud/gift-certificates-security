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
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class CertificateServiceImpl extends AbstractService<CertificateDto, Long> implements CertificateService {

    private static final String CERTIFICATE_NOT_FOUND = "certificate.not.found";
    private static final String CERTIFICATE_ID_NOT_MAPPED = "certificate.id.not.mapped";
    private static final String TAG_NAME_REGEXP = "^[\\p{L}][\\p{L} \\-']{0,30}[\\p{L}]$";

    private final CertificateRepository certificateRepository;
    private final DtoConverter<Certificate, CertificateDto> converter;
    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> tagConverter;

    @Override
    public List<CertificateDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(certificateRepository.findAll(fields, pageRequest));
    }

    @Override
    public List<CertificateDto> findAllByTagId(LinkedMultiValueMap<String, String> fields,
                                               PageDto pageDto,
                                               Long id) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(certificateRepository.findAllCertificatesByTagId(fields, pageRequest, id));
    }

    @Override
    public List<CertificateDto> findAllByTagName(LinkedMultiValueMap<String, String> fields,
                                                 PageDto pageDto,
                                                 String tagName) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(certificateRepository.findAllCertificatesByTagName(fields, pageRequest, tagName));
    }

    @Override
    public CertificateDto findById(Long id) {
        return converter.toDto(certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE)));
    }


    @Override
    public CertificateDto create(CertificateDto certificateDto) {
        return converter.toDto(certificateRepository.save(converter.toEntity(certificateDto)));
    }

    @Override
    public CertificateDto deleteById(Long id) {
        CertificateDto certificateDto = findById(id);
        certificateRepository.delete(converter.toEntity(certificateDto));
        return certificateDto;
    }

    @Override
    public CertificateDto update(Long id, CertificateDto certificateDto) {
        if (!isEqualsIds(certificateDto.getId(), id)) {
            throw new InvalidResourcePropertyException(CERTIFICATE_ID_NOT_MAPPED, id, ErrorCodes.INVALID_ID_PROPERTY);
        }
        Certificate certificateToUpdate = converter.toEntity(findById(id));
        Set<Tag> tagsToUpdate = tagConverter.toEntity(certificateDto.getTags());
        certificateToUpdate.addTags(getUniqueTagsForThisCertificate(certificateToUpdate.getTags(), tagsToUpdate));
        updateTags(tagsToUpdate);
        return converter.toDto(certificateRepository.update(certificateToUpdate, id));
    }

    private Set<Tag> getUniqueTagsForThisCertificate(Set<Tag> sourceTags, Set<Tag> tagsToUpdate) {
        Set<Long> sourceTagsIds = sourceTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        return tagsToUpdate.stream()
                .filter(tag -> !sourceTagsIds.contains(tag.getId()))
                .collect(Collectors.toSet());
    }

    private void updateTags(Set<Tag> tagsToUpdate) {
        tagsToUpdate.forEach(tag -> tagRepository.update(tag, tag.getId()));
    }
}
