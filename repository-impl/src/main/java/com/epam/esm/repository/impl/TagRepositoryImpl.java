package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.TagRepository;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Repository
@NoArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String FILTER_KEY_NAME = "name";
    public static final String SORT_REQUEST_PARAM = "sort";
    public static final String SEARCH_REQUEST_PARAM = "search";

    private final Map<String, BiFunction<CriteriaBuilder, Root<Tag>, javax.persistence.criteria.Order>> sortBy =
            Map.of("+name", (cb, root) -> cb.asc(root.get(TAG_NAME)),
                    "-name", (cb, root) -> cb.desc(root.get(TAG_NAME)),
                    "+id", (cb, root) -> cb.asc(root.get(TAG_ID)),
                    "-id", (cb, root) -> cb.desc(root.get(TAG_ID)));

    @PersistenceContext
    private EntityManager em;

    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(em.find(Tag.class, id));
    }

    public Tag save(Tag entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(Tag entity) {
        em.remove(entity);
    }

    @Override
    public Optional<Tag> findMostPopularTagOfUserWithHighestCostOfAllOrders() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> longCriteriaQuery = cb.createQuery(Long.class);
        Root<Order> order = longCriteriaQuery.from(Order.class);
        longCriteriaQuery
                .select(order.get("user").get("id"))
                .groupBy(order.get("user").get("id"))
                .orderBy(cb.desc(cb.sum(order.get("cost"))));
        Long userId = em.createQuery(longCriteriaQuery)
                .setMaxResults(1)
                .getResultList()
                .get(0);
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Order> orderRoot = cq.from(Order.class);
        Join<Certificate, Tag> joinedTag = orderRoot.join("certificate")
                .join("tags");
        cq.select(joinedTag)
                .groupBy(joinedTag.get("id"))
                .orderBy(cb.desc(cb.count(joinedTag.get("id"))))
                .where(cb.equal(orderRoot.get("user").get("id"), userId));
        return em.createQuery(cq)
                .setMaxResults(1)
                .getResultList().stream()
                .findFirst();
    }

    public List<Tag> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private List<Predicate> filters(LinkedMultiValueMap<String, String> fields,
                                    CriteriaBuilder criteriaBuilder,
                                    Root<Tag> root) {
        String searchQuery = fields.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(FILTER_KEY_NAME, List.of("")).get(0).trim();
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(TAG_NAME), createLikeQuery(searchQuery)));
        if (!filterByName.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get(TAG_NAME), filterByName));
        }
        return predicates;
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Tag> criteriaQuery,
                      Root<Tag> root) {
        String stringSortParams = fields.getOrDefault(SORT_REQUEST_PARAM, List.of(TAG_ID)).get(0).trim();
        if (stringSortParams.isEmpty()) {
            stringSortParams = "+" .concat(TAG_ID);
        }
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(TAG_NAME) ? "+" .concat(el) : el)
                .map(el -> el.startsWith(TAG_ID) ? "+" .concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<javax.persistence.criteria.Order> orders = sortParams.stream()
                .filter(sortBy::containsKey)
                .map(tagProperty -> sortBy.get(tagProperty).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.orderBy(orders);
    }

    private List<Tag> executeQuery(CriteriaQuery<Tag> criteriaQuery, Pageable pageable) {
        return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
