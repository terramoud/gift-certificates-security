package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.utils.TriFunction;
import com.epam.esm.repository.api.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class TagRepositoryImpl extends AbstractRepository<Tag, Long> implements TagRepository {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ORDER_JOINED_FIELD_USER = "user";
    private static final String USER_ID = "id";
    private static final String ORDER_COST = "cost";
    private static final String ORDER_JOINED_FIELD_CERTIFICATE = "certificate";
    private static final String CERTIFICATE_JOINED_FIELD_TAGS = "tags";
    private static final int ONE_RESULT = 1;
    private static final int FIRST_ELEMENT = 0;
    private static final String[] FIELDS_FOR_SEARCH = {NAME};

    private static final Map<String,
            BiFunction<CriteriaBuilder, Root<Tag>, javax.persistence.criteria.Order>> SORT_ORDERS_MAP = Map.of(
            "+name", (cb, root) -> cb.asc(root.get(NAME)),
            "-name", (cb, root) -> cb.desc(root.get(NAME)),
            "+id", (cb, root) -> cb.asc(root.get(ID)),
            "-id", (cb, root) -> cb.desc(root.get(ID))
    );

    private static final Map<String,
            TriFunction<CriteriaBuilder, Root<Tag>, String, Predicate>> FIELDS_TO_FILTERS_MAP =
            Map.ofEntries(
                    Map.entry(NAME, (cb, r, filterValue) -> cb.equal(r.get(NAME), filterValue))
            );

    private final EntityManager entityManager;

    @Autowired
    public TagRepositoryImpl(EntityManager em) {
        super(Tag.class, em, SORT_ORDERS_MAP, FIELDS_TO_FILTERS_MAP, FIELDS_FOR_SEARCH);
        this.entityManager = em;
    }

    @Override
    public Optional<Tag> findMostPopularTagOfUserWithHighestCostOfAllOrders() {
        CriteriaQuery<Long> longCriteriaQuery = cb.createQuery(Long.class);
        Root<Order> order = longCriteriaQuery.from(Order.class);
        longCriteriaQuery.select(order.get(ORDER_JOINED_FIELD_USER).get(USER_ID))
                .groupBy(order.get(ORDER_JOINED_FIELD_USER).get(USER_ID))
                .orderBy(cb.desc(cb.sum(order.get(ORDER_COST))));
        Long userId = entityManager.createQuery(longCriteriaQuery)
                .setMaxResults(ONE_RESULT)
                .getResultList()
                .get(FIRST_ELEMENT);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);
        Join<Certificate, Tag> joinedTag = orderRoot.join(ORDER_JOINED_FIELD_CERTIFICATE)
                .join(CERTIFICATE_JOINED_FIELD_TAGS);
        criteriaQuery.select(joinedTag)
                .groupBy(joinedTag.get(ID))
                .orderBy(cb.desc(cb.count(joinedTag.get(ID))))
                .where(cb.equal(orderRoot.get(ORDER_JOINED_FIELD_USER).get(USER_ID), userId));
        return entityManager.createQuery(criteriaQuery)
                .setMaxResults(ONE_RESULT)
                .getResultList().stream()
                .findFirst();
    }
}
