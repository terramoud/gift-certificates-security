package com.epam.esm.controller;

import com.epam.esm.domain.converter.CertificateDtoConverter;
import com.epam.esm.domain.converter.TagDtoConverter;
import com.epam.esm.domain.dto.CertificateDto;
import com.epam.esm.domain.dto.TagDto;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.service.api.CertificateService;
import com.epam.esm.service.api.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/tags")
public class TagController {

    private final CertificateService certificateService;
    private final TagService tagService;
    private final TagDtoConverter converter;
    private final CertificateDtoConverter certificateConverter;

    @Autowired
    public TagController(CertificateService certificateService, TagService tagService, TagDtoConverter converter, CertificateDtoConverter certificateConverter) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.converter = converter;
        this.certificateConverter = certificateConverter;
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Tag> tags = tagService.getAllTags(allRequestParameters, size, page);
        List<TagDto> tagDtoList = tags.stream()
                .map(converter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tagDtoList, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagId(
            @PathVariable("tag-id") Long tagId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Certificate> giftCertificates =
                certificateService.getAllCertificatesByTagId(allRequestParameters, size, page, tagId);
        List<CertificateDto> certificateDtoList = giftCertificates.stream()
                .map(certificateConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(certificateDtoList, HttpStatus.OK);
    }

    @GetMapping("/name/{tag-name}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagName(
            @PathVariable("tag-name") String tagName,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Certificate> giftCertificates =
                certificateService.getAllCertificatesByTagName(allRequestParameters, size, page, tagName);
        List<CertificateDto> certificateDtoList = giftCertificates.stream()
                .map(certificateConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(certificateDtoList, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable("tag-id") Long tagId) {
        Tag tag = tagService.getTagById(tagId);
        return new ResponseEntity<>(converter.toDto(tag), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TagDto> addTag(@RequestBody TagDto tagDto) {
        Tag addedTag = tagService.addTag(converter.toTag(tagDto));
        return new ResponseEntity<>(converter.toDto(addedTag), HttpStatus.CREATED);
    }

    @PutMapping("/{tag-id}")
    public ResponseEntity<TagDto> updateTagById(
            @PathVariable("tag-id") Long tagId,
            @RequestBody TagDto tagDto) {
        Tag updatedTag = tagService.updateTagById(tagId, converter.toTag(tagDto));
        return new ResponseEntity<>(converter.toDto(updatedTag), HttpStatus.OK);
    }

    @DeleteMapping("/{tag-id}")
    public ResponseEntity<TagDto> deleteTagById(@PathVariable("tag-id") Long tagId) {
        Tag tag = tagService.deleteTagById(tagId);
        return new ResponseEntity<>(converter.toDto(tag), HttpStatus.OK);
    }
}
