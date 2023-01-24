package com.epam.esm.service.impl;


import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.validation.TagValidator;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    public static final String TAG_NOT_FOUND = "tag.not.found";
    public static final String TAG_ERROR_INVALID_ID = "tag.error.invalid.id";

    private final TagRepository tagRepository;
    private final TagValidator validator;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagValidator tagValidator) {
        this.tagRepository = tagRepository;
        this.validator = tagValidator;
    }

    @Override
    public List<Tag> getAllTags(LinkedMultiValueMap<String, String> fields, int size, int page) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return tagRepository.findAll(fields, pageRequest);
    }

    @Override
    public Tag getTagById(Long tagId) {
        if (validator.validateId(tagId)) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
    }

    @Override
    public Tag addTag(Tag tag) {
        if (validator.validateId(tag.getId())) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tag.getId(), ErrorCodes.INVALID_TAG_ID_PROPERTY);
        if (validator.validateName(tag.getName())) throw new InvalidResourceNameException(
                "tag.error.invalid.name", tag.getName(), ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTagById(Long tagId, Tag tag) {
        if (validator.validateId(tagId)) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        if (validator.validateId(tag.getId()) || !tag.getId().equals(tagId))
            throw new InvalidResourcePropertyException(
                        "tag.error.paramId.no.equals.bodyId", tag.getId(), ErrorCodes.INVALID_TAG_ID_PROPERTY);
        Tag tagToUpdate = getTagById(tagId);
        if (tag.getName() != null) tagToUpdate.setName(tag.getName());
        if (validator.validateName(tagToUpdate.getName())) throw new InvalidResourceNameException(
                "tag.error.invalid.name", tag.getName(), ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        return tagRepository.update(tagToUpdate, tagId);
    }

    @Override
    public Tag deleteTagById(Long tagId) {
        Tag tag = getTagById(tagId);
        tagRepository.delete(tag);
        return tag;
    }
}
