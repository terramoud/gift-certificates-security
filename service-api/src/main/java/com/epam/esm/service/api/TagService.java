package com.epam.esm.service.api;

import com.epam.esm.domain.payload.TagDto;

public interface TagService extends BaseService<TagDto, Long> {

    /**
     * Find most popular tag of user with highest cost of all orders.
     *
     * @return the found tag
     */
    TagDto findMostPopularTagOfUserWithHighestCostOfAllOrders();
}
