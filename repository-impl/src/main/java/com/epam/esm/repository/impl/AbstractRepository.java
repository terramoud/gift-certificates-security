package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.AbstractEntity;
import com.epam.esm.domain.utils.TriFunction;
import com.epam.esm.repository.api.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractRepository<E extends AbstractEntity, N> implements BaseRepository<E, N> {
    protected static final String SORT_REQUEST_PARAM = "sort";
    protected static final String SEARCH_REQUEST_PARAM = "search";
    protected static final String ASC_BY_ID = "+id";
    protected static final String SQL_LIKE_PATTERN = "%{0}%";

    protected final CriteriaBuilder cb;
    protected final CriteriaQuery<E> criteriaQuery;
    protected final Root<E> root;
    private final Class<E> entityClass;
    private final EntityManager em;
    private final Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortOrdersMap;
    private final Map<String, TriFunction<CriteriaBuilder, Root<E>, String, Predicate>> fieldsToFiltersMap;
    private final String[] fieldsForSearch;

    protected AbstractRepository(Class<E> entityClass,
                                 EntityManager em,
                                 Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortOrdersMap,
                                 Map<String, TriFunction<CriteriaBuilder, Root<E>, String, Predicate>> filterMap,
                                 String[] fieldsForSearch) {
        this.entityClass = entityClass;
        this.em = em;
        this.sortOrdersMap = sortOrdersMap;
        this.fieldsToFiltersMap = filterMap;
        this.fieldsForSearch = fieldsForSearch;
        this.cb = this.em.getCriteriaBuilder();
        this.criteriaQuery = cb.createQuery(entityClass);
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
        List<Predicate> predicateList = createFilters(requestParams, cb, root, fieldsToFiltersMap);
        Predicate[] searchPredicates = createSearchQuery(requestParams, cb, root, fieldsForSearch);
        if (searchPredicates.length != 0) {
            predicateList.add(cb.or(searchPredicates));
        }
        if (predicates.length != 0) {
            predicateList.addAll(Arrays.asList(predicates));
        }
        List<Order> sortParams = createSortParams(requestParams, cb, root, sortOrdersMap);
        criteriaQuery.select(root)
                .where(predicateList.toArray(Predicate[]::new))
                .orderBy(sortParams);
        return executeQuery(criteriaQuery, pageable);
    }

    protected List<E> executeQuery(CriteriaQuery<E> criteriaQuery, Pageable pageable) {
        return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    protected List<Order> createSortParams(LinkedMultiValueMap<String, String> requestSortParams,
                                         CriteriaBuilder criteriaBuilder,
                                         Root<E> root,
                                         Map<String, BiFunction<CriteriaBuilder, Root<E>, Order>> sortBy) {
        String sortParams = requestSortParams.getOrDefault(SORT_REQUEST_PARAM, List.of(ASC_BY_ID)).get(0);
        if (sortParams.isEmpty()) {
            sortParams = ASC_BY_ID;
        }
        return Arrays.stream(sortParams.split(","))
                .map(String::trim)
                .distinct()
                .filter(sortBy::containsKey)
                .map(entityField -> sortBy.get(entityField).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
    }

    protected List<Predicate> createFilters(LinkedMultiValueMap<String, String> requestParams,
                                          CriteriaBuilder criteriaBuilder,
                                          Root<E> root,
                                          Map<String, TriFunction<CriteriaBuilder,
                                                  Root<E>, String, Predicate>> filterBy) {
        return requestParams.entrySet()
                .stream()
                .distinct()
                .filter(param -> filterBy.containsKey(param.getKey()))
                .map(param -> filterBy.get(param.getKey())
                        .apply(criteriaBuilder, root, param.getValue().get(0).trim()))
                .collect(Collectors.toList());
    }

    protected Predicate[] createSearchQuery(LinkedMultiValueMap<String, String> requestParams,
                                          CriteriaBuilder criteriaBuilder,
                                          Root<E> root,
                                          String[] fieldsForSearch) {
        String searchQuery = requestParams.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
        return Arrays.stream(fieldsForSearch)
                .map(entityField -> criteriaBuilder.like(root.get(entityField), createLikeQuery(searchQuery)))
                .toArray(Predicate[]::new);
    }

    protected String createLikeQuery(String searchQuery) {
        return MessageFormat.format(SQL_LIKE_PATTERN, searchQuery);
    }
}
