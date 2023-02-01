package com.epam.esm.controller;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.CertificateService;
import com.epam.esm.service.api.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@RestController
@RequestMapping("api/v1/tags")
@AllArgsConstructor
@Validated
public class TagController {

    private static final String PAGE_DEFAULT = "0";
    private static final String SIZE_DEFAULT = "5";
    private final CertificateService certificateService;
    private final TagService tagService;
    private final HateoasAdder<TagDto> hateoasAdder;
    private final HateoasAdder<CertificateDto> certificateHateoasAdder;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = PAGE_DEFAULT)
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = SIZE_DEFAULT)
            @Positive(message = INVALID_SIZE) int size) {
        List<TagDto> tagDtos = tagService.findAll(allRequestParameters, new PageDto(page, size));
        hateoasAdder.addLinks(tagDtos);
        return new ResponseEntity<>(tagDtos, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagId(
            @PathVariable("tag-id") Long tagId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = PAGE_DEFAULT)
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = SIZE_DEFAULT)
            @Positive(message = INVALID_SIZE) int size) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagId(allRequestParameters, new PageDto(page, size), tagId);
        certificateHateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/name/{tag-name}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagName(
            @PathVariable("tag-name") String tagName,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = PAGE_DEFAULT)
            @PositiveOrZero(message = INVALID_PAGE) int page,
            @RequestParam(value = "size", defaultValue = SIZE_DEFAULT)
            @Positive(message = INVALID_SIZE) int size) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagName(allRequestParameters, new PageDto(page, size), tagName);
        certificateHateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}")
    public ResponseEntity<TagDto> getTagById(
            @PathVariable("tag-id") @Positive(message = TAG_INVALID_ID) Long tagId) {
        TagDto tagDto = tagService.findById(tagId);
        hateoasAdder.addLinks(tagDto);
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<TagDto> addTag(@RequestBody @Valid TagDto tagDto) {
        TagDto addedTagDto = tagService.create(tagDto);
        hateoasAdder.addLinks(addedTagDto);
        return new ResponseEntity<>(addedTagDto, HttpStatus.CREATED);
    }

    @PutMapping("/{tag-id}")
    public ResponseEntity<TagDto> updateTagById(
            @PathVariable("tag-id") @Positive(message = TAG_INVALID_ID) Long tagId,
            @RequestBody @Valid TagDto tagDto) {
        TagDto updatedTagDto = tagService.update(tagId, tagDto);
        hateoasAdder.addLinks(updatedTagDto);
        return new ResponseEntity<>(updatedTagDto, HttpStatus.OK);
    }

    @DeleteMapping("/{tag-id}")
    public ResponseEntity<TagDto> deleteTagById(
            @PathVariable("tag-id") @Positive(message = TAG_INVALID_ID) Long tagId) {
        TagDto tagDto = tagService.deleteById(tagId);
        hateoasAdder.addLinks(tagDto);
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }
}
