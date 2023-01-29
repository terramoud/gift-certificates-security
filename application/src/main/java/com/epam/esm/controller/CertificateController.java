package com.epam.esm.controller;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.service.api.CertificateService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/gift-certificates")
@AllArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final HateoasAdder<CertificateDto> hateoasAdder;

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size) {
        List<CertificateDto> certificateDtos =
                certificateService.findAll(allRequestParameters, new PageDto(page, size));
        hateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> getCertificateById(@PathVariable("certificate-id") Long certificateId) {
        CertificateDto certificateDto = certificateService.findById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CertificateDto> addCertificate(@RequestBody CertificateDto certificateDto) {
        CertificateDto addedCertificateDto  = certificateService.create(certificateDto);
        hateoasAdder.addLinks(addedCertificateDto);
        return new ResponseEntity<>(addedCertificateDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> updateCertificateById(
            @PathVariable("certificate-id") Long certificateId,
            @RequestBody CertificateDto certificateDto) {
        CertificateDto updatedCertificateDto = certificateService.update(certificateId, certificateDto);
        hateoasAdder.addLinks(updatedCertificateDto);
        return new ResponseEntity<>(updatedCertificateDto, HttpStatus.OK);
    }

    @PutMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> updateCertificateByIdPut(
            @PathVariable("certificate-id") Long certificateId,
            @RequestBody CertificateDto certificateDto) {
        CertificateDto updatedCertificateDto = certificateService.update(certificateId, certificateDto);
        hateoasAdder.addLinks(updatedCertificateDto);
        return new ResponseEntity<>(updatedCertificateDto, HttpStatus.OK);
    }

    @DeleteMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> deleteCertificateById(@PathVariable("certificate-id") Long certificateId) {
        CertificateDto certificateDto = certificateService.deleteById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.OK);
    }
}
