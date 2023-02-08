package com.epam.esm.repository.impl;


import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.utils.TriFunction;
import com.epam.esm.repository.api.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
@Slf4j
public class OrderRepositoryImpl extends AbstractRepository<Order, Long> implements OrderRepository {

    private static final String ID = "id";
    private static final String COST = "cost";
    private static final String CREATE_DATE = "createDate";

    private static final String JOINED_FIELD_CERTIFICATE = "certificate";
    private static final String JOINED_FIELD_USER = "user";
    private static final String CERTIFICATE_JOINED_FIELD_TAGS = "tags";
    private static final String USER_ID = "id";
    private static final String[] FIELDS_FOR_SEARCH = {};

    private static final Map<String, BiFunction<CriteriaBuilder, Root<Order>,
            javax.persistence.criteria.Order>> SORT_ORDERS_MAP =
            Map.of(
                    "+createDate", (cb, root) -> cb.asc(root.get(CREATE_DATE)),
                    "-createDate", (cb, root) -> cb.desc(root.get(CREATE_DATE)),
                    "+cost", (cb, root) -> cb.asc(root.get(COST)),
                    "-cost", (cb, root) -> cb.desc(root.get(COST)),
                    "+id", (cb, root) -> cb.asc(root.get(ID)),
                    "-id", (cb, root) -> cb.desc(root.get(ID))
            );

    private static final Map<String,
            TriFunction<CriteriaBuilder, Root<Order>, String, Predicate>> FIELDS_TO_FILTERS_MAP =
            Map.ofEntries(
                    Map.entry(COST, (cb, r, filterValue) -> cb.equal(r.get(COST), filterValue)),
                    Map.entry(CREATE_DATE, (cb, r, filterValue) -> cb.equal(r.get(CREATE_DATE), filterValue))
            );

    private final EntityManager entityManager;

    public OrderRepositoryImpl(EntityManager em) {
        super(Order.class, em, SORT_ORDERS_MAP, FIELDS_TO_FILTERS_MAP, FIELDS_FOR_SEARCH);
        this.entityManager = em;
        root.fetch(JOINED_FIELD_CERTIFICATE, JoinType.LEFT)
                .fetch(CERTIFICATE_JOINED_FIELD_TAGS, JoinType.LEFT);
    }

    @Override
    public Optional<Order> findById(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
            Root<Order> root = criteriaQuery.from(Order.class);
            criteriaQuery.where(criteriaBuilder.equal(root.get(ID), id));
            return Optional.ofNullable(entityManager.createQuery(criteriaQuery).getSingleResult());
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
        Predicate predicate = cb.equal(userJoin.get(USER_ID), userId);
        return findAllByPredicates(fields, pageable, predicate);
    }
}
