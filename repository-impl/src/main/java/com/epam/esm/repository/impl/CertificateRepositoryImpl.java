package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.AbstractRepository;
import com.epam.esm.repository.api.CertificateRepository;
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
public class CertificateRepositoryImpl extends AbstractRepository<Certificate, Long> implements CertificateRepository {

    private static final Logger LOG = LogManager.getLogger(CertificateRepositoryImpl.class);
    public static final String CERTIFICATE_ID = "id";
    public static final String CERTIFICATE_NAME = "name";
    public static final String CERTIFICATE_DESCRIPTION = "description";
    public static final String CERTIFICATE_PRICE = "price";
    public static final String CERTIFICATE_DURATION = "duration";
    public static final String CERTIFICATE_CREATE_DATE = "create_date";
    public static final String CERTIFICATE_LAST_UPDATE_DATE = "last_update_date";

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

    public CertificateRepositoryImpl() {
        super(Certificate.class);
    }

    @Override
    public List<Certificate> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(CERTIFICATE_NAME, List.of("")).get(0).trim();
        String filterByDescription = fields.getOrDefault(CERTIFICATE_DESCRIPTION, List.of("")).get(0).trim();
        String filterByPrice = fields.getOrDefault(CERTIFICATE_PRICE, List.of("")).get(0).trim();
        String filterByDuration = fields.getOrDefault(CERTIFICATE_DURATION, List.of("")).get(0).trim();
        String filterByCreateDate = fields.getOrDefault(CERTIFICATE_CREATE_DATE, List.of("")).get(0).trim();
        String filterByLastUpdateDate = fields.getOrDefault(CERTIFICATE_LAST_UPDATE_DATE, List.of("")).get(0).trim();
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(CERTIFICATE_NAME), "%" + searchQuery + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), filterByName));
        if (!filterByDescription.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDescription));
        if (!filterByPrice.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByPrice));
        if (!filterByDuration.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDuration));
        if (!filterByCreateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByCreateDate));
        if (!filterByLastUpdateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByLastUpdateDate));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Certificate> findAllCertificatesByTagId(LinkedMultiValueMap<String, String> fields, Pageable pageable, Long tagId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(CERTIFICATE_NAME, List.of("")).get(0).trim();
        String filterByDescription = fields.getOrDefault(CERTIFICATE_DESCRIPTION, List.of("")).get(0).trim();
        String filterByPrice = fields.getOrDefault(CERTIFICATE_PRICE, List.of("")).get(0).trim();
        String filterByDuration = fields.getOrDefault(CERTIFICATE_DURATION, List.of("")).get(0).trim();
        String filterByCreateDate = fields.getOrDefault(CERTIFICATE_CREATE_DATE, List.of("")).get(0).trim();
        String filterByLastUpdateDate = fields.getOrDefault(CERTIFICATE_LAST_UPDATE_DATE, List.of("")).get(0).trim();
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(CERTIFICATE_NAME), "%" + searchQuery + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), filterByName));
        if (!filterByDescription.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDescription));
        if (!filterByPrice.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByPrice));
        if (!filterByDuration.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDuration));
        if (!filterByCreateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByCreateDate));
        if (!filterByLastUpdateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByLastUpdateDate));
        Join<Certificate, Tag> certificatesTags = root.join("certificates_tags");
        predicates.add(criteriaBuilder.equal(certificatesTags.get("tag_id"), tagId));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    @Override
    public List<Certificate> findAllCertificatesByTagName(LinkedMultiValueMap<String, String> fields, Pageable pageable,
                                                          String tagName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        String searchQuery = fields.getOrDefault("search", List.of("")).get(0).trim();
        String filterByName = fields.getOrDefault(CERTIFICATE_NAME, List.of("")).get(0).trim();
        String filterByDescription = fields.getOrDefault(CERTIFICATE_DESCRIPTION, List.of("")).get(0).trim();
        String filterByPrice = fields.getOrDefault(CERTIFICATE_PRICE, List.of("")).get(0).trim();
        String filterByDuration = fields.getOrDefault(CERTIFICATE_DURATION, List.of("")).get(0).trim();
        String filterByCreateDate = fields.getOrDefault(CERTIFICATE_CREATE_DATE, List.of("")).get(0).trim();
        String filterByLastUpdateDate = fields.getOrDefault(CERTIFICATE_LAST_UPDATE_DATE, List.of("")).get(0).trim();
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(CERTIFICATE_NAME), "%" + searchQuery + "%"));
        if (!filterByName.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), filterByName));
        if (!filterByDescription.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDescription));
        if (!filterByPrice.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByPrice));
        if (!filterByDuration.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDuration));
        if (!filterByCreateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByCreateDate));
        if (!filterByLastUpdateDate.isEmpty())
            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByLastUpdateDate));
        Join<Certificate, Tag> certificates = root
                .join("certificates_tags")
                .join("tags");
        predicates.add( criteriaBuilder.equal(certificates.get("tags.name"), tagName));
        criteriaQuery.select(root).where(predicates.toArray(Predicate[]::new));
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return executeQuery(criteriaQuery, pageable);
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<Certificate> criteriaQuery,
                      Root<Certificate> root) {
        String stringSortParams = fields.getOrDefault("sort", List.of(CERTIFICATE_ID)).get(0);
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(CERTIFICATE_ID) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_NAME) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_DESCRIPTION) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_PRICE) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_DURATION) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_CREATE_DATE) ? "+".concat(el) : el)
                .map(el -> el.startsWith(CERTIFICATE_LAST_UPDATE_DATE) ? "+".concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<Order> orders = sortParams.stream()
                .filter(sortBy::containsKey)
                .map(tagProperty -> sortBy.get(tagProperty).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.orderBy(orders);
    }


    private List<Certificate> executeQuery(CriteriaQuery<Certificate> criteriaQuery, Pageable pageable) {
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}