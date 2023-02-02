package com.epam.esm.repository.impl;


import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Repository
@Transactional
@NoArgsConstructor
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

    private static final String ORDER_ID = "id";
    private static final String ORDER_COST = "cost";
    private static final String ORDER_CREATE_DATE = "createDate";
    private static final String SORT_REQUEST_PARAM = "sort";
    private static final String PARAM_ID = "id";
    private static final String PARAM_COST = "cost";
    private static final String PARAM_CREATE_DATE = "create_date";

    private static final String JOINED_FIELD_CERTIFICATE = "certificate";
    private static final String JOINED_FIELD_USER = "user";
    private static final String CERTIFICATE_JOINED_FIELD_TAGS = "tags";
    private static final String USER_ID = "id";

    private final Map<String, BiFunction<CriteriaBuilder, Root<Order>, javax.persistence.criteria.Order>> sortBy =
            Map.of(
            "+create_date", (cb, root) -> cb.asc(root.get(ORDER_CREATE_DATE)),
            "-create_date", (cb, root) -> cb.desc(root.get(ORDER_CREATE_DATE)),
            "+cost", (cb, root) -> cb.asc(root.get(ORDER_COST)),
            "-cost", (cb, root) -> cb.desc(root.get(ORDER_COST)),
            "+id", (cb, root) -> cb.asc(root.get(ORDER_ID)),
            "-id", (cb, root) -> cb.desc(root.get(ORDER_ID))
            );

    @PersistenceContext
    private EntityManager em;

    public Optional<Order> findById(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
            Root<Order> root = criteriaQuery.from(Order.class);
            root.fetch(JOINED_FIELD_CERTIFICATE, JoinType.LEFT)
                    .fetch(CERTIFICATE_JOINED_FIELD_TAGS, JoinType.LEFT);
            criteriaQuery.where(criteriaBuilder.equal(root.get(ORDER_ID), id));
            return Optional.ofNullable(em.createQuery(criteriaQuery).getSingleResult());
        } catch (EmptyResultDataAccessException | PersistenceException ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public Order save(Order entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public Order insert(Order order) {
        Order savedOrder = save(order);
        return update(savedOrder, savedOrder.getId());
    }

    @Override
    public Order update(Order entity, Long id) {
        return em.merge(entity);
    }

    @Override
    public void delete(Order entity) {
        em.remove(entity);
    }

    public List<Order> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = criteriaQuery.from(Order.class);
        root.fetch(JOINED_FIELD_CERTIFICATE, JoinType.LEFT)
                .fetch(CERTIFICATE_JOINED_FIELD_TAGS, JoinType.LEFT);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Order> findAllOrdersByUserId(LinkedMultiValueMap<String, String> fields,
                                             Pageable pageable,
                                             Long userId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = criteriaQuery.from(Order.class);
        root.fetch(JOINED_FIELD_CERTIFICATE, JoinType.LEFT)
                .fetch(CERTIFICATE_JOINED_FIELD_TAGS, JoinType.LEFT);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        Join<Order, User> userJoin = root.join(JOINED_FIELD_USER);
        predicates.add(criteriaBuilder.equal(userJoin.get(USER_ID), userId));
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private List<Predicate> filters(LinkedMultiValueMap<String, String> fields,
                                    CriteriaBuilder criteriaBuilder,
                                    Root<Order> root) {
        String filterByCost = fields.getOrDefault(PARAM_COST, List.of("")).get(0);
        String filterByCreateDate = fields.getOrDefault(PARAM_CREATE_DATE, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        if (!filterByCost.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get(ORDER_COST), filterByCost));
        }
        if (!filterByCreateDate.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get(ORDER_CREATE_DATE), filterByCreateDate));
        }
        return predicates;
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Order> criteriaQuery,
                      Root<Order> root) {
        String stringSortParams = fields.getOrDefault(SORT_REQUEST_PARAM, List.of(PARAM_ID)).get(0).trim();
        if (stringSortParams.isEmpty()) {
            stringSortParams = "+".concat(PARAM_ID);
        }
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(PARAM_ID) ? "+".concat(el) : el)
                .map(el -> el.startsWith(PARAM_COST) ? "+".concat(el) : el)
                .map(el -> el.startsWith(PARAM_CREATE_DATE) ? "+".concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<javax.persistence.criteria.Order> orders = sortParams.stream()
                .filter(sortBy::containsKey)
                .map(orderProperty -> sortBy.get(orderProperty).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.orderBy(orders);
    }

    private List<Order> executeQuery(CriteriaQuery<Order> criteriaQuery, Pageable pageable) {
        return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
