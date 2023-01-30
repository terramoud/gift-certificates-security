package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.TagController;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagHateoasAdder implements HateoasAdder<TagDto> {
    private static final Class<TagController> CONTROLLER = TagController.class;
    private static final LinkedMultiValueMap<String, String> REQUEST_PARAMS = new LinkedMultiValueMap<>(
            Map.of("sort", List.of("+id"),
                    "search", List.of(""))
    );

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
                .getAllTags(REQUEST_PARAMS, DEFAULT_PAGE, DEFAULT_SIZE))
                .withRel("tags"));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getGiftCertificatesByTagId(tagDto.getId(), REQUEST_PARAMS, DEFAULT_PAGE, DEFAULT_SIZE))
                .withRel("gift-certificates-by-tag-id"));
        tagDto.add(linkTo(methodOn(CONTROLLER)
                .getGiftCertificatesByTagName(tagDto.getName(), REQUEST_PARAMS, DEFAULT_PAGE, DEFAULT_SIZE))
                .withRel("gift-certificates-by-tag-name"));
    }
}
