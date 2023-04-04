package com.epam.esm.repository.api;


import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.payload.OrderFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface is a repository for working with the Order
 * entity in the database.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    /**
     * Finds all orders with pagination and filtering.
     *
     * @param filter the filter to apply to the orders
     * @param pageable the pagination settings to use
     * @return a list of orders matching the filter criteria
     * @throws IllegalArgumentException if the filter is invalid
     */
    @Query(value = "SELECT o FROM Order o " +
            "WHERE " +
            "(:#{#filter.cost} is null or o.cost = :#{#filter.cost}) AND " +
            "(:#{#filter.createDate} is null or o.createDate = :#{#filter.createDate}) AND " +
            "(:#{#filter.user.id} is null or o.user.id = :#{#filter.user.id}) AND " +
            "(:#{#filter.user.login} is null or o.user.login = :#{#filter.user.login}) AND " +
            "(:#{#filter.user.email} is null or o.user.email = :#{#filter.user.email}) AND " +
            "(:#{#filter.user.role} is null or o.user.role = :#{#filter.user.role}) AND " +
            "(:#{#filter.certificate.name} is null or o.certificate.name = :#{#filter.certificate.name}) AND " +
            "(:#{#filter.certificate.description} is null or o.certificate.description = :#{#filter.certificate.description}) AND " +
            "(:#{#filter.certificate.price} is null or o.certificate.price = :#{#filter.certificate.price}) AND " +
            "(:#{#filter.certificate.duration} is null or o.certificate.duration = :#{#filter.certificate.duration}) AND " +
            "(:#{#filter.certificate.createDate} is null or o.certificate.createDate = :#{#filter.certificate.createDate}) AND " +
            "(:#{#filter.certificate.lastUpdateDate} is null or o.certificate.lastUpdateDate = :#{#filter.certificate.lastUpdateDate})")
    List<Order> findAll(@Param("filter") OrderFilterDto filter, Pageable pageable);

    /**
     * Finds all orders for a given user with pagination and filtering.
     *
     * @param userId the ID of the user to find orders for
     * @param filter the filter to apply to the orders
     * @param pageable the pagination settings to use
     * @return a list of orders matching the filter criteria
     * @throws IllegalArgumentException if the user ID or filter is invalid
     */
    @Query(value = "SELECT o FROM Order o JOIN o.user u " +
            "WHERE u.id = :userId AND " +
            "(:#{#filter.cost} is null or o.cost = :#{#filter.cost}) AND " +
            "(:#{#filter.createDate} is null or o.createDate = :#{#filter.createDate}) AND " +
            "(:#{#filter.user.id} is null or o.user.id = :#{#filter.user.id}) AND " +
            "(:#{#filter.user.login} is null or o.user.login = :#{#filter.user.login}) AND " +
            "(:#{#filter.user.email} is null or o.user.email = :#{#filter.user.email}) AND " +
            "(:#{#filter.user.role} is null or o.user.role = :#{#filter.user.role}) AND " +
            "(:#{#filter.certificate.name} is null or o.certificate.name = :#{#filter.certificate.name}) AND " +
            "(:#{#filter.certificate.description} is null or o.certificate.description = :#{#filter.certificate.description}) AND " +
            "(:#{#filter.certificate.price} is null or o.certificate.price = :#{#filter.certificate.price}) AND " +
            "(:#{#filter.certificate.duration} is null or o.certificate.duration = :#{#filter.certificate.duration}) AND " +
            "(:#{#filter.certificate.createDate} is null or o.certificate.createDate = :#{#filter.certificate.createDate}) AND " +
            "(:#{#filter.certificate.lastUpdateDate} is null or o.certificate.lastUpdateDate = :#{#filter.certificate.lastUpdateDate})")
    List<Order> findAllByUserId(@Param("userId") Long userId,
                                @Param("filter") OrderFilterDto filter,
                                Pageable pageable);
}
