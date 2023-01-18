package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.AbstractRepository;
import com.epam.esm.repository.api.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TagRepositoryImpl extends AbstractRepository<Tag, Long> implements TagRepository {

    private static final Logger LOG = LogManager.getLogger(TagRepositoryImpl.class);
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";

    private final Map<String, BiFunction<CriteriaBuilder, Root<Tag>, Order>> sortBy = Map.of(
            "+name", (cb, root) -> cb.asc(root.get(TAG_NAME)),
            "-name", (cb, root) -> cb.desc(root.get(TAG_NAME)),
            "+id", (cb, root) -> cb.asc(root.get(TAG_ID)),
            "-id", (cb, root) -> cb.desc(root.get(TAG_ID))
    );

    public TagRepositoryImpl() {
        super(Tag.class);
    }

    @Override
    public List<Tag> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(TAG_NAME, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(TAG_NAME), "%" + searchQuery + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(TAG_NAME), filterByName));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Tag> findAllTagsByCertificateId(LinkedMultiValueMap<String, String> fields, Pageable pageable, Long certificateId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0);
        String filterByName = fields.getOrDefault(TAG_NAME, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(TAG_NAME), "%" + searchQuery.trim() + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(TAG_NAME), filterByName));
        Join<Tag, Certificate> certificatesTags = root.join("certificates_tags");
        predicates.add(criteriaBuilder.equal(certificatesTags.get("certificate_id"), certificateId));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Tag> findAllTagsByCertificateName(LinkedMultiValueMap<String, String> fields, Pageable pageable, String certificateName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0);
        String filterByName = fields.getOrDefault(TAG_NAME, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(TAG_NAME), "%" + searchQuery.trim() + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(TAG_NAME), filterByName));
        Join<Tag, Certificate> certificates = root
                .join("certificates_tags")
                .join("certificates");
        predicates.add( criteriaBuilder.equal(certificates.get("certificates.name"), certificateName));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Tag> criteriaQuery,
                      Root<Tag> root) {
        String stringSortParams = fields.getOrDefault("sort", List.of(TAG_ID)).get(0).trim();
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
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
