package com.epam.esm.controller;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.CertificateService;
import com.epam.esm.service.api.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tags")
@AllArgsConstructor
public class TagController {

    private final CertificateService certificateService;
    private final TagService tagService;
    private final HateoasAdder<TagDto> hateoasAdder;
    private final HateoasAdder<CertificateDto> certificateHateoasAdder;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size) {
        List<TagDto> tagDtos = tagService.findAll(allRequestParameters, new PageDto(page, size));
        hateoasAdder.addLinks(tagDtos);
        return new ResponseEntity<>(tagDtos, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagId(
            @PathVariable("tag-id") Long tagId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagId(allRequestParameters, new PageDto(page, size), tagId);
        certificateHateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/name/{tag-name}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagName(
            @PathVariable("tag-name") String tagName,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagName(allRequestParameters, new PageDto(page, size), tagName);
        certificateHateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable("tag-id") Long tagId) {
        TagDto tagDto = tagService.findById(tagId);
        hateoasAdder.addLinks(tagService.findById(tagId));
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TagDto> addTag(@RequestBody TagDto tagDto) {
        TagDto addedTagDto = tagService.create(tagDto);
        hateoasAdder.addLinks(addedTagDto);
        return new ResponseEntity<>(addedTagDto, HttpStatus.CREATED);
    }

    @PutMapping("/{tag-id}")
    public ResponseEntity<TagDto> updateTagById(@PathVariable("tag-id") Long tagId,
                                                @RequestBody TagDto tagDto) {
        TagDto updatedTagDto = tagService.update(tagId, tagDto);
        hateoasAdder.addLinks(updatedTagDto);
        return new ResponseEntity<>(updatedTagDto, HttpStatus.OK);
    }

    @DeleteMapping("/{tag-id}")
    public ResponseEntity<TagDto> deleteTagById(@PathVariable("tag-id") Long tagId) {
        TagDto tagDto = tagService.deleteById(tagId);
        hateoasAdder.addLinks(tagDto);
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }
}
