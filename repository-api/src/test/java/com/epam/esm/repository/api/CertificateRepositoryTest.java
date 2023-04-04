package com.epam.esm.repository.api;

import com.epam.esm.config.RepositoryTestConfig;
import com.epam.esm.config.TestCertificates;
import com.epam.esm.config.TestTags;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.CertificateFilterDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The {@code CertificateRepositoryTest} class provides integration tests
 * for {@link CertificateRepository} implementation.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class CertificateRepositoryTest {

    @Autowired
    private CertificateRepository certificateRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Tests the {@link CertificateRepository#findAll(CertificateFilterDto, Pageable)}
     * method to ensure that it returns a sorted list of certificates
     * according to the specified properties values.
     *
     * @param certificateFilterDto the filter criteria to be applied
     *                             to the certificates.
     * @param pageable the page information to be used for pagination.
     * @param expected the expected list of certificates after sorting
     *                 according to the specified properties values.
     * @see CertificateRepository#findAll(CertificateFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListCertificatesByPropertiesValues(CertificateFilterDto certificateFilterDto,
                                                                         Pageable pageable,
                                                                         List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAll(certificateFilterDto, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * Tests the {@link CertificateRepository#findAll(CertificateFilterDto, Pageable)}
     * method to ensure that it returns a sorted list of certificates
     * according to the specified field values in the filter.
     *
     * @param certificateFilterDto the filter criteria to be applied
     *                            to the certificates.
     * @param pageable the page information to be used for pagination.
     * @param expected the expected list of certificates after
     *                 sorting according to the specified field
     *                 values in the filter.
     * @see CertificateRepository#findAll(CertificateFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllByFilterByFieldValues")
    void testFindAllShouldReturnSortedListCertificatesByFilterByFieldValues(CertificateFilterDto certificateFilterDto,
                                                                            Pageable pageable,
                                                                            List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAll(certificateFilterDto, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * This test case verifies that the method `findAll` of the `CertificateRepository` interface
     * returns a sorted list of certificates by searching part of their name or description.
     * It uses the test cases provided by the method `testCasesForFindAllBySearchPartOfNameOrDescription`
     * as input parameters to call the `findAll` method and compare the expected result with the actual result.
     *
     * @param certificateFilterDto the filter object for certificate search
     * @param pageable the pagination object
     * @param expected the list of expected certificates
     * @see CertificateRepository#findAll(CertificateFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameOrDescription")
    void testFindAllShouldReturnSortedListCertificatesBySearch(CertificateFilterDto certificateFilterDto,
                                                               Pageable pageable,
                                                               List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAll(certificateFilterDto, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * This test case verifies that the method `findAllByTagId` of the `CertificateRepository` interface
     * returns a sorted list of certificates by tag id. It uses the test cases provided by the method
     * `testCasesForFindAllCertificatesByTagId` as input parameters to call the `findAllByTagId` method
     * and compare the expected result with the actual result.
     *
     * @param certificateFilterDto the filter object for certificate search
     * @param pageable the pagination object
     * @param tagId the tag id
     * @param expected the list of expected certificates
     * @see CertificateRepository#findAllByTagId(Long, CertificateFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllCertificatesByTagId")
    void testFindAllCertificatesByTagIdShouldReturnSortedListCertificatesByTagId(
            CertificateFilterDto certificateFilterDto,
            Pageable pageable,
            Long tagId,
            List<Certificate> expected) {
        List<Certificate> certificates = certificateRepository.findAllByTagId(tagId, certificateFilterDto, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * This test case verifies that the method `findAllByTagName` of the `CertificateRepository` interface
     * returns a sorted list of certificates by tag name. It uses the test cases provided by the method
     * `testCasesForFindAllCertificatesByTagName` as input parameters to call the `findAllByTagName` method
     * and compare the expected result with the actual result.
     *
     * @param certificateFilterDto the filter object for certificate search
     * @param pageable the pagination object
     * @param tagName the tag name
     * @param expected the list of expected certificates
     * @see CertificateRepository#findAllByTagName(String, CertificateFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllCertificatesByTagName")
    void testFindAllCertificatesByTagNameShouldReturnSortedListCertificatesByTagName(
            CertificateFilterDto certificateFilterDto,
            Pageable pageable,
            String tagName,
            List<Certificate> expected) {
        List<Certificate> certificates =
                certificateRepository.findAllByTagName(tagName, certificateFilterDto, pageable);
        assertEquals(expected, certificates);
    }

    /**
     * Tests if the method findById(Object) of CertificateRepository
     * interface returns the expected Certificate object that
     * matches the given ID.
     * This test creates a new TestCertificates object to get
     * the expected certificate and calls the findById method
     * with an ID of 1L from certificateRepository object.
     * It then compares the returned Optional<Certificate>
     * object with the expected Optional<Certificate> object
     * using the assertEquals method.
     *
     * @see CertificateRepository#findById(Object)
     */
    @Test
    void testFindByIdShouldReturnCertificateWithId() {
        TestCertificates tc = new TestCertificates();
        Optional<Certificate> expected = Optional.of(tc.certificate1);
        Optional<Certificate> certificate = certificateRepository.findById(1L);
        assertEquals(expected, certificate);
    }

    /**
     * Test that saving a new certificate creates a new entity in the database.
     *
     * This method creates a new Certificate object and sets its properties,
     * then saves it using the {@link CertificateRepository#save(Object)}
     * method. It then retrieves the saved certificate using the
     * {@link CertificateRepository#findById(Object)} method, and
     * asserts that the expected certificate and the saved
     * certificate are equal.
     *
     * @see CertificateRepository#save(Object)
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
        Certificate expected = certificateRepository.findById(newCertificate.getId()).orElseThrow();
        assertEquals(expected, newCertificate);
    }

    /**
     * Updates an existing certificate in the database with new
     * values and returns the updated certificate.
     *
     * @see CertificateRepository#save(Object)
     * @see TestTags
     * @throws NoSuchElementException if no element with the given
     *          id was found in the database
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        TestTags tt = new TestTags();
        Certificate certificate = certificateRepository.findById(1L).orElseThrow();
        certificate.setName("updated certificate");
        certificate.setLastUpdateDate(LocalDateTime.now());
        certificate.addTags(Set.of(tt.tag10, tt.tag12));
        Certificate updatedCertificate = certificateRepository.save(certificate);
        Certificate expected = certificateRepository.findById(1L).orElseThrow();
        assertEquals(expected, updatedCertificate);
        assertEquals(expected.getTags(), updatedCertificate.getTags());
    }


    /**
     * This test method checks if an entity is deleted from the
     * database when delete method is called and the corresponding
     * entity is no longer found in the database.
     *
     * @see CertificateRepository#delete(Object)
     */
    @Test
    void testDeleteShouldDeleteEntityInDB() {
        Optional<Certificate> certificateToDelete = certificateRepository.findById(1L);
        certificateRepository.delete(certificateToDelete.orElseThrow());
        Optional<Certificate> expected = certificateRepository.findById(1L);
        assertThat(expected).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAllByFilterByFieldValues() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        CertificateFilterDto.builder()
                                .description("New Year edition gift certificate")
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of(tc.certificate9)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .name("standard plus")
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of(tc.certificate2)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .lastUpdateDate(LocalDateTime.parse("2023-01-01T07:37:14.974"))
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of(tc.certificate9,
                                tc.certificate1,
                                tc.certificate2,
                                tc.certificate3)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .lastUpdateDate(LocalDateTime.parse("2023-01-01T07:37:14.974"))
                                .price(new BigDecimal("1099.99"))
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of(tc.certificate9,
                                tc.certificate3)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .duration(180)
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of(tc.certificate1)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .duration(1800)
                                .build(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
                        List.of())
        );
    }

    private static Stream<Arguments> testCasesForFindAll() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(2, 2, Sort.by("id").ascending()
                                .and(Sort.by("name").descending())),
                        List.of(tc.certificate5, tc.certificate6)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(1, 10),
                        List.of()),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
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
                        new CertificateFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id").descending()),
                        List.of(
                                tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(2, 3, Sort.by("name").ascending()),
                        List.of(
                                tc.certificate1,
                                tc.certificate3,
                                tc.certificate2)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(2, 4, Sort.by("name").ascending()),
                        List.of(
                                tc.certificate2,
                                tc.certificate4)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(1, 4, Sort.by("id").descending()
                                .and(Sort.by("name").descending())),
                        List.of(
                                tc.certificate6,
                                tc.certificate5,
                                tc.certificate4,
                                tc.certificate3)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
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
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("description").ascending()),
                        List.of(tc.certificate5,
                                tc.certificate7,
                                tc.certificate9,
                                tc.certificate8,
                                tc.certificate6)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("price").descending()
                                .and(Sort.by("duration").ascending())
                                .and(Sort.by("description").descending())),
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate10,
                                tc.certificate6,
                                tc.certificate8)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("price").descending()),
                        List.of(tc.certificate3,
                                tc.certificate4,
                                tc.certificate5,
                                tc.certificate6,
                                tc.certificate7)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("createDate").ascending()
                                .and(Sort.by("lastUpdateDate").descending())),
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
                        CertificateFilterDto.builder()
                                .nameContaining("base")
                                .build(),
                        PageRequest.of(0, 5),
                        List.of(tc.certificate5)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .descriptionContaining("level")
                                .build(),
                        PageRequest.of(0, 5, Sort.by("id")),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3,
                                tc.certificate4,
                                tc.certificate5)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .descriptionContaining("stand")
                                .nameContaining("stand").build(),
                        PageRequest.of(0, 5, Sort.by("id")),
                        List.of(tc.certificate1,
                                tc.certificate2,
                                tc.certificate3)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
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
                        new CertificateFilterDto(),
                        PageRequest.of(1, 5, Sort.by("id").descending()),
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .descriptionContaining("stand")
                                .nameContaining("stand").build(),
                        PageRequest.of(0, 5, Sort.by("id").descending()),
                        List.of(tc.certificate3,
                                tc.certificate2,
                                tc.certificate1))
        );
    }

    private static Stream<Arguments> testCasesForFindAllCertificatesByTagId() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("description").ascending()),
                        1L,
                        List.of(tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("price").descending()
                                .and(Sort.by("duration").ascending())
                                .and(Sort.by("description").descending())),
                        2L,
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("id").descending()),
                        2L,
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .descriptionContaining("stand")
                                .nameContaining("stand").build(),
                        PageRequest.of(0, 5, Sort.by("id").descending()),
                        3L,
                        List.of(tc.certificate2,
                                tc.certificate1))
        );
    }


    private static Stream<Arguments> testCasesForFindAllCertificatesByTagName() {
        TestCertificates tc = new TestCertificates();
        return Stream.of(
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("description").ascending()),
                        "language courses",
                        List.of(tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("price").descending()
                                .and(Sort.by("duration").ascending())
                                .and(Sort.by("description").descending())),
                        "dancing courses",
                        List.of(tc.certificate4,
                                tc.certificate3,
                                tc.certificate5,
                                tc.certificate1,
                                tc.certificate2)),
                Arguments.of(
                        new CertificateFilterDto(),
                        PageRequest.of(0, 5, Sort.by("id").descending()),
                        "dancing courses",
                        List.of(tc.certificate5,
                                tc.certificate4,
                                tc.certificate3,
                                tc.certificate2,
                                tc.certificate1)),
                Arguments.of(
                        CertificateFilterDto.builder()
                                .descriptionContaining("stand")
                                .nameContaining("stand").build(),
                        PageRequest.of(0, 5, Sort.by("id").descending()),
                        "diving courses",
                        List.of(tc.certificate2,
                                tc.certificate1))
        );
    }
}