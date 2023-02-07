package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.entity.Tag;
import com.epam.esm.repository.api.CertificateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
@Slf4j
public class CertificateRepositoryImpl extends AbstractRepository<Certificate, Long> implements CertificateRepository {

    private static final String CERTIFICATE_ID = "id";
    private static final String CERTIFICATE_NAME = "name";
    private static final String CERTIFICATE_DESCRIPTION = "description";
    private static final String CERTIFICATE_PRICE = "price";
    private static final String CERTIFICATE_DURATION = "duration";
    private static final String CERTIFICATE_CREATE_DATE = "createDate";
    private static final String CERTIFICATE_LAST_UPDATE_DATE = "lastUpdateDate";

    private static final String REQUEST_PARAM_NAME = "name";
    private static final String REQUEST_PARAM_DESCRIPTION = "description";
    private static final String REQUEST_PARAM_PRICE = "price";
    private static final String REQUEST_PARAM_DURATION = "duration";
    private static final String REQUEST_PARAM_CREATE_DATE = "create_date";
    private static final String REQUEST_PARAM_LAST_UPDATE_DATE = "last_update_date";

    private static final String JOINED_FIELD_NAME = "tags";
    private static final String TAG_ID_FIELD = "id";
    private static final String TAG_NAME_FIELD = "name";

    private static final String[] ADMITTED_REQUEST_PARAMS = {
            CERTIFICATE_ID,
            CERTIFICATE_NAME,
            CERTIFICATE_DESCRIPTION,
            CERTIFICATE_PRICE,
            CERTIFICATE_DURATION,
            CERTIFICATE_CREATE_DATE,
            CERTIFICATE_LAST_UPDATE_DATE};

    private static final String[] FIELDS_FOR_SEARCH = {CERTIFICATE_NAME, CERTIFICATE_DESCRIPTION,};

    private static final Map<String, BiFunction<CriteriaBuilder, Root<Certificate>, Order>> SORT_ORDERS_MAP =
            Map.ofEntries(
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

    private static final Map<String, String> REQUEST_PARAM_TO_ENTITY_FIELD_NAME = Map.of(
            REQUEST_PARAM_NAME, CERTIFICATE_NAME,
            REQUEST_PARAM_DESCRIPTION, CERTIFICATE_DESCRIPTION,
            REQUEST_PARAM_PRICE, CERTIFICATE_PRICE,
            REQUEST_PARAM_DURATION, CERTIFICATE_DURATION,
            REQUEST_PARAM_CREATE_DATE, CERTIFICATE_CREATE_DATE,
            REQUEST_PARAM_LAST_UPDATE_DATE, CERTIFICATE_LAST_UPDATE_DATE
    );

    public CertificateRepositoryImpl() {
        super(Certificate.class,
                SORT_ORDERS_MAP,
                ADMITTED_REQUEST_PARAMS,
                REQUEST_PARAM_TO_ENTITY_FIELD_NAME,
                FIELDS_FOR_SEARCH);
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        try {
            fetchLeftJoin(root);
            Predicate predicate = criteriaBuilder.equal(root.get(CERTIFICATE_ID), id);
            criteriaQuery.where(predicate);
            Certificate certificate = em.createQuery(criteriaQuery).getSingleResult();
            return Optional.ofNullable(certificate);
        } catch (EmptyResultDataAccessException | PersistenceException ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Certificate> findAllByTagId(LinkedMultiValueMap<String, String> fields,
                                            Pageable pageable,
                                            Long tagId) {
        Join<Certificate, Tag> joinedTags = root.join(JOINED_FIELD_NAME);
        Predicate predicate = criteriaBuilder.equal(joinedTags.get(TAG_ID_FIELD), tagId);
        return findAllByPredicates(fields, pageable, predicate);
    }

    @Override
    public List<Certificate> findAllByTagName(LinkedMultiValueMap<String, String> fields,
                                              Pageable pageable,
                                              String tagName) {
        Join<Certificate, Tag> joinedTags = root.join(JOINED_FIELD_NAME);
        Predicate predicate = criteriaBuilder.equal(joinedTags.get(TAG_NAME_FIELD), tagName);
        return findAllByPredicates(fields, pageable, predicate);
    }

//    @Override
//    protected List<Predicate> createFilters(LinkedMultiValueMap<String, String> requestParams,
//                                            CriteriaBuilder criteriaBuilder,
//                                            Root<Certificate> root,
//                                            HashMap<Object, Object> COLUMN_FIELD_MAP, String... fieldsForSearch) {
//        List<Predicate> predicates = new ArrayList<>();
//        String searchQuery = requestParams.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
//        predicates.add(criteriaBuilder.or(
//                Arrays.stream(fieldsForSearch)
//                    .map(entityName -> criteriaBuilder.like(root.get(entityName), createLikeQuery(searchQuery)))
//                .toArray(Predicate[]::new)
//        ));
//        requestParams.entrySet().stream()
//                .filter(param -> CertificateRepositoryImpl.COLUMN_FIELD_MAP.containsKey(param.getKey()))
//                .forEach(param -> {
//                    String entityFieldName = CertificateRepositoryImpl.COLUMN_FIELD_MAP.get(param.getKey());
//                    String entityFieldValue = param.getValue().get(0).trim();
//                    predicates.add(criteriaBuilder.equal(root.get(entityFieldName), entityFieldValue));
//                });
//        return predicates;



//        requestParams.forEach((paramName, paramValue) -> {
//            if (listAdmittedFilter.contains(paramName)) {
//                predicates.add(map.get(paramName).apply(paramValue.get(0).trim()));
//            }
//        });

//        Arrays.stream(admittedFilterNames)
//                .forEach(f -> {
//                    String entityFieldValue = requestParams.getOrDefault(f, List.of("")).get(0).trim();
//                    if (!entityFieldValue.isEmpty()) {
//                        predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), entityFieldValue));
//                    }
//                });


//        String filterByName = requestParams.getOrDefault(COLUMN_NAME, List.of("")).get(0).trim();
//        String filterByDescription = requestParams.getOrDefault(COLUMN_DESCRIPTION, List.of("")).get(0).trim();
//        String filterByPrice = requestParams.getOrDefault(COLUMN_PRICE, List.of("")).get(0).trim();
//        String filterByDuration = requestParams.getOrDefault(COLUMN_DURATION, List.of("")).get(0).trim();
//        String filterByCreateDate = requestParams.getOrDefault(COLUMN_CREATE_DATE, List.of("")).get(0).trim();
//        String filterByLastUpdateDate = requestParams.getOrDefault(COLUMN_LAST_UPDATE_DATE, List.of("")).get(0).trim();
////        predicates = new ArrayList<>();
//
//        if (!filterByName.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_NAME), filterByName));
//        }
//        if (!filterByDescription.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DESCRIPTION), filterByDescription));
//        }
//        if (!filterByPrice.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_PRICE), filterByPrice));
//        }
//        if (!filterByDuration.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_DURATION), filterByDuration));
//        }
//        if (!filterByCreateDate.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_CREATE_DATE), filterByCreateDate));
//        }
//        if (!filterByLastUpdateDate.isEmpty()) {
//            predicates.add(criteriaBuilder.equal(root.get(CERTIFICATE_LAST_UPDATE_DATE), filterByLastUpdateDate));
//        }
//    }

    @Override
    protected void fetchLeftJoin(Root<Certificate> root) {
        root.fetch(JOINED_FIELD_NAME, JoinType.LEFT);
    }
}