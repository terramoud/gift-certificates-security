package com.epam.esm.repository.impl;

import com.epam.esm.config.TestTags;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.TagRepository;
import com.epam.esm.config.RepositoryTestConfig;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class TagRepositoryImplTest {

    @Autowired
    private TagRepository tagRepository;

    @PersistenceContext
    protected EntityManager em;

    @BeforeEach
    void setUp() {

    }

    /**
     * @see TagRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListTagsByIdAndName(LinkedMultiValueMap<String, String> fields,
                                                          Pageable pageable,
                                                          Comparator<Tag> tagComparator) {
        List<Tag> tagList = em.createQuery("SELECT t FROM Tag t ORDER BY t.id ASC", Tag.class).getResultList();
        List<Tag> expected = List.of(tagList.stream()
                .sorted(tagComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(Tag[]::new));
        List<Tag> tags = tagRepository.findAll(fields, pageable);
        assertEquals(expected, tags);
    }

    /**
     * @see TagRepositoryImpl#findAll(LinkedMultiValueMap, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameAndFilter")
    void testFindAllShouldReturnSortedListTagsBySearch(LinkedMultiValueMap<String, String> fields,
                                                       Pageable pageable,
                                                       Comparator<Tag> tagComparator) {
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0);
        String tagName = fields.getOrDefault("name", List.of("")).get(0);
        String andNameEqualsTagName = (tagName.isEmpty()) ? "" : "and t.name = '" + tagName + "'";
        List<Tag> tagListByBySearchPartOfName = em.createQuery(
                "SELECT t FROM Tag t WHERE t.name like '%" + searchQuery +
                        "%' " + andNameEqualsTagName + " ORDER BY t.id ASC", Tag.class).getResultList();
        List<Tag> expected = List.of(tagListByBySearchPartOfName.stream()
                .sorted(tagComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(Tag[]::new));
        List<Tag> tags = tagRepository.findAll(fields, pageable);
        assertEquals(expected, tags);
    }

    /**
     * @see TagRepositoryImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnTagWithId() {
        TestTags testTags = new TestTags();
        Optional<Tag> expected = Optional.of(testTags.tag1);
        Optional<Tag> tag = tagRepository.findById(1L);
        assertEquals(expected, tag);
    }

    /**
     * @see TagRepositoryImpl#save(Tag)
     */
    @Test
    void testSave() {
        Tag newTag = new Tag();
        newTag.setName("new Tag");
        tagRepository.save(newTag);
        Optional<Tag> expected = tagRepository.findById(newTag.getId());
        assertEquals(expected.get(), newTag);
    }

    /**
     * @see TagRepositoryImpl#update(Tag, Long)
     */
    @Test
    void testUpdate() {
        TestTags testTags = new TestTags();
        testTags.tag1.setName("changed tag");
        Tag updatedTag = tagRepository.update(testTags.tag1, 1L);
        Tag expected = tagRepository.findById(1L).get();
        assertEquals(expected, updatedTag);
    }

    /**
     * @see TagRepositoryImpl#delete(Tag)
     */
    @Test
    void testDelete() {
        Optional<Tag> tagToDelete = tagRepository.findById(1L);
        tagRepository.delete(tagToDelete.get());
        Optional<Tag> tag = tagRepository.findById(1L);
        assertThat(tag).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+id, -name"))),
                        PageRequest.of(2, 5),
                        Comparator.comparing(Tag::getId)
                                .thenComparing(Tag::getName, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of(""))),
                        PageRequest.of(1, 10),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id"))),
                        PageRequest.of(1, 10),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("+name"))),
                        PageRequest.of(2, 3),
                        Comparator.comparing(Tag::getName)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("sort", List.of("-id, -name"))),
                        PageRequest.of(1, 10),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())
                                .thenComparing(Tag::getName, Comparator.reverseOrder())),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(1, 10),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("", List.of(""))),
                        PageRequest.of(1, 1),
                        Comparator.comparing(Tag::getId))
        );
    }

    private static Stream<Arguments> testCasesForFindAllBySearchPartOfNameAndFilter() {
        return Stream.of(
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of(""))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("airpla"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of("search", List.of("d"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of("dr"),
                                "sort", List.of("-id, +name"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())
                                .thenComparing(Tag::getName)),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of("ma"),
                                "sort", List.of("+name, -id"))),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of("ma"),
                                "sort", List.of("+name, -id"),
                                "name", List.of("math courses")
                        )),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "search", List.of(""),
                                "sort", List.of("+name, -id"),
                                "name", List.of("math courses")
                        )),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        new LinkedMultiValueMap<>(Map.of(
                                "sort", List.of("+name, -id"),
                                "name", List.of("math courses")
                        )),
                        PageRequest.of(0, 10),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder())
        );
    }
}