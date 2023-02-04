package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Tag;

import java.util.Optional;


public interface TagRepository extends BaseRepository<Tag, Long> {
    Optional<Tag> findMostPopularTagOfUserWithHighestCostOfAllOrders();
}
