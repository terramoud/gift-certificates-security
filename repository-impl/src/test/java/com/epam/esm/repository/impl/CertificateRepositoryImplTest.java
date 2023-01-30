package com.epam.esm.repository.impl;

import com.epam.esm.config.RepositoryTestConfig;
import com.epam.esm.config.TestCertificates;
import com.epam.esm.config.TestTags;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.CertificateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class CertificateRepositoryImplTest {

    @Autowired
    private CertificateRepository certificateRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * @see CertificateRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListCertificatesByPropertiesValues(LinkedMultiValueMap<String, String> fields,
                                                                         Pageable pageable,
                                                                         List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAll(fields, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * @see CertificateRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameOrDescription")
    void testFindAllShouldReturnSortedListCertificatesBySearch(LinkedMultiValueMap<String, String> fields,
                                                               Pageable pageable,
                                                               List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAll(fields, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * @see CertificateRepositoryImpl#findAllCertificatesByTagId(LinkedMultiValueMap, Pageable, Long)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllCertificatesByTagId")
    void testFindAllCertificatesByTagIdShouldReturnSortedListCertificatesByTagId(
            LinkedMultiValueMap<String, String> fields,
            Pageable pageable,
            Long tagId,
            List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAllCertificatesByTagId(fields, pageable, tagId);
        assertEquals(expected, certificates);
    }


    /**
     * @see CertificateRepositoryImpl#findAllCertificatesByTagName(LinkedMultiValueMap, Pageable, String)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllCertificatesByTagName")
    void testFindAllCertificatesByTagNameShouldReturnSortedListCertificatesByTagName(
            LinkedMultiValueMap<String, String> fields,
            Pageable pageable,
            String tagName,
            List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAllCertificatesByTagName(fields, pageable, tagName);
        assertEquals(expected, certificates);
    }

    /**
     * @see CertificateRepositoryImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnTagWithId() {
        TestCertificates tc = new TestCertificates();
        Optional<Certificate> expected = Optional.of(tc.certificate1);
        Optional<Certificate> certificate = certificateRepository.findById(1L);
        assertEquals(expected, certificate);
    }

    /**
     * @see CertificateRepositoryImpl#save(Certificate)
     */
    @Test
    void testSaveShouldCreateEntityInDB() {
        Certificate newCertificate = new Certificate();
        newCertificate.setName("new gift certificate");
        newCertificate.setDescription("new gift certificate description");
        newCertificate.setPrice(new BigDecimal("99.98"));
        newCertificate.setDuration(40);
        newCertificate.setCreateDate(LocalDateTime.of(2022, 3, 6, 22, 0));
        newCertificate.setLastUpdateDate(LocalDateTime.now());
        Tag tag1 = new Tag();
        tag1.setName("new tag1");
        Tag tag5 = new Tag();
        tag5.setName("new tag5");
        Tag tag11 = new Tag();
        tag11.setName("new tag11");
        newCertificate.setTags(Set.of(tag1, tag11, tag5));
        certificateRepository.save(newCertificate);
        Certificate expected = certificateRepository.findById(newCertificate.getId()).get();
        assertEquals(expected, newCertificate);
    }

    /**
     * @see CertificateRepositoryImpl#update(Certificate, Long)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        TestTags tt = new TestTags();
        Certificate certificate = certificateRepository.findById(1L).get();
        certificate.setName("updated certificate");
        certificate.setLastUpdateDate(LocalDateTime.now());
        certificate.addTags(Set.of(tt.tag10, tt.tag12));
        Certificate updatedCertificate = certificateRepository.update(certificate, 1L);
        Certificate expected = certificateRepository.findById(1L).get();
        System.out.println("expected = " + expected.getTags());
        assertEquals(expected, updatedCertificate);
        assertEquals(expected.getTags(), updatedCertificate.getTags());
    }

    /**
     * @see CertificateRepositoryImpl#delete(Certificate)
     */
    @Test
    void testDeleteShouldDeleteEntityInDB() {
        Optional<Certificate> certificateToDelete = certificateRepository.findById(1L);
        certificateRepository.delete(certificateToDelete.get());
        Optional<Certificate> expected = certificateRepository.findById(1L);
        assertThat(expected).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+id, -name"))),
                        PageRequest.of(2, 2),
                        List.of(tc.certificate5, tc.certificate6)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(1, 10),
                        List.of()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(0, 10),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4,
                                tc.certificate5,
                                tc.certificate6,
                                tc.certificate7,
                                tc.certificate8,
                                tc.certificate9,
                                tc.certificate10)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id"))),
                        PageRequest.of(1, 5),
                        List.of(
                                tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+name"))),
                        PageRequest.of(2, 3),
                        List.of(
                                tc.certificate1,
                                tc.certificate3,
                                tc.certificate2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+name"))),
                        PageRequest.of(2, 4),
                        List.of(
                                tc.certificate2,
                                tc.certificate4)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id, -name"))),
                        PageRequest.of(1, 4),
                        List.of(
                                tc.certificate6,
                                tc.certificate5,
                                tc.certificate4,
                                tc.certificate3)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(0, 10),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4,
                                tc.certificate5,
                                tc.certificate6,
                                tc.certificate7,
                                tc.certificate8,
                                tc.certificate9,
                                tc.certificate10)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+description"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate5,
                                tc.certificate7,
                                tc.certificate9,
                                tc.certificate8,
                                tc.certificate6)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-price, +duration, -description"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate10,
                                tc.certificate6,
                                tc.certificate8)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-price"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate3,
                                tc.certificate4,
                                tc.certificate5,
                                tc.certificate6,
                                tc.certificate7)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+create_date, -last_update_date"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate9,
                                tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4))
        );
    }

    private static Stream<Arguments> testCasesForFindAllBySearchPartOfNameOrDescription() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("base"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate5)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("level"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4,
                                tc.certificate5)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("stand"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of(""))),
                        PageRequest.of(0, 10),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4,
                                tc.certificate5,
                                tc.certificate6,
                                tc.certificate7,
                                tc.certificate8,
                                tc.certificate9,
                                tc.certificate10)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of(""))),
                        PageRequest.of(1, 5),
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of("stand"))),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate3,
                                tc.certificate2,
                                tc.certificate1))
        );
    }

    private static Stream<Arguments> testCasesForFindAllCertificatesByTagId() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+description"))),
                        PageRequest.of(0, 5),
                        1L,
                        List.of(tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-price, +duration, -description"))),
                        PageRequest.of(0, 5),
                        2L,
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of(""))),
                        PageRequest.of(0, 5),
                        2L,
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of("stand"))),
                        PageRequest.of(0, 5),
                        3L,
                        List.of(tc.certificate2,
                                tc.certificate1))
        );
    }


    private static Stream<Arguments> testCasesForFindAllCertificatesByTagName() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+description"))),
                        PageRequest.of(0, 5),
                        "language courses",
                        List.of(tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-price, +duration, -description"))),
                        PageRequest.of(0, 5),
                        "dancing courses",
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of(""))),
                        PageRequest.of(0, 5),
                        "dancing courses",
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("-id"),
                                "search", List.of("stand"))),
                        PageRequest.of(0, 5),
                        "diving courses",
                        List.of(tc.certificate2,
                                tc.certificate1))
        );
    }

}