package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TagRepository extends BaseRepository<Tag, Long> {
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
