package com.epam.esm.service.impl;

import com.epam.esm.config.ServiceTestConfig;
import com.epam.esm.domain.converter.impl.CertificateDtoConverter;
import com.epam.esm.domain.converter.impl.TagDtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.impl.CertificateRepositoryImpl;
import com.epam.esm.repository.impl.TagRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ServiceTestConfig.class})
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CertificateServiceImplTest {

    private static final CertificateDto INPUT_CERTIFICATE_DTO = new CertificateDto(1L,
            "standard",
            "standard level gift certificate",
            new BigDecimal("999.99"),
            180,
            LocalDateTime.parse("2023-01-02T07:37:15"),
            LocalDateTime.parse("2023-01-02T07:37:15"),
            Collections.emptySet());

    private static final List<Certificate> CERTIFICATES = LongStream.range(0, 9L)
            .mapToObj(i -> new Certificate(i, "certificateName" + i,
                    "standard level gift certificate" + i,
                    new BigDecimal("999.99"),
                    180,
                    LocalDateTime.parse("2023-01-02T07:37:15"),
                    LocalDateTime.parse("2023-01-02T07:37:15"),
                    Collections.emptySet()))
            .sorted(Comparator.comparing(Certificate::getName)
                    .thenComparing(Certificate::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    private static final List<CertificateDto> EXPECTED_CERTIFICATES = LongStream.range(0, 9L)
            .mapToObj(i -> new CertificateDto(i, "certificateName" + i,
                    "standard level gift certificate" + i,
                    new BigDecimal("999.99"),
                    180,
                    LocalDateTime.parse("2023-01-02T07:37:15"),
                    LocalDateTime.parse("2023-01-02T07:37:15"),
                    Collections.emptySet()))
            .sorted(Comparator.comparing(CertificateDto::getName)
                    .thenComparing(CertificateDto::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());

    @Mock
    private final CertificateRepositoryImpl certificateRepository =
            Mockito.mock(CertificateRepositoryImpl.class);

    @Mock
    private final CertificateDtoConverter converter = Mockito.mock(CertificateDtoConverter.class);

    @Mock
    private final TagRepositoryImpl tagRepository = Mockito.mock(TagRepositoryImpl.class);

    @Mock
    private final TagDtoConverter tagConverter = Mockito.mock(TagDtoConverter.class);

    private CertificateServiceImpl certificateService;

    @BeforeEach
    void setUp() {
        certificateService = new CertificateServiceImpl(
                certificateRepository,
                converter,
                tagRepository,
                tagConverter);
    }

    @AfterEach
    void tearDown() {
        reset(certificateRepository);
        reset(converter);
        reset(certificateRepository);
        reset(tagConverter);
    }

    /**
     * @see CertificateServiceImpl#findAll(LinkedMultiValueMap, PageDto)
     */
    @Test
    void testFindAllShouldReturnSortedListCertificatesByNameAndId() {
        when(certificateRepository.findAll(any(), any())).thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES,
                certificateService.findAll(new LinkedMultiValueMap<>(), new PageDto(1, 1)));
    }

    /**
     * @see CertificateServiceImpl#findAllByTagId(LinkedMultiValueMap, PageDto, Long)
     */
    @Test
    void testFindAllByTagIdShouldReturnSortedListCertificatesByTagId() {
        when(certificateRepository.findAllByTagId(any(), any(), anyLong()))
                .thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES, certificateService.findAllByTagId(
                new LinkedMultiValueMap<>(),
                new PageDto(1, 1),
                1L));
    }

    /**
     * @see CertificateServiceImpl#findAllByTagName(LinkedMultiValueMap, PageDto, String)
     */
    @Test
    void testFindAllByTagNameShouldReturnSortedListCertificatesByTagName() {
        when(certificateRepository.findAllByTagName(any(), any(), anyString()))
                .thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES, certificateService.findAllByTagName(
                new LinkedMultiValueMap<>(),
                new PageDto(1, 1),
                "some tag"));
    }

    /**
     * @see CertificateServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnCertificateDto() {
        CertificateDto expected = new CertificateDto();
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(new Certificate()));
        when(converter.toDto(any(Certificate.class))).thenReturn(expected);
        assertEquals(expected, certificateService.findById(anyLong()));
    }

    /**
     * @see CertificateServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldThrowExceptionWhenCertificateIsNotFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> certificateService.findById(1L));
    }

    /**
     * @see CertificateServiceImpl#create(CertificateDto)
     */
    @Test
    void testCreateShouldCreateNewTagAndReturnTadDto() {
        CertificateDto expected = new CertificateDto();
        when(converter.toEntity(any(CertificateDto.class))).thenReturn(new Certificate());
        when(certificateRepository.save(any())).thenReturn(new Certificate());
        when(converter.toDto(any(Certificate.class))).thenReturn(expected);
        assertEquals(expected, certificateService.create(new CertificateDto()));
    }

    /**
     * @see CertificateServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldDeleteCertificate() {
        CertificateDto expected = new CertificateDto();
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(new Certificate()));
        when(converter.toDto(any(Certificate.class))).thenReturn(expected);
        assertEquals(expected, certificateService.deleteById(anyLong()));
    }

    /**
     * @see CertificateServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldThrowExceptionWhenCertificateIsNotFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> certificateService.deleteById(1L));
    }

    /**
     * @see CertificateServiceImpl#update(Long, CertificateDto)
     */
    @Test
    void testUpdateShouldReturnUpdatedTadDto() {
        CertificateDto expected = INPUT_CERTIFICATE_DTO;
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(new Certificate()));
        when(converter.toEntity(any(CertificateDto.class))).thenReturn(new Certificate());
        when(certificateRepository.update(any(Certificate.class))).thenReturn(new Certificate());
        lenient().when(tagRepository.update(any(Tag.class))).thenReturn(new Tag());
        when(converter.toDto(any(Certificate.class))).thenReturn(expected);
        assertEquals(expected, certificateService.update(1L, INPUT_CERTIFICATE_DTO));
    }

    /**
     * @see CertificateServiceImpl#update(Long, CertificateDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenCertificateIsNotFound() {
        CertificateDto input = new CertificateDto();
        input.setId(99999L);
        assertThrows(ResourceNotFoundException.class,
                () -> certificateService.update(99999L, input));
    }

    /**
     * @see CertificateServiceImpl#update(Long, CertificateDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenCertificateIsNotEquals() {
        assertThrows(InvalidResourcePropertyException.class,
                () -> certificateService.update(999L, INPUT_CERTIFICATE_DTO));
    }
}