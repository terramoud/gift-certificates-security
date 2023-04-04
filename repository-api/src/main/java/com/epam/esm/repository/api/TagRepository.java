package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.payload.TagFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * The TagRepository interface provides methods for working
 * with tags in the database.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Repository
public interface TagRepository extends BaseRepository<Tag, Long> {

    /**
     * Finds all tags matching the specified filter.
     *
     * @param filter the filter to apply to the search
     * @param pageable the pagination information
     * @return the list of found tags
     * @throws IllegalArgumentException if the provided filter or pageable parameters are invalid
     */
    @Query(value = "SELECT t FROM Tag t " +
            "WHERE " +
            "(:#{#filter.name} is null or t.name = :#{#filter.name}) AND " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :#{#filter.nameContaining}, '%'))")
    List<Tag> findAll(@Param("filter") TagFilterDto filter, Pageable pageable);

    /**
     * Finds the most popular tag among the orders of the user
     * with the highest total cost of all orders.
     *
     * @return the most popular tag
     */
    @Query(value = "SELECT t.* " +
            "FROM tags t " +
            "JOIN certificates_tags ct ON ct.tag_id = t.id " +
            "JOIN certificates c ON c.id = ct.certificate_id " +
            "JOIN orders o ON o.gift_certificate_id = c.id " +
            "WHERE o.user_id = (" +
            "   SELECT o2.user_id " +
            "   FROM orders o2 " +
            "   GROUP BY o2.user_id " +
            "   ORDER BY SUM(o2.cost) DESC " +
            "   LIMIT 1" +
            ") " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(t.id) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Tag> findMostPopularTagOfUserWithHighestCostOfAllOrders();
}
