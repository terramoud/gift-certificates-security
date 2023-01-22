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
@RequestMapping("api/v1/gift-certificates")
public class CertificateController {

    private final TagService tagService;
    private final CertificateService certificateService;
    private final CertificateDtoConverter converter;
    private final TagDtoConverter tagConverter;

    @Autowired
    public CertificateController(CertificateService certificateService,
                                 TagService tagService,
                                 CertificateDtoConverter converter,
                                 TagDtoConverter tagConverter) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.converter = converter;
        this.tagConverter = tagConverter;
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Certificate> certificates = certificateService.getAllCertificates(allRequestParameters, size, page);
        List<CertificateDto> certificateDtoList = certificates.stream()
                .map(converter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(certificateDtoList, HttpStatus.OK);
    }

    @GetMapping("/{certificate-id}/tags") // api/v1/gift-certificates/{certificate-id}/tags
    public ResponseEntity<List<TagDto>> getAllTagsByCertificateId(
            @PathVariable("certificate-id") Long certificateId,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Tag> tags = tagService.getAllTagsByCertificateId(allRequestParameters, size, page, certificateId);
        List<TagDto> tagDtoList = tags.stream()
                .map(tagConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tagDtoList, HttpStatus.OK);
    }

    @GetMapping("/name/{certificate-name}/tags") // api/v1/gift-certificates/name/{certificate-name}/tags
    public ResponseEntity<List<TagDto>> getAllTagsByCertificateName(
            @PathVariable("certificate-name") String certificateName,
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Tag> tags = tagService.getAllTagsByCertificateName(allRequestParameters, size, page, certificateName);
        List<TagDto> tagDtoList = tags.stream()
                .map(tagConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tagDtoList, HttpStatus.OK);
    }

    @GetMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> getCertificateById(@PathVariable("certificate-id") Long certificateId) {
        Certificate certificate = certificateService.getCertificateById(certificateId);
        return new ResponseEntity<>(converter.toDto(certificate), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CertificateDto> addCertificate(@RequestBody CertificateDto certificateDto) {
        Certificate addedCertificate = certificateService.addCertificate(converter.ToCertificate(certificateDto));
        return new ResponseEntity<>(converter.toDto(addedCertificate), HttpStatus.CREATED);
    }

    @PutMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> updateCertificateById(
            @PathVariable("certificate-id") Long certificateId,
            @RequestBody CertificateDto certificateDto) {
        Certificate updatedCertificate = certificateService
                .updateCertificateById(certificateId, converter.ToCertificate(certificateDto));
        return new ResponseEntity<>(converter.toDto(updatedCertificate), HttpStatus.OK);
    }

    @DeleteMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> deleteCertificateById(@PathVariable("certificate-id") Long certificateId) {
        Certificate certificate = certificateService.deleteCertificateById(certificateId);
        return new ResponseEntity<>(converter.toDto(certificate), HttpStatus.OK);
    }
}
