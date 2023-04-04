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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ExceptionConstants.INVALID_JSON_PATCH;


/**
 * The {@code CertificateController} class represents a RESTfull web service that handles
 * HTTP requests related to gift certificates.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@RestController
@RequestMapping("api/v1/gift-certificates")
@AllArgsConstructor
@Validated
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;
    private final ObjectMapper objectMapper;
    private final HateoasAdder<CertificateDto> hateoasAdder;

    /**
     * Handles the HTTP GET request to retrieve all gift certificates.
     *
     * @param certificateFilterDto the filter criteria to apply to the query
     * @param pageable             the pagination information
     * @return a {@link ResponseEntity} containing a list of
     *          {@link CertificateDto}s and a status code
     */
    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates(
            CertificateFilterDto certificateFilterDto,
            Pageable pageable) {
        List<CertificateDto> certificateDtos =
                certificateService.findAll(certificateFilterDto, pageable);
        hateoasAdder.addLinks(certificateDtos);
        return new ResponseEntity<>(certificateDtos, HttpStatus.OK);
    }

    /**
     * Handles the HTTP GET request to retrieve a gift certificate by its ID.
     *
     * @param certificateId the ID of the certificate to retrieve
     * @return a {@link ResponseEntity} containing the retrieved
     *          {@link CertificateDto} and a status code
     */
    @GetMapping("/{certificate-id}")
    public ResponseEntity<CertificateDto> getCertificateById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId) {
        CertificateDto certificateDto = certificateService.findById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.OK);
    }

    /**
     * Handles the HTTP POST request to create a new gift certificate.
     *
     * @param certificateDto the {@link CertificateDto}
     *                         representing the new certificate to create
     * @return a {@link ResponseEntity} containing the
     *          created {@link CertificateDto} and a status code
     */
    @PostMapping
    @AdminWritePermission
    @Validated(OnCreate.class)
    public ResponseEntity<CertificateDto> addCertificate(@RequestBody @Valid CertificateDto certificateDto) {
        CertificateDto addedCertificateDto = certificateService.create(certificateDto);
        hateoasAdder.addLinks(addedCertificateDto);
        return new ResponseEntity<>(addedCertificateDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing gift certificate with a specified
     * ID in the database. Returns HTTP status 200.
     * @param certificateId a unique identifier of the gift certificate to update
     * @param certificateDto an object containing the updated gift certificate
     * @return ResponseEntity with CertificateDto and HTTP status 200 (OK)
     */
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

    /**
     * Deletes the gift certificate with the given ID.
     * This method handles HTTP DELETE requests with the
     * URL "api/v1/gift-certificates/{certificate-id}"
     * and the path variable "certificate-id" set to a positive integer.
     * This method requires the user making the request
     * to have the "ADMIN_WRITE" authority.
     *
     * @param certificateId the ID of the gift certificate to delete
     * @return a response entity with no body and the HTTP status code "204 No Content"
     */
    @DeleteMapping("/{certificate-id}")
    @AdminWritePermission
    public ResponseEntity<CertificateDto> deleteCertificateById(
            @PathVariable("certificate-id") @Positive(message = CERTIFICATE_INVALID_ID) Long certificateId) {
        CertificateDto certificateDto = certificateService.deleteById(certificateId);
        hateoasAdder.addLinks(certificateDto);
        return new ResponseEntity<>(certificateDto, HttpStatus.NO_CONTENT);
    }

    /**
     * Updates the gift certificate with the given ID with a partial update.
     * This method handles HTTP PATCH requests with the
     * URL "api/v1/gift-certificates/{certificate-id}"
     * and the path variable "certificate-id" set to a positive integer.
     * The body of the request should be a JSON patch object conforming
     * to the JSON Patch specification (RFC 6902). This method requires
     * the user making the request to have the "ADMIN_WRITE" authority.
     *
     * @param certificateId the ID of the gift certificate to update
     * @param patch the JSON patch to apply to the gift certificate
     * @return a response entity with the updated gift certificate
     *          and the HTTP status code "200 OK"
     * @throws AccessDeniedException if the requesting user does
     *          not have appropriate permissions
     * @throws InvalidJsonPatchException if the JSON patch is invalid or
     *          cannot be applied to the gift certificate
     */
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

    /**
     * Applies a JSON Patch to a CertificateDto object.
     *
     * @param patch the JSON Patch object containing the updates
     * @param certificateDto the CertificateDto object to update
     * @return a validated CertificateDto object with the updates applied
     * @throws InvalidJsonPatchException if there was an issue applying the JSON patch
     */
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
