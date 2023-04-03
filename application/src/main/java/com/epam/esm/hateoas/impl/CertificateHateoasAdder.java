package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.CertificateFilterDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.hateoas.HateoasAdder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateHateoasAdder implements HateoasAdder<CertificateDto> {

    @Value("${page-size.default}")
    private int defaultSize;
    private static final Class<CertificateController> CONTROLLER = CertificateController.class;
    private final HateoasAdder<TagDto> tagDtoHateoasAdder;

    @Autowired
    public CertificateHateoasAdder(HateoasAdder<TagDto> tagDtoHateoasAdder) {
        this.tagDtoHateoasAdder = tagDtoHateoasAdder;
    }

    @Override
    public void addLinks(CertificateDto certificateDto) {
        certificateDto.add(linkTo(methodOn(CONTROLLER)
                .getCertificateById(certificateDto.getId()))
                .withSelfRel());
        certificateDto.add(linkTo(methodOn(CONTROLLER)
                .deleteCertificateById(certificateDto.getId()))
                .withRel(DELETE));
        certificateDto.add(linkTo(methodOn(CONTROLLER)
                .updateCertificateById(certificateDto.getId(), certificateDto))
                .withRel(UPDATE));
        certificateDto.add(linkTo(methodOn(CONTROLLER)
                .addCertificate(certificateDto))
                .withRel(CREATE));
        certificateDto.add(linkTo(methodOn(CONTROLLER)
                .getAllCertificates(new CertificateFilterDto(), Pageable.ofSize(defaultSize)))
                .withRel("gift-certificates"));
        tagDtoHateoasAdder.addLinks(certificateDto.getTags());
    }
}
