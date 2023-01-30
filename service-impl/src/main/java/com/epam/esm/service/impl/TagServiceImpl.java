package com.epam.esm.service.impl;


import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.TagService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class TagServiceImpl extends AbstractService<TagDto, Long> implements TagService {

    private static final String TAG_NOT_FOUND = "tag.not.found";
    private static final String TAG_ID_NOT_MAPPED = "tag.id.not.mapped";

    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> converter;
    private final DtoConverter<Certificate, CertificateDto> certificateConverter;

    @Override
    public List<TagDto> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(tagRepository.findAll(fields, pageRequest));
    }

    @Override
    public TagDto findById(Long tagId) {
        return converter.toDto(tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE)));
    }

    @Override
    public TagDto create(TagDto tagDto) {
        return converter.toDto(tagRepository.save(converter.toEntity(tagDto)));
    }

    @Override
    public TagDto update(Long id, TagDto tagDto) {
        if (!isEqualsIds(tagDto.getId(), id)) {
            throw new InvalidResourcePropertyException(TAG_ID_NOT_MAPPED, tagDto.getId(), INVALID_ID_PROPERTY);
        }
        if (tagRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(TAG_NOT_FOUND, id, ErrorCodes.NOT_FOUND_TAG_RESOURCE);
        }
        return converter.toDto(tagRepository.update(converter.toEntity(tagDto), id));
    }

    @Override
    public TagDto deleteById(Long tagId) {
        TagDto tagDto = findById(tagId);
        tagRepository.delete(converter.toEntity(findById(tagId)));
        return tagDto;
    }
}
