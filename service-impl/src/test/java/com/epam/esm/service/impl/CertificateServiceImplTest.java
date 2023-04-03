package com.epam.esm.service.impl;

import com.epam.esm.config.ServiceTestConfig;
import com.epam.esm.domain.converter.impl.CertificateDtoConverter;
import com.epam.esm.domain.converter.impl.TagDtoConverter;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateFilterDto;
import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.api.CertificateRepository;
import com.epam.esm.repository.api.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

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
    private final CertificateRepository certificateRepository =
            Mockito.mock(CertificateRepository.class);

    @Mock
    private final CertificateDtoConverter converter = Mockito.mock(CertificateDtoConverter.class);

    @Mock
    private final TagRepository tagRepository = Mockito.mock(TagRepository.class);

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
     * @see CertificateServiceImpl#findAll(CertificateFilterDto, Pageable)
     */
    @Test
    void testFindAllShouldReturnSortedListCertificatesByNameAndId() {
        when(certificateRepository.findAll(any(CertificateFilterDto.class), any(Pageable.class)))
                .thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES,
                certificateService.findAll(new CertificateFilterDto(), Pageable.unpaged()));
    }

    /**
     * @see CertificateServiceImpl#findAllByTagId(Long, CertificateFilterDto, Pageable)
     */
    @Test
    void testFindAllByTagIdShouldReturnSortedListCertificatesByTagId() {
        when(certificateRepository.findAllByTagId(anyLong(), any(CertificateFilterDto.class), any(Pageable.class)))
                .thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES, certificateService.findAllByTagId(
                1L, new CertificateFilterDto(), Pageable.unpaged()));
    }

    /**
     * @see CertificateServiceImpl#findAllByTagName(String, CertificateFilterDto, Pageable)
     */
    @Test
    void testFindAllByTagNameShouldReturnSortedListCertificatesByTagName() {
        when(certificateRepository.findAllByTagName(anyString(), any(CertificateFilterDto.class), any(Pageable.class)))
                .thenReturn(CERTIFICATES);
        when(converter.toDto(anyList())).thenReturn(EXPECTED_CERTIFICATES);
        assertEquals(EXPECTED_CERTIFICATES, certificateService.findAllByTagName(
                "some tag", new CertificateFilterDto(), Pageable.unpaged()));
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
        when(certificateRepository.save(any(Certificate.class))).thenReturn(new Certificate());
        lenient().when(tagRepository.save(any(Tag.class))).thenReturn(new Tag());
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