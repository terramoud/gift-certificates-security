package com.epam.esm.controller;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.CertificateFilterDto;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.OnUpdate;
import com.epam.esm.exceptions.InvalidJsonPatchException;
import com.epam.esm.hateoas.HateoasAdder;
import com.epam.esm.security.annotations.AdminWritePermission;
import com.epam.esm.service.api.CertificateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ExceptionConstants.INVALID_JSON_PATCH;

@RestController
@RequestMapping("api/v1/gift-certificates")
@AllArgsConstructor
@Validated
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;
    private final ObjectMapper objectMapper;
    private final HateoasAdder<CertificateDto> hateoasAdder;

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates(
            CertificateFilterDto certificateFilterDto,
            Pageable pageable) {
        List<CertificateDto> certificateDtos =
                certificateService.findAll(certificateFilterDto, pageable);
        hateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    @GetMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> getCertificateById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId) {
        CertificateDto certificateDto = certificateService.findById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.OK);
    }

    @PostMapping
    @AdminWritePermission
    @Validated(OnCreate.class)
    public ResponseEntity<CertificateDto> addCertificate(@RequestBody @Valid CertificateDto certificateDto) {
        CertificateDto addedCertificateDto = certificateService.create(certificateDto);
        hateoasAdder.addLinks(addedCertificateDto);
        return new ResponseEntity<>(addedCertificateDto, HttpStatus.CREATED);
    }

    @PutMapping("/{certificate-id}")
    @AdminWritePermission
    @Validated({OnUpdate.class})
    public ResponseEntity<CertificateDto> updateCertificateById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId,
            @RequestBody @Valid CertificateDto certificateDto) {
        CertificateDto updatedCertificateDto = certificateService.update(certificateId, certificateDto);
        hateoasAdder.addLinks(updatedCertificateDto);
        return new ResponseEntity<>(updatedCertificateDto, HttpStatus.OK);
    }

    @DeleteMapping("/{certificate-id}")
    @AdminWritePermission
    public ResponseEntity<CertificateDto> deleteCertificateById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId) {
        CertificateDto certificateDto = certificateService.deleteById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{certificate-id}")
    @AdminWritePermission
    public ResponseEntity<CertificateDto> updateCertificatePartiallyById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId,
            @RequestBody JsonPatch patch) {
        CertificateDto partiallyModifiedDto = applyPatch(patch, certificateService.findById(certificateId));
        CertificateDto updatedDto = certificateService.update(certificateId, partiallyModifiedDto);
        hateoasAdder.addLinks(updatedDto);
        return new ResponseEntity<>(updatedDto, HttpStatus.OK);
    }

    private @Valid CertificateDto applyPatch(JsonPatch patch,
                                             CertificateDto certificateDto) throws InvalidJsonPatchException {
        try {
            JsonNode patched = patch.apply(objectMapper.convertValue(certificateDto, JsonNode.class));
            return objectMapper.treeToValue(patched, CertificateDto.class);
        } catch (JsonPatchException | JsonProcessingException | RuntimeException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            throw new InvalidJsonPatchException(INVALID_JSON_PATCH);
        }
    }
}
