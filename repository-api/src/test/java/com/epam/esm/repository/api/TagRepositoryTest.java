package com.epam.esm.repository.api;

import com.epam.esm.config.TestTags;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.config.RepositoryTestConfig;
import com.epam.esm.domain.payload.TagFilterDto;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The {@link TagRepositoryTest} class provides
 * integration tests for {@link TagRepository} implementation.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepositoryTestConfig.class)
@Transactional
class TagRepositoryTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
    }

    /**
     * @see TagRepository#findMostPopularTagOfUserWithHighestCostOfAllOrders()
     */
    @Test
    void testFindMostPopularTagOfUserWithHighestCostOfAllOrders() {
        Optional<Tag> tag = tagRepository.findMostPopularTagOfUserWithHighestCostOfAllOrders();
        TestTags testTags = new TestTags();
        Optional<Tag> expected = Optional.of(testTags.tag9);
        assertEquals(expected, tag);
    }

    /**
     * @see TagRepository#findAll(TagFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAll")
    void testFindAllShouldReturnSortedListTagsByIdAndName(TagFilterDto tagFilterDto,
                                                          Pageable pageable,
                                                          Comparator<Tag> tagComparator) {
        List<Tag> tagList = em.createQuery("SELECT t FROM Tag t ORDER BY t.id ASC", Tag.class).getResultList();
        List<Tag> expected = List.of(tagList.stream()
                .sorted(tagComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(Tag[]::new));
        List<Tag> tags = tagRepository.findAll(tagFilterDto, pageable);
        assertEquals(expected, tags);
    }

    /**
     * @see TagRepository#findAll(TagFilterDto, Pageable)
     */
    @ParameterizedTest
    @MethodSource("testCasesForFindAllBySearchPartOfNameAndFilter")
    void testFindAllShouldReturnSortedListTagsBySearch(TagFilterDto tagFilterDto,
                                                       Pageable pageable,
                                                       Comparator<Tag> tagComparator) {
        String searchQuery = tagFilterDto.getNameContaining();
        String tagName = (tagFilterDto.getName() == null) ? "" : tagFilterDto.getName();
        String andNameEqualsTagName = (tagName.isEmpty()) ? "" : "and t.name = '" + tagName + "'";
        List<Tag> tagListByBySearchPartOfName = em.createQuery(
                "SELECT t FROM Tag t WHERE t.name like '%" + searchQuery +
                        "%' " + andNameEqualsTagName + " ORDER BY t.id ASC", Tag.class).getResultList();
        List<Tag> expected = List.of(tagListByBySearchPartOfName.stream()
                .sorted(tagComparator)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toArray(Tag[]::new));
        List<Tag> tags = tagRepository.findAll(tagFilterDto, pageable);
        assertEquals(expected, tags);
    }

    /**
     * @see TagRepository#findById(Object)
     */
    @Test
    void testFindByIdShouldReturnTagWithId() {
        TestTags testTags = new TestTags();
        Optional<Tag> expected = Optional.of(testTags.tag1);
        Optional<Tag> tag = tagRepository.findById(1L);
        assertEquals(expected, tag);
    }

    /**
     * @see TagRepository#save(Object)
     */
    @Test
    void testSaveShouldCreateEntityInDB() {
        Tag newTag = new Tag();
        newTag.setName("new Tag");
        tagRepository.save(newTag);
        Optional<Tag> expected = tagRepository.findById(newTag.getId());
        assertEquals(expected.orElseThrow(), newTag);
    }

    /**
     * @see BaseRepository#save(Object)
     */
    @Test
    void testUpdateShouldUpdateEntityInDB() {
        TestTags testTags = new TestTags();
        testTags.tag1.setName("changed tag");
        Tag updatedTag = tagRepository.save(testTags.tag1);
        Tag expected = tagRepository.findById(1L).orElseThrow();
        assertEquals(expected, updatedTag);
    }

    /**
     * @see TagRepository#delete(Object)
     */
    @Test
    void testDeleteShouldDeleteEntityInDB() {
        Optional<Tag> tagToDelete = tagRepository.findById(1L);
        tagRepository.delete(tagToDelete.orElseThrow());
        Optional<Tag> tag = tagRepository.findById(1L);
        assertThat(tag).isEmpty();
    }

    private static Stream<Arguments> testCasesForFindAll() {
        return Stream.of(
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(2, 5, Sort.by("id").ascending()
                                .and(Sort.by("name").descending())),
                        Comparator.comparing(Tag::getId)
                                .thenComparing(Tag::getName, Comparator.reverseOrder())),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(1, 10, Sort.by("id")),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(1, 10, Sort.by("id").descending()),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(2, 3, Sort.by("name").ascending()),
                        Comparator.comparing(Tag::getName)),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(1, 10, Sort.by("id").descending()
                                .and(Sort.by("name").descending())),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())
                                .thenComparing(Tag::getName, Comparator.reverseOrder())),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(1, 10, Sort.by("id")),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(1, 1, Sort.by("id")),
                        Comparator.comparing(Tag::getId))
        );
    }

    private static Stream<Arguments> testCasesForFindAllBySearchPartOfNameAndFilter() {
        return Stream.of(
                Arguments.of(
                        new TagFilterDto(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        TagFilterDto.builder().nameContaining("airpla").build(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        TagFilterDto.builder().nameContaining("d").build(),
                        PageRequest.of(0, 10, Sort.by("id")),
                        Comparator.comparing(Tag::getId)),
                Arguments.of(
                        TagFilterDto.builder().nameContaining("dr").build(),
                        PageRequest.of(0, 10, Sort.by("id").descending()
                                .and(Sort.by("name").ascending())),
                        Comparator.comparing(Tag::getId, Comparator.reverseOrder())
                                .thenComparing(Tag::getName)),
                Arguments.of(
                        TagFilterDto.builder().nameContaining("ma").build(),
                        PageRequest.of(0, 10, Sort.by("name").ascending()
                                .and(Sort.by("id").descending())),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        TagFilterDto.builder()
                                .nameContaining("ma")
                                .name("math courses")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("name").ascending()
                                .and(Sort.by("id").descending())),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        TagFilterDto.builder()
                                .name("math courses")
                                .build(),
                        PageRequest.of(0, 10, Sort.by("name").ascending()
                                .and(Sort.by("id").descending())),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder()),
                Arguments.of(
                        TagFilterDto.builder()
                                .name("math courses")
                                .build(),
                        PageRequest.of(1, 1, Sort.by("name").ascending()
                                .and(Sort.by("id").descending())),
                        Comparator.comparing(Tag::getName)
                                .thenComparing(Tag::getId), Comparator.reverseOrder())
        );
    }
}
