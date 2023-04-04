package com.epam.esm.service.impl;


import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.payload.TagFilterDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.TagService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

/**
 * Implementation of {@link TagService} interface that
 * uses {@link TagRepository} to perform CRUD operations
 * on {@link Tag} entities.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class TagServiceImpl extends AbstractService<TagDto, Long> implements TagService {

    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> converter;
    private final DtoConverter<Certificate, CertificateDto> certificateConverter;

    @Override
    public List<TagDto> findAll(TagFilterDto tagFilterDto, Pageable pageable) {
        List<Tag> tags = tagRepository.findAll(tagFilterDto, pageable);
        return converter.toDto(tags);
    }

    @Override
    public TagDto findById(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        return converter.toDto(tag);
    }

    @Override
    public TagDto create(TagDto tagDto) {
        Tag tag = converter.toEntity(tagDto);
        Tag savedTag = tagRepository.save(tag);
        return converter.toDto(savedTag);
    }

    @Override
    public TagDto update(Long id, TagDto tagDto) {
        if (!isEqualsIds(tagDto.getId(), id)) {
            throw new InvalidResourcePropertyException(TAG_ID_NOT_MAPPED, tagDto.getId(), INVALID_ID_PROPERTY);
        }
        if (tagRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(TAG_NOT_FOUND, id, ErrorCodes.NOT_FOUND_TAG_RESOURCE);
        }
        Tag tag = converter.toEntity(tagDto);
        Tag updated = tagRepository.save(tag);
        return converter.toDto(updated);
    }

    @Override
    public TagDto deleteById(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        tagRepository.delete(tag);
        return converter.toDto(tag);
    }

    @Override
    public TagDto findMostPopularTagOfUserWithHighestCostOfAllOrders() {
        Tag tag = tagRepository.findMostPopularTagOfUserWithHighestCostOfAllOrders()
                .orElseThrow(() -> new MostPopularTagNotFoundException(
                        TAG_NOT_FOUND, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        return converter.toDto(tag);
    }
}
