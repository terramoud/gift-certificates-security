package com.epam.esm.repository.api;


import com.epam.esm.domain.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

}
