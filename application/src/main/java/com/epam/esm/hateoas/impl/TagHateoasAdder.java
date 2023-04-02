package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.TagController;
import com.epam.esm.domain.payload.CertificateFilterDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.payload.TagFilterDto;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagHateoasAdder implements HateoasAdder<TagDto> {

    private static final Class<TagController> CONTROLLER = TagController.class;

    @Override
    public void addLinks(TagDto tagDto) {
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getTagById(tagDto.getId()))
                .withSelfRel());
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .deleteTagById(tagDto.getId()))
                .withRel(DELETE));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .updateTagById(tagDto.getId(), tagDto))
                .withRel(UPDATE));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .addTag(tagDto))
                .withRel(CREATE));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getMostPopularTag())
                .withRel("most-popular-tag"));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getAllTags(new TagFilterDto(), Pageable.ofSize(20)))
                .withRel("tags"));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getGiftCertificatesByTagId(tagDto.getId(), new CertificateFilterDto(), Pageable.ofSize(20)))
                .withRel("gift-certificates-by-tag-id"));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getGiftCertificatesByTagName(tagDto.getName(), new CertificateFilterDto(), Pageable.ofSize(20)))
                .withRel("gift-certificates-by-tag-name"));
    }
}
