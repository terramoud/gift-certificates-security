package com.epam.esm.controller;

import com.epam.esm.domain.converter.CertificateDtoConverter;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.service.api.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/gift-certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateDtoConverter converter;

    @Autowired
    public CertificateController(CertificateService certificateService,
                                 CertificateDtoConverter converter) {
        this.certificateService = certificateService;
        this.converter = converter;
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates(
            @RequestParam LinkedMultiValueMap<String, String> allRequestParameters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Certificate> certificates = certificateService.getAllCertificates(allRequestParameters, size, page);
        return new ResponseEntity<>(converter.listToDtos(certificates), HttpStatus.OK);
    }

    @GetMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> getCertificateById(@PathVariable("certificate-id") Long certificateId) {
        Certificate certificate = certificateService.getCertificateById(certificateId);
        return new ResponseEntity<>(converter.toDto(certificate), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CertificateDto> addCertificate(@RequestBody CertificateDto certificateDto) {
        Certificate addedCertificate = certificateService.addCertificate(converter.toEntity(certificateDto));
        return new ResponseEntity<>(converter.toDto(addedCertificate), HttpStatus.CREATED);
    }

    @PutMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> updateCertificateById(
            @PathVariable("certificate-id") Long certificateId,
            @RequestBody CertificateDto certificateDto) {
        Certificate updatedCertificate = certificateService
                .updateCertificateById(certificateId, converter.toEntity(certificateDto));
        return new ResponseEntity<>(converter.toDto(updatedCertificate), HttpStatus.OK);
    }

    @DeleteMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> deleteCertificateById(@PathVariable("certificate-id") Long certificateId) {
        Certificate certificate = certificateService.deleteCertificateById(certificateId);
        return new ResponseEntity<>(converter.toDto(certificate), HttpStatus.OK);
    }
}
