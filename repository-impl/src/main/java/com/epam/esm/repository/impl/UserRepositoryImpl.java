package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.UserRepository;
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
public class UserRepositoryImpl implements UserRepository {

    public static final String USER_ID = "id";
    public static final String USER_LOGIN = "login";
    public static final String SORT_REQUEST_PARAM = "sort";
    public static final String SEARCH_REQUEST_PARAM = "search";
    public static final String FILTER_KEY_LOGIN = "login";

    private final Map<String, BiFunction<CriteriaBuilder, Root<User>, Order>> sortBy = Map.of(
            "+login", (cb, root) -> cb.asc(root.get(USER_LOGIN)),
            "-login", (cb, root) -> cb.desc(root.get(USER_LOGIN)),
            "+id", (cb, root) -> cb.asc(root.get(USER_ID)),
            "-id", (cb, root) -> cb.desc(root.get(USER_ID))
    );

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public User save(User entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public User update(User entity, Long id) {
        return em.merge(entity);
    }

    @Override
    public void delete(User entity) {
        em.remove(entity);
    }

    @Override
    public List<User> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        List<Predicate> predicates = filters(fields, criteriaBuilder, root);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        criteriaQuery.select(root);
        sort(fields, criteriaBuilder, criteriaQuery, root);
        return em.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private List<Predicate> filters(LinkedMultiValueMap<String, String> fields,
                                    CriteriaBuilder criteriaBuilder,
                                    Root<User> root) {
        String searchQuery = fields.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0);
        String filterByLogin = fields.getOrDefault(FILTER_KEY_LOGIN, List.of("")).get(0);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(root.get(USER_LOGIN), createLikeQuery(searchQuery)));
        if (!filterByLogin.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get(USER_LOGIN), filterByLogin));
        }
        return predicates;
    }

    private void sort(LinkedMultiValueMap<String, String> fields,
                      CriteriaBuilder criteriaBuilder,
                      CriteriaQuery<User> criteriaQuery,
                      Root<User> root) {
        String stringSortParams = fields.getOrDefault(SORT_REQUEST_PARAM, List.of(USER_ID)).get(0).trim();
        if (stringSortParams.isEmpty()) {
            stringSortParams = "+".concat(USER_ID);
        }
        List<String> sortParams = Arrays.stream(stringSortParams.split(","))
                .map(String::trim)
                .map(el -> el.startsWith(USER_LOGIN) ? "+".concat(el) : el)
                .map(el -> el.startsWith(USER_ID) ? "+".concat(el) : el)
                .distinct()
                .collect(Collectors.toList());
        List<Order> orders = sortParams.stream()
                .filter(sortBy::containsKey)
                .map(tagProperty -> sortBy.get(tagProperty).apply(criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.orderBy(orders);
    }
}
