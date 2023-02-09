package com.epam.esm.service.impl;

import com.epam.esm.config.ServiceTestConfig;
import com.epam.esm.domain.converter.impl.CertificateDtoConverter;
import com.epam.esm.domain.converter.impl.TagDtoConverter;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.MostPopularTagNotFoundException;
import com.epam.esm.exceptions.ResourceNotFoundException;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestConfig.class})
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TagServiceImplTest {

    /**
     * Create a mock implementation of the TagRepository
     */
    @Mock
    public final TagRepositoryImpl tagRepository = Mockito.mock(TagRepositoryImpl.class);

    @Mock
    private final TagDtoConverter converter = Mockito.mock(TagDtoConverter.class);

    @Mock
    private final CertificateDtoConverter certificateConverter =
            Mockito.mock(CertificateDtoConverter.class);

    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, converter, certificateConverter);
    }

    @AfterEach
    void tearDown() {
        reset(tagRepository);
        reset(converter);
        reset(certificateConverter);
    }

    /**
     * @see TagServiceImpl#findAll(LinkedMultiValueMap, PageDto)
     */
    @Test
    void testFindAllShouldReturnSortedListTagsByNameAndId() {
        List<Tag> tags = LongStream.range(0, 9L)
                .mapToObj(i -> new Tag(i, "tagName" + i))
                .sorted(Comparator.comparing(Tag::getName)
                        .thenComparing(Tag::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        List<TagDto> expectedTags = LongStream.range(0, 9L)
                .mapToObj(i -> new TagDto(i, "tagName" + i))
                .sorted(Comparator.comparing(TagDto::getName)
                        .thenComparing(TagDto::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        when(tagRepository.findAll(any(), any())).thenReturn(tags);
        when(converter.toDto(anyList())).thenReturn(expectedTags);
        assertEquals(expectedTags, tagService.findAll(new LinkedMultiValueMap<>(), new PageDto(1, 1)));
    }

    /**
     * @see TagServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldReturnTagDto() {
        TagDto expected = new TagDto();
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(new Tag()));
        when(converter.toDto(any(Tag.class))).thenReturn(expected);
        assertEquals(expected, tagService.findById(anyLong()));
    }

    /**
     * @see TagServiceImpl#findById(Long)
     */
    @Test
    void testFindByIdShouldThrowExceptionWhenTagIsNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tagService.findById(1L));
    }

    /**
     * @see TagServiceImpl#create(TagDto)
     */
    @Test
    void testCreateShouldCreateNewTagAndReturnTadDto() {
        TagDto expected = new TagDto();
        when(converter.toEntity(any(TagDto.class))).thenReturn(new Tag());
        when(tagRepository.save(any())).thenReturn(new Tag());
        when(converter.toDto(any(Tag.class))).thenReturn(expected);
        assertEquals(expected, tagService.create(new TagDto()));
    }

    /**
     * @see TagServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldDeleteTag() {
        TagDto expected = new TagDto();
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(new Tag()));
        when(converter.toDto(any(Tag.class))).thenReturn(expected);
        assertEquals(expected, tagService.deleteById(anyLong()));
    }

    /**
     * @see TagServiceImpl#deleteById(Long)
     */
    @Test
    void testDeleteByIdShouldThrowExceptionWhenTagIsNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteById(1L));
    }

    /**
     * @see TagServiceImpl#update(Long, TagDto)
     */
    @Test
    void testUpdateShouldReturnUpdatedTadDto() {
        TagDto expected = new TagDto(1L, "updated");
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(new Tag()));
        when(converter.toEntity(any(TagDto.class))).thenReturn(new Tag());
        when(tagRepository.update(any(Tag.class))).thenReturn(new Tag());
        when(converter.toDto(any(Tag.class))).thenReturn(expected);
        assertEquals(expected, tagService.update(1L, new TagDto(1L, "updated")));
    }

    /**
     * @see TagServiceImpl#update(Long, TagDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenTagIsNotFound() {
        TagDto tagToUpdate = new TagDto(1L, "updated");
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tagService.update(1L, tagToUpdate));
    }

    /**
     * @see TagServiceImpl#update(Long, TagDto)
     */
    @Test
    void testUpdateShouldThrowExceptionWhenTagIsNotEquals() {
        TagDto tagToUpdate = new TagDto(1L, "updated");
        assertThrows(InvalidResourcePropertyException.class, () -> tagService.update(999L, tagToUpdate));
    }

    /**
     * @see TagServiceImpl#findMostPopularTagOfUserWithHighestCostOfAllOrders()
     */
    @Test
    void testFindMostPopularTagOfUserWithHighestCostOfAllOrders() {
        TagDto expected = new TagDto();
        when(tagRepository.findMostPopularTagOfUserWithHighestCostOfAllOrders())
                .thenReturn(Optional.of(new Tag()));
        when(converter.toDto(any(Tag.class))).thenReturn(expected);
        assertEquals(expected, tagService.findMostPopularTagOfUserWithHighestCostOfAllOrders());
    }

    /**
     * @see TagServiceImpl#findMostPopularTagOfUserWithHighestCostOfAllOrders()
     */
    @Test
    void testFindMostPopularTagOfUserWithHighestCostOfAllOrdersShouldThrowExceptionWhenTagIsNotFound() {
        when(tagRepository.findMostPopularTagOfUserWithHighestCostOfAllOrders())
                .thenReturn(Optional.empty());
        assertThrows(MostPopularTagNotFoundException.class,
                () -> tagService.findMostPopularTagOfUserWithHighestCostOfAllOrders());
    }
}