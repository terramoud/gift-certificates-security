package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.TagRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class TagRepositoryImpl extends AbstractRepository<Tag, Long> implements TagRepository {

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String REQUEST_PARAM_NAME = "name";
    private static final String ORDER_FIELD_USER = "user";
    private static final String USER_ID = "id";
    private static final String ORDER_COST = "cost";
    private static final String ORDER_FIELD_CERTIFICATE = "certificate";
    private static final String CERTIFICATE_FIELD_TAGS = "tags";
    private static final String[] ADMITTED_SORT_PARAMS = {REQUEST_PARAM_NAME};
    private static final String[] FIELDS_FOR_SEARCH = {TAG_NAME};

    private static final Map<String, BiFunction<CriteriaBuilder, Root<Tag>, Order>> SORT_ORDERS_MAP = Map.of(
            "+name", (cb, root) -> cb.asc(root.get(TAG_NAME)),
            "-name", (cb, root) -> cb.desc(root.get(TAG_NAME)),
            "+id", (cb, root) -> cb.asc(root.get(TAG_ID)),
            "-id", (cb, root) -> cb.desc(root.get(TAG_ID))
    );

    private static final Map<String, String> REQUEST_PARAM_TO_ENTITY_FIELD_NAME = Map.of(
            REQUEST_PARAM_NAME, TAG_NAME
    );

    public TagRepositoryImpl() {
        super(Tag.class,
                SORT_ORDERS_MAP,
                ADMITTED_SORT_PARAMS,
                REQUEST_PARAM_TO_ENTITY_FIELD_NAME,
                FIELDS_FOR_SEARCH);
    }

    @Override
    public Optional<Tag> findMostPopularTagOfUserWithHighestCostOfAllOrders() {
        CriteriaQuery<Long> longCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Order> order = longCriteriaQuery.from(Order.class);
        longCriteriaQuery
                .select(order.get(ORDER_FIELD_USER).get(USER_ID))
                .groupBy(order.get(ORDER_FIELD_USER).get(USER_ID))
                .orderBy(criteriaBuilder.desc(criteriaBuilder.sum(order.get(ORDER_COST))));
        Long userId = em.createQuery(longCriteriaQuery)
                .setMaxResults(1)
                .getResultList()
                .get(0);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);
        Join<Certificate, Tag> joinedTag = orderRoot.join(ORDER_FIELD_CERTIFICATE)
                .join(CERTIFICATE_FIELD_TAGS);
        criteriaQuery.select(joinedTag)
                .groupBy(joinedTag.get(TAG_ID))
                .orderBy(criteriaBuilder.desc(criteriaBuilder.count(joinedTag.get(TAG_ID))))
                .where(criteriaBuilder.equal(orderRoot.get(ORDER_FIELD_USER).get(USER_ID), userId));
        return em.createQuery(criteriaQuery)
                .setMaxResults(1)
                .getResultList().stream()
                .findFirst();
    }

    @Override
    protected void fetchLeftJoin(Root<Tag> root) {
        throw new UnsupportedOperationException();
    }
}
