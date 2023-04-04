package com.epam.esm.service.api;

import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.payload.TagFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface represents the business logic for managing tags.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface TagService extends BaseService<TagDto, Long> {

    /**
     * Find most popular tag of user with highest cost of all orders.
     *
     * @return the found tag
     */
    TagDto findMostPopularTagOfUserWithHighestCostOfAllOrders();

    /**
     * Finds all tags that match the given filter and are pageable.
     *
     * @param tagFilterDto the tag filter criteria
     * @param pageable the pagination parameters
     * @return the list of found tags
     */
    List<TagDto> findAll(TagFilterDto tagFilterDto, Pageable pageable);
}
