package com.epam.esm.controller;

import com.epam.esm.domain.payload.*;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.OnUpdate;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.security.annotations.AdminWritePermission;
import com.epam.esm.security.annotations.UserReadPermission;
import com.epam.esm.service.api.CertificateService;
import com.epam.esm.service.api.TagService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@RestController
@RequestMapping("api/v1/tags")
@AllArgsConstructor
@Validated
public class TagController {

    private final CertificateService certificateService;
    private final TagService tagService;
    private final HateoasAdder<TagDto> hateoasAdder;
    private final HateoasAdder<CertificateDto> certificateHateoasAdder;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags(TagFilterDto tagFilterDto,
                                                   Pageable pageable) {
        List<TagDto> tagDtos = tagService.findAll(tagFilterDto, pageable);
        hateoasAdder.addLinks(tagDtos);
        return new ResponseEntity<>(tagDtos, HttpStatus.OK);
    }

    @GetMapping("/{tag-id}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagId(
            @PathVariable("tag-id") Long tagId,
            CertificateFilterDto certificateFilterDto,
            Pageable pageable) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagId(tagId, certificateFilterDto, pageable);
        certificateHateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/name/{tag-name}/gift-certificates")
    public ResponseEntity<List<CertificateDto>> getGiftCertificatesByTagName(
            @PathVariable("tag-name") String tagName,
            CertificateFilterDto certificateFilterDto,
            Pageable pageable) {
        List<CertificateDto> certificateDtos =
                certificateService.findAllByTagName(tagName, certificateFilterDto, pageable);
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
    @AdminWritePermission
    @Validated(OnCreate.class)
    public ResponseEntity<TagDto> addTag(@RequestBody @Valid TagDto tagDto) {
        TagDto addedTagDto = tagService.create(tagDto);
        hateoasAdder.addLinks(addedTagDto);
        return new ResponseEntity<>(addedTagDto, HttpStatus.CREATED);
    }

    @PutMapping("/{tag-id}")
    @AdminWritePermission
    @Validated({OnUpdate.class})
    public ResponseEntity<TagDto> updateTagById(
            @PathVariable("tag-id") @Positive(message = TAG_INVALID_ID) Long tagId,
            @RequestBody @Valid TagDto tagDto) {
        TagDto updatedTagDto = tagService.update(tagId, tagDto);
        hateoasAdder.addLinks(updatedTagDto);
        return new ResponseEntity<>(updatedTagDto, HttpStatus.OK);
    }

    @DeleteMapping("/{tag-id}")
    @AdminWritePermission
    public ResponseEntity<TagDto> deleteTagById(
            @PathVariable("tag-id") @Positive(message = TAG_INVALID_ID) Long tagId) {
        TagDto tagDto = tagService.deleteById(tagId);
        hateoasAdder.addLinks(tagDto);
        return new ResponseEntity<>(tagDto, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/popular")
    @UserReadPermission
    public ResponseEntity<TagDto> getMostPopularTag() {
        TagDto tagDto = tagService.findMostPopularTagOfUserWithHighestCostOfAllOrders();
        hateoasAdder.addLinks(tagDto);
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }
}
