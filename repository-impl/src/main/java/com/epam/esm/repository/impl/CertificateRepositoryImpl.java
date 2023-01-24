package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.CertificateRepository;
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
public class CertificateRepositoryImpl implements CertificateRepository {

    private static final Logger LOG = LogManager.getLogger(CertificateRepositoryImpl.class);
    public static final String CERTIFICATE_ID = "id";
    public static final String CERTIFICATE_NAME = "name";
    public static final String CERTIFICATE_DESCRIPTION = "description";
    public static final String CERTIFICATE_PRICE = "price";
    public static final String CERTIFICATE_DURATION = "duration";
    public static final String CERTIFICATE_CREATE_DATE = "createDate";
    public static final String CERTIFICATE_LAST_UPDATE_DATE = "lastUpdateDate";

    public static final String SORT_REQUEST_PARAM = "sort";
    public static final String SEARCH_REQUEST_PARAM = "search";
    public static final String FILTER_BY_ID = "id";
    public static final String FILTER_BY_NAME = "name";
    public static final String FILTER_BY_DESCRIPTION = "description";
    public static final String FILTER_BY_PRICE = "price";
    public static final String FILTER_BY_DURATION = "duration";
    public static final String FILTER_BY_CREATE_DATE = "create_date";
    public static final String FILTER_BY_LAST_UPDATE_DATE = "last_update_date";

    private final Map<String, BiFunction<CriteriaBuilder, Root<Certificate>, Order>> sortBy = Map.ofEntries(
            Map.entry("+id", (cb, root) -> cb.asc(root.get(CERTIFICATE_ID))),
            Map.entry("-id", (cb, root) -> cb.desc(root.get(CERTIFICATE_ID))),
            Map.entry("+name", (cb, root) -> cb.asc(root.get(CERTIFICATE_NAME))),
            Map.entry("-name", (cb, root) -> cb.desc(root.get(CERTIFICATE_NAME))),
            Map.entry("+description", (cb, root) -> cb.asc(root.get(CERTIFICATE_DESCRIPTION))),
            Map.entry("-description", (cb, root) -> cb.desc(root.get(CERTIFICATE_DESCRIPTION))),
            Map.entry("+price", (cb, root) -> cb.asc(root.get(CERTIFICATE_PRICE))),
            Map.entry("-price", (cb, root) -> cb.desc(root.get(CERTIFICATE_PRICE))),
            Map.entry("+duration", (cb, root) -> cb.asc(root.get(CERTIFICATE_DURATION))),
            Map.entry("-duration", (cb, root) -> cb.desc(root.get(CERTIFICATE_DURATION))),
            Map.entry("+create_date", (cb, root) -> cb.asc(root.get(CERTIFICATE_CREATE_DATE))),
            Map.entry("-create_date", (cb, root) -> cb.desc(root.get(CERTIFICATE_CREATE_DATE))),
            Map.entry("+last_update_date", (cb, root) -> cb.asc(root.get(CERTIFICATE_LAST_UPDATE_DATE))),
            Map.entry("-last_update_date", (cb, root) -> cb.desc(root.get(CERTIFICATE_LAST_UPDATE_DATE)))
    );

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Certificate> findById(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        root.fetch("tags", JoinType.LEFT);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        return Optional.ofNullable(em.createQuery(criteriaQuery).getSingleResult());
    }

    @Override
    public Certificate save(Certificate entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public Certificate update(Certificate entity, Long id) {
        return em.merge(entity);
    }

    @Override
    public void delete(Certificate entity) {
        em.remove(entity);
    }

    @Override
    public List<Certificate> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        root.fetch("tags", JoinType.LEFT);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Certificate> findAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields,
                                                        Pageable pageable,
                                                        Long tagId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        root.fetch("tags", JoinType.LEFT);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        Join<Certificate, Tag> certificatesTags = root.join("tags");
        predicates.add(criteriaBuilder.equal(certificatesTags.get("id"), tagId));
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Certificate> findAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields,
                                                          Pageable pageable,
                                                          String tagName) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        root.fetch("tags", JoinType.LEFT);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        Join<Certificate, Tag> certificatesTags = root.join("tags");
        predicates.add(criteriaBuilder.equal(certificatesTags.get("name"), tagName));
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private List<Predicate> filters(LinkedMultiValueMap<String, String> fields,
                                    CriteriaBuilder criteriaBuilder,
                                    Root<Certificate> root) {
        String searchQuery = fields.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(FILTER_BY_NAME, List.of("")).get(0).trim();
        String filterByDescription = fields.getOrDefault(FILTER_BY_DESCRIPTION, List.of("")).get(0).trim();
        String filterByPrice = fields.getOrDefault(FILTER_BY_PRICE, List.of("")).get(0).trim();
        String filterByDuration = fields.getOrDefault(FILTER_BY_DURATION, List.of("")).get(0).trim();
        String filterByCreateDate = fields.getOrDefault(FILTER_BY_CREATE_DATE, List.of("")).get(0).trim();
        String filterByLastUpdateDate = fields.getOrDefault(FILTER_BY_LAST_UPDATE_DATE, List.of("")).get(0).trim();
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.or(
                criteriaBuilder.like(root.get(CERTIFICATE_NAME), "%" + searchQuery + "%"),
                criteriaBuilder.like(root.get(CERTIFICATE_DESCRIPTION), "%" + searchQuery + "%")
        ));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), filterByName));
        if (!filterByDescription.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDescription));
        if (!filterByPrice.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_PRICE), filterByPrice));
        if (!filterByDuration.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DURATION), filterByDuration));
        if (!filterByCreateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_CREATE_DATE), filterByCreateDate));
        if (!filterByLastUpdateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_LAST_UPDATE_DATE), filterByLastUpdateDate));
        return predicates;
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Certificate> criteriaQuery,
                      Root<Certificate> root) {
        String stringSortParams = fields.getOrDefault(SORT_REQUEST_PARAM, List.of(FILTER_BY_ID)).get(0);
        if (stringSortParams.isEmpty()) stringSortParams = "+".concat(FILTER_BY_ID);
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(FILTER_BY_ID) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_NAME) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_DESCRIPTION) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_PRICE) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_DURATION) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_CREATE_DATE) ? "+".concat(el) : el)
                .map(el -> el.startsWith(FILTER_BY_LAST_UPDATE_DATE) ? "+".concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<Order> orders = sortParams.stream()
                .filter(sortBy::containsKey)
                .map(certificateProperty -> sortBy.get(certificateProperty).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.orderBy(orders);
    }

    private List<Certificate> executeQuery(CriteriaQuery<Certificate> criteriaQuery, Pageable pageable) {
               return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}