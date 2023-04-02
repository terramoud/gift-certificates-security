package com.epam.esm.service.api;

import com.epam.esm.domain.payload.TagDto;
import com.epam.esm.domain.payload.TagFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService extends BaseService<TagDto, Long> {

    /**
     * Find most popular tag of user with highest cost of all orders.
     *
     * @return the found tag
     */
    TagDto findMostPopularTagOfUserWithHighestCostOfAllOrders();

    List<TagDto> findAll(TagFilterDto tagFilterDto, Pageable pageable);
}
