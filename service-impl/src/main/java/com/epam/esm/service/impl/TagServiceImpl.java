package com.epam.esm.service.impl;


import com.epam.esm.domain.entity.Tag;
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

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAllTags(LinkedMultiValueMap<String, String> fields, int size, int page) {
        if (size < 0) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return tagRepository.findAll(fields, pageRequest);
    }

    @Override
    public List<Tag> getAllTagsByCertificateId(LinkedMultiValueMap<String, String> fields,
                                               int size,
                                               int page,
                                               Long certificateId) {
        if (certificateId == null || certificateId < 1) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.id", certificateId, ErrorCodes.INVALID_CERTIFICATE_ID_PROPERTY);
        if (size < 0) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return tagRepository.findAllTagsByCertificateId(fields, pageRequest, certificateId);
    }

    @Override
    public List<Tag> getAllTagsByCertificateName(LinkedMultiValueMap<String, String> fields,
                                                 int size,
                                                 int page,
                                                 String certificateName) {
        if (certificateName == null || certificateName.isEmpty()) throw new InvalidResourcePropertyException(
                "certificate.error.invalid.name", certificateName, ErrorCodes.INVALID_CERTIFICATE_NAME_PROPERTY);
        if (size < 0) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return tagRepository.findAllTagsByCertificateName(fields, pageRequest, certificateName);
    }

    @Override
    public Tag getTagById(Long tagId) {
        if (tagId == null || tagId < 1) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
    }

    @Override
    public Tag addTag(Tag tag) {
        if (tag.getName() == null ||
                tag.getName().isEmpty() ||
                tag.getName().equals("null")) throw new InvalidResourceNameException("tag.error.invalid.name",
                    tag.getName(), ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTagById(Long tagId, Tag tag) {
        if (tagId == null || tagId < 1) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        if (tag == null ||
                tag.getId() == null ||
                tag.getId() < 1 ||
                !tag.getId().equals(tagId)) throw new InvalidResourcePropertyException(
                        "tag.error.paramId.no.equals.bodyId", tag.getId(), ErrorCodes.INVALID_TAG_ID_PROPERTY);
        Tag tagToUpdate = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        if (tag.getName() != null) tagToUpdate.setName(tag.getName());
        if (tag.getCertificates() != null) {
            System.out.println("tagToUpdate.getCertificates() = " + tagToUpdate.getCertificates());
            tagToUpdate.getCertificates().addAll(tag.getCertificates());
            System.out.println("tagToUpdate.getCertificates() after = " + tagToUpdate.getCertificates());
        }
        if (tagToUpdate.getName().isEmpty() ||
                tagToUpdate.getName().equals("null")) throw new InvalidResourceNameException(
                "tag.error.invalid.name", tag.getName(), ErrorCodes.INVALID_TAG_NAME_PROPERTY);
        return tagRepository.update(tag, tagId);
    }

    @Override
    public Tag deleteTagById(Long tagId) {
        if (tagId == null || tagId < 1) throw new InvalidResourcePropertyException(
                TAG_ERROR_INVALID_ID, tagId, ErrorCodes.INVALID_TAG_ID_PROPERTY);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TAG_NOT_FOUND, tagId, ErrorCodes.NOT_FOUND_TAG_RESOURCE));
        tagRepository.delete(tag);
        return tag;
    }
}
