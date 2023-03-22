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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class TagServiceImpl extends AbstractService<TagDto, Long> implements TagService {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final Map<String, Sort> sortMap = Map.of(
            "+id", Sort.by(Direction.ASC, ID),
            "-id", Sort.by(Direction.DESC, ID),
            "+name", Sort.by(Direction.ASC, NAME),
            "-name", Sort.by(Direction.DESC, NAME)
    );

    private static final Map<String, Function<String, Specification<Tag>>> filterMap = Map.of(
            NAME, filterValue -> (root, query, cb) -> cb.equal(root.get(NAME), filterValue));

    private static final Map<String, Function<String, Specification<Tag>>> searchMap = Map.of(
            NAME, searchValue -> (r, query, cb) -> cb.like(r.get(NAME), createLikeQuery(searchValue)));

    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> converter;
    private final DtoConverter<Certificate, CertificateDto> certificateConverter;

    @Override
    public List<TagDto> findAll(LinkedMultiValueMap<String, String> requestParams, PageDto pageDto) {
        List<Tag> tags = findAllAbstract(requestParams, pageDto, tagRepository, sortMap, filterMap, searchMap);
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
