package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.exceptions.*;
import com.epam.esm.repository.api.CertificateRepository;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.service.api.CertificateService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.epam.esm.domain.validation.ValidationConstants.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
@Slf4j
public class CertificateServiceImpl extends AbstractService<CertificateDto, Long> implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final DtoConverter<Certificate, CertificateDto> converter;
    private final TagRepository tagRepository;
    private final DtoConverter<Tag, TagDto> tagConverter;
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String CREATE_DATE = "createDate";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String JOINED_FIELD_TAGS = "tags";
    private static final String TAG_ID_FIELD = "id";
    private static final String TAG_NAME_FIELD = "name";

    private static final Map<String, Sort> sortMap = Map.ofEntries(
            Map.entry("+id", Sort.by(Sort.Direction.ASC, ID)),
            Map.entry("-id", Sort.by(Sort.Direction.DESC, ID)),
            Map.entry("+name", Sort.by(Sort.Direction.ASC, NAME)),
            Map.entry("-name", Sort.by(Sort.Direction.DESC, NAME)),
            Map.entry("+description", Sort.by(Sort.Direction.ASC, DESCRIPTION)),
            Map.entry("-description", Sort.by(Sort.Direction.DESC, DESCRIPTION)),
            Map.entry("+price", Sort.by(Sort.Direction.ASC, PRICE)),
            Map.entry("-price", Sort.by(Sort.Direction.DESC, PRICE)),
            Map.entry("+duration", Sort.by(Sort.Direction.ASC, DURATION)),
            Map.entry("-duration", Sort.by(Sort.Direction.DESC, DURATION)),
            Map.entry("+createDate", Sort.by(Sort.Direction.ASC, CREATE_DATE)),
            Map.entry("-createDate", Sort.by(Sort.Direction.DESC, CREATE_DATE)),
            Map.entry("+lastUpdateDate", Sort.by(Sort.Direction.ASC, LAST_UPDATE_DATE)),
            Map.entry("-lastUpdateDate", Sort.by(Sort.Direction.DESC, LAST_UPDATE_DATE))
    );

    private static final Map<String, Function<String, Specification<Certificate>>> filterMap = Map.of(
            NAME, filterValue -> (root, query, cb) -> cb.equal(root.get(NAME), filterValue),
            DESCRIPTION, filterValue -> (root, query, cb) -> cb.equal(root.get(DESCRIPTION), filterValue),
            PRICE, filterValue -> (root, query, cb) -> cb.equal(root.get(PRICE), filterValue),
            DURATION, filterValue -> (root, query, cb) -> cb.equal(root.get(DURATION), filterValue),
            CREATE_DATE, filterValue -> (root, query, cb) -> {
                try {
                    LocalDateTime.parse(filterValue, FORMATTER);
                    return cb.equal(root.get(CREATE_DATE), LocalDateTime.parse(filterValue));
                } catch (RuntimeException ex) {
                    log.warn(DATE_TIME_PARSE_EXCEPTION, ex);
                    return cb.conjunction();
                }
            },
            LAST_UPDATE_DATE, filterValue -> (root, query, cb) -> {
                try {
                    LocalDateTime.parse(filterValue, FORMATTER);
                    return cb.equal(root.get(LAST_UPDATE_DATE), LocalDateTime.parse(filterValue));
                } catch (RuntimeException ex) {
                    log.warn(DATE_TIME_PARSE_EXCEPTION, ex);
                    return cb.conjunction();
                }
            }
    );

    private static final Map<String, Function<String, Specification<Certificate>>> searchMap = Map.of(
            NAME, searchValue -> (r, query, cb) -> cb.like(r.get(NAME), createLikeQuery(searchValue)),
            DESCRIPTION, searchValue -> (r, query, cb) -> cb.like(r.get(DESCRIPTION), createLikeQuery(searchValue))
    );

    @Override
    public List<CertificateDto> findAll(LinkedMultiValueMap<String, String> requestParams, PageDto pageDto) {
        List<Certificate> certificates =
                findAllAbstract(requestParams, pageDto, certificateRepository, sortMap, filterMap, searchMap);
        return converter.toDto(certificates);
    }

    @Override
    public List<CertificateDto> findAllByTagId(LinkedMultiValueMap<String, String> requestParams,
                                               PageDto pageDto,
                                               Long id) {
        List<Certificate> certificates = findAllAbstract(requestParams,
                pageDto,
                certificateRepository,
                sortMap,
                filterMap,
                searchMap,
                whereJoinedTagsEquals(TAG_ID_FIELD, id));
        return converter.toDto(certificates);
    }

    @Override
    public List<CertificateDto> findAllByTagName(LinkedMultiValueMap<String, String> requestParams,
                                                 PageDto pageDto,
                                                 String tagName) {
        List<Certificate> certificates = findAllAbstract(requestParams,
                pageDto,
                certificateRepository,
                sortMap,
                filterMap,
                searchMap,
                whereJoinedTagsEquals(TAG_NAME_FIELD, tagName));
        return converter.toDto(certificates);
    }

    @Override
    public CertificateDto findById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        return converter.toDto(certificate);
    }

    @Override
    public CertificateDto create(CertificateDto certificateDto) {
        Certificate certificate = converter.toEntity(certificateDto);
        Certificate savedCertificate = certificateRepository.save(certificate);
        return converter.toDto(savedCertificate);
    }

    @Override
    public CertificateDto deleteById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CERTIFICATE_NOT_FOUND, id, ErrorCodes.NOT_FOUND_CERTIFICATE_RESOURCE));
        certificateRepository.delete(certificate);
        return converter.toDto(certificate);
    }

    @Override
    public CertificateDto update(Long id, CertificateDto certificateDto) {
        if (!isEqualsIds(certificateDto.getId(), id)) {
            throw new InvalidResourcePropertyException(CERTIFICATE_ID_NOT_MAPPED, id, ErrorCodes.INVALID_ID_PROPERTY);
        }
        Certificate sourceCertificate = converter.toEntity(findById(id));
        Certificate certificateToUpdate = converter.toEntity(certificateDto);
        Set<Tag> tagsToUpdate = Set.copyOf(certificateToUpdate.getTags());
        certificateToUpdate.mergeTags(sourceCertificate.getTags());
        Certificate updated = certificateRepository.save(certificateToUpdate);
        tagsToUpdate.forEach(tagRepository::save);
        return converter.toDto(updated);
    }

    private <E> Specification<Certificate> whereJoinedTagsEquals(String fieldName, E fieldValue) {
        return (root, query, cb) -> {
            Join<Certificate, Tag> joinedTags = root.join(JOINED_FIELD_TAGS);
            return cb.equal(joinedTags.get(fieldName), fieldValue);
        };
    }
}
