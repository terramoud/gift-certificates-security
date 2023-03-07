package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.domain.utils.TriFunction;
import com.epam.esm.repository.api.CertificateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CertificateRepositoryImpl extends AbstractRepository<Certificate, Long> implements CertificateRepository {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String CREATE_DATE = "createDate";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String JOINED_FIELD_TAGS = "tags";
    private static final String TAG_ID_FIELD = "id";
    private static final String TAG_NAME_FIELD = "name";
    private static final String[] FIELDS_FOR_SEARCH = {NAME, DESCRIPTION};

    private static final Map<String, BiFunction<CriteriaBuilder, Root<Certificate>, Order>> SORT_ORDERS_MAP =
            Map.ofEntries(
                    Map.entry("+id", (cb, root) -> cb.asc(root.get(ID))),
                    Map.entry("-id", (cb, root) -> cb.desc(root.get(ID))),
                    Map.entry("+name", (cb, root) -> cb.asc(root.get(NAME))),
                    Map.entry("-name", (cb, root) -> cb.desc(root.get(NAME))),
                    Map.entry("+description", (cb, root) -> cb.asc(root.get(DESCRIPTION))),
                    Map.entry("-description", (cb, root) -> cb.desc(root.get(DESCRIPTION))),
                    Map.entry("+price", (cb, root) -> cb.asc(root.get(PRICE))),
                    Map.entry("-price", (cb, root) -> cb.desc(root.get(PRICE))),
                    Map.entry("+duration", (cb, root) -> cb.asc(root.get(DURATION))),
                    Map.entry("-duration", (cb, root) -> cb.desc(root.get(DURATION))),
                    Map.entry("+createDate", (cb, root) -> cb.asc(root.get(CREATE_DATE))),
                    Map.entry("-createDate", (cb, root) -> cb.desc(root.get(CREATE_DATE))),
                    Map.entry("+lastUpdateDate", (cb, root) -> cb.asc(root.get(LAST_UPDATE_DATE))),
                    Map.entry("-lastUpdateDate", (cb, root) -> cb.desc(root.get(LAST_UPDATE_DATE)))
            );

    private static final Map<String,
            TriFunction<CriteriaBuilder, Root<Certificate>, String, Predicate>> FIELDS_TO_FILTERS_MAP =
            Map.ofEntries(
                    Map.entry(NAME, (cb, r, filterValue) -> cb.equal(r.get(NAME), filterValue)),
                    Map.entry(DESCRIPTION, (cb, r, filterValue) -> cb.equal(r.get(DESCRIPTION), filterValue)),
                    Map.entry(PRICE, (cb, r, filterValue) -> cb.equal(r.get(PRICE), filterValue)),
                    Map.entry(DURATION, (cb, r, filterValue) -> cb.equal(r.get(DURATION), filterValue)),
                    Map.entry(CREATE_DATE, (cb, r, filterValue) -> cb.equal(r.get(CREATE_DATE), filterValue)),
                    Map.entry(LAST_UPDATE_DATE, (cb, r, filterValue) -> cb.equal(r.get(LAST_UPDATE_DATE), filterValue))
            );

    private final EntityManager entityManager;

    @Autowired
    public CertificateRepositoryImpl(EntityManager em) {
        super(Certificate.class, em, SORT_ORDERS_MAP, FIELDS_TO_FILTERS_MAP, FIELDS_FOR_SEARCH);
        this.entityManager = em;
        root.fetch(JOINED_FIELD_TAGS, JoinType.LEFT);
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        try {
            Predicate predicate = cb.equal(root.get(ID), id);
            criteriaQuery.where(predicate);
            Certificate certificate = entityManager.createQuery(criteriaQuery).getSingleResult();
            return Optional.ofNullable(certificate);
        } catch (EmptyResultDataAccessException | PersistenceException ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

//    @Override
//    public List<Certificate> findAllByTagId(LinkedMultiValueMap<String, String> fields,
//                                            Pageable pageable,
//                                            Long tagId) {
//        Join<Certificate, Tag> joinedTags = root.join(JOINED_FIELD_TAGS);
//        Predicate predicate = cb.equal(joinedTags.get(TAG_ID_FIELD), tagId);
//        return findAllByPredicates(fields, pageable, predicate);
//    }

    @Override
    public List<Certificate> findAllByTagId(LinkedMultiValueMap<String, String> fields,
                                            Pageable pageable,
                                            Long tagId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> query = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> certificateRoot = query.from(Certificate.class);
        Join<Certificate, Tag> joinedTags = certificateRoot.join(JOINED_FIELD_TAGS);

        List<Predicate> predicateList = createFilters(fields, criteriaBuilder, certificateRoot, FIELDS_TO_FILTERS_MAP);
        Predicate[] searchPredicates = createSearchQuery(fields, criteriaBuilder, certificateRoot, FIELDS_FOR_SEARCH);
        if (searchPredicates.length != 0) {
            predicateList.add(criteriaBuilder.or(searchPredicates));
        }

        predicateList.add(criteriaBuilder.equal(joinedTags.get(TAG_ID_FIELD), tagId));
        List<Order> sortParams = createSortParams(fields, criteriaBuilder, certificateRoot, SORT_ORDERS_MAP);
        query.select(certificateRoot)
                .where(predicateList.toArray(Predicate[]::new))
                .orderBy(sortParams);
        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public List<Certificate> findAllByTagName(LinkedMultiValueMap<String, String> fields,
                                              Pageable pageable,
                                              String tagName) {
//        Join<Certificate, Tag> joinedTags = root.join(JOINED_FIELD_TAGS);
//        Predicate predicate = cb.equal(joinedTags.get(TAG_NAME_FIELD), tagName);
//        return findAllByPredicates(fields, pageable, predicate);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> query = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> certificateRoot = query.from(Certificate.class);
        Join<Certificate, Tag> joinedTags = certificateRoot.join(JOINED_FIELD_TAGS);

        List<Predicate> predicateList = createFilters(fields, criteriaBuilder, certificateRoot, FIELDS_TO_FILTERS_MAP);
        Predicate[] searchPredicates = createSearchQuery(fields, criteriaBuilder, certificateRoot, FIELDS_FOR_SEARCH);
        if (searchPredicates.length != 0) {
            predicateList.add(criteriaBuilder.or(searchPredicates));
        }

        predicateList.add(criteriaBuilder.equal(joinedTags.get(TAG_NAME_FIELD), tagName));
        List<Order> sortParams = createSortParams(fields, criteriaBuilder, certificateRoot, SORT_ORDERS_MAP);
        query.select(certificateRoot)
                .where(predicateList.toArray(Predicate[]::new))
                .orderBy(sortParams);
        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}