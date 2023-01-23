package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TagRepositoryImpl implements TagRepository {

    private static final Logger LOG = LogManager.getLogger(TagRepositoryImpl.class);
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String FILTER_KEY_NAME = "name";
    public static final String SORT_REQUEST_PARAM = "sort";
    public static final String SEARCH_REQUEST_PARAM = "search";

    private final Map<String, BiFunction<CriteriaBuilder, Root<Tag>, Order>> sortBy = Map.of(
            "+name", (cb, root) -> cb.asc(root.get(TAG_NAME)),
            "-name", (cb, root) -> cb.desc(root.get(TAG_NAME)),
            "+id", (cb, root) -> cb.asc(root.get(TAG_ID)),
            "-id", (cb, root) -> cb.desc(root.get(TAG_ID))
    );

    @PersistenceContext
    private EntityManager em;

    public TagRepositoryImpl() {

    }

    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(em.find(Tag.class, id));
    }

    public Tag save(Tag entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity, Long id) {
        return em.merge(entity);
    }

    @Override
    public void delete(Tag entity) {
        em.remove(entity);
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

    public List<Tag> findAllTagsByCertificateId(LinkedMultiValueMap<String, String> fields,
                                                Pageable pageable,
                                                Long certificateId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        Join<Tag, Certificate> certificatesTags = root.join("certificates");
        predicates.add(criteriaBuilder.equal(certificatesTags.get("id"), certificateId));
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    public List<Tag> findAllTagsByCertificateName(LinkedMultiValueMap<String, String> fields,
                                                  Pageable pageable,
                                                  String certificateName) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        Join<Tag, Certificate> certificates = root.join("certificates");
        predicates.add(criteriaBuilder.equal(certificates.get("name"), certificateName));
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private List<Predicate> filters(LinkedMultiValueMap<String, String> fields,
                                    CriteriaBuilder criteriaBuilder,
                                    Root<Tag> root) {
        String searchQuery = fields.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0);
        String filterByName = fields.getOrDefault(FILTER_KEY_NAME, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(TAG_NAME), "%" + searchQuery.trim() + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(TAG_NAME), filterByName));
        return predicates;
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Tag> criteriaQuery,
                      Root<Tag> root) {
        String stringSortParams = fields.getOrDefault(SORT_REQUEST_PARAM, List.of(TAG_ID)).get(0).trim();
        if (stringSortParams.isEmpty()) stringSortParams = "+".concat(TAG_ID);
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(TAG_NAME) ? "+".concat(el) : el)
                .map(el -> el.startsWith(TAG_ID) ? "+".concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<Order> orders = sortParams.stream()
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
