package com.epam.esm.repository.impl;


import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
@Slf4j
public class OrderRepositoryImpl extends AbstractRepository<Order, Long> implements OrderRepository {

    private static final String ORDER_ID = "id";
    private static final String ORDER_COST = "cost";
    private static final String ORDER_CREATE_DATE = "createDate";
    private static final String PARAM_COST = "cost";
    private static final String PARAM_CREATE_DATE = "create_date";

    private static final String JOINED_FIELD_CERTIFICATE = "certificate";
    private static final String JOINED_FIELD_USER = "user";
    private static final String CERTIFICATE_JOINED_FIELD_TAGS = "tags";
    private static final String USER_ID = "id";
    private static final String[] ADMITTED_SORT_PARAMS = {ORDER_ID, ORDER_COST, ORDER_CREATE_DATE};
    private static final String[] FIELDS_FOR_SEARCH = {};

    private static final Map<String, BiFunction<CriteriaBuilder, Root<Order>,
            javax.persistence.criteria.Order>> SORT_ORDERS_MAP =
            Map.of(
                    "+create_date", (cb, root) -> cb.asc(root.get(ORDER_CREATE_DATE)),
                    "-create_date", (cb, root) -> cb.desc(root.get(ORDER_CREATE_DATE)),
                    "+cost", (cb, root) -> cb.asc(root.get(ORDER_COST)),
                    "-cost", (cb, root) -> cb.desc(root.get(ORDER_COST)),
                    "+id", (cb, root) -> cb.asc(root.get(ORDER_ID)),
                    "-id", (cb, root) -> cb.desc(root.get(ORDER_ID))
            );
    private static final Map<String, String> REQUEST_PARAM_TO_ENTITY_FIELD_NAME = Map.of(
            PARAM_COST, ORDER_COST,
            PARAM_CREATE_DATE, ORDER_CREATE_DATE
    );

    public OrderRepositoryImpl() {
        super(Order.class,
                SORT_ORDERS_MAP,
                ADMITTED_SORT_PARAMS,
                REQUEST_PARAM_TO_ENTITY_FIELD_NAME,
                FIELDS_FOR_SEARCH);
    }

    public Optional<Order> findById(Long id) {
        try {
            fetchLeftJoin(root);
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
            Root<Order> root = criteriaQuery.from(Order.class);
            criteriaQuery.where(criteriaBuilder.equal(root.get(ORDER_ID), id));
            return Optional.ofNullable(em.createQuery(criteriaQuery).getSingleResult());
        } catch (EmptyResultDataAccessException | PersistenceException ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAllByUserId(LinkedMultiValueMap<String, String> fields,
                                       Pageable pageable,
                                       Long userId) {
        Join<Order, User> userJoin = root.join(JOINED_FIELD_USER);
        Predicate predicate = criteriaBuilder.equal(userJoin.get(USER_ID), userId);
        return findAllByPredicates(fields, pageable, predicate);
    }

    @Override
    protected void fetchLeftJoin(Root<Order> root) {
        root.fetch(JOINED_FIELD_CERTIFICATE, JoinType.LEFT)
                .fetch(CERTIFICATE_JOINED_FIELD_TAGS, JoinType.LEFT);
    }
}
