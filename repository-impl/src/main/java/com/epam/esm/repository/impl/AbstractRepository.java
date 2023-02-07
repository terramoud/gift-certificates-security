package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.AbstractEntity;
import com.epam.esm.repository.api.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public abstract class AbstractRepository<E extends AbstractEntity, N> implements BaseRepository<E, N> {
    protected static final String SORT_REQUEST_PARAM = "sort";
    protected static final String SEARCH_REQUEST_PARAM = "search";
    protected static final String ENTITY_ID = "id";
    protected static final String SQL_LIKE_PATTERN = "%{0}%";

    protected final Class<E> entityClass;
    protected final CriteriaBuilder criteriaBuilder;
    protected final CriteriaQuery<E> criteriaQuery;
    protected final Root<E> root;
    private final Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortOrdersMap;
    private final String[] admittedRequestParams;
    private final Map<String, String> requestParamToEntityFieldName;
    private final String[] fieldsForSearch;

    @PersistenceContext
    protected EntityManager em;

    protected AbstractRepository(Class<E> entityClass,
                                 Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortOrdersMap,
                                 String[] admittedRequestParams,
                                 Map<String, String> requestParamToEntityFieldName,
                                 String[] fieldsForSearch) {
        this.entityClass = entityClass;
        this.sortOrdersMap = sortOrdersMap;
        this.admittedRequestParams = admittedRequestParams;
        this.requestParamToEntityFieldName = requestParamToEntityFieldName;
        this.fieldsForSearch = fieldsForSearch;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
    }

    @Override
    public List<E> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        return findAllByPredicates(fields, pageable);
    }

    @Override
    public Optional<E> findById(N id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public E save(E entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public E update(E entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(E entity) {
        em.remove(entity);
    }

    protected List<E> findAllByPredicates(LinkedMultiValueMap<String, String> requestParams,
                                          Pageable pageable,
                                          Predicate... predicates) {
        fetchLeftJoin(root);
        List<Predicate> filters =
                createFilters(requestParams, criteriaBuilder, root, requestParamToEntityFieldName, fieldsForSearch);
        filters.addAll(Arrays.asList(predicates));
        List<Order> sortParams =
                createSortParams(requestParams, criteriaBuilder, root, sortOrdersMap, admittedRequestParams);
        criteriaQuery.select(root)
                .orderBy(sortParams)
                .where(filters.toArray(Predicate[]::new));
        return executeQuery(criteriaQuery, pageable);
    }

    protected String createLikeQuery(String searchQuery) {
        return MessageFormat.format(SQL_LIKE_PATTERN, searchQuery);
    }

    protected List<E> executeQuery(CriteriaQuery<E> criteriaQuery, Pageable pageable) {
        return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    protected abstract void fetchLeftJoin(Root<E> root);

    protected List<Order> createSortParams(LinkedMultiValueMap<String, String> requestSortParams,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<E> root,
                                           Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortBy,
                                           String... admittedSortParams) {
        String stringSortParams = requestSortParams.getOrDefault(SORT_REQUEST_PARAM, List.of(ENTITY_ID)).get(0);
        if (stringSortParams.isEmpty()) {
            stringSortParams = "+".concat(ENTITY_ID);
        }
        return Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(sortParam -> Arrays.stream(admittedSortParams)
                        .anyMatch(sortParam::startsWith) ? "+".concat(sortParam) : sortParam)
                .distinct()
                .filter(sortBy::containsKey)
                .map(entityField -> sortBy.get(entityField).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
    }

    protected List<Predicate> createFilters(LinkedMultiValueMap<String, String> requestParams,
                                            CriteriaBuilder criteriaBuilder,
                                            Root<E> root,
                                            Map<String, String> requestParamToEntityFieldName,
                                            String... fieldsForSearch) {
        List<Predicate> predicates = requestParams.entrySet()
                .stream()
                .filter(param -> requestParamToEntityFieldName.containsKey(param.getKey()))
                .map(param -> {
                    String entityFieldName = requestParamToEntityFieldName.get(param.getKey());
                    String entityFieldValue = param.getValue().get(0).trim();
                    return criteriaBuilder.equal(root.get(entityFieldName), entityFieldValue);
                })
                .collect(Collectors.toList());
        String searchQuery = requestParams.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
        predicates.add(criteriaBuilder.or(
                Arrays.stream(fieldsForSearch)
                        .map(entityName -> criteriaBuilder.like(root.get(entityName), createLikeQuery(searchQuery)))
                        .toArray(Predicate[]::new)
        ));
        return predicates;
    }
}
