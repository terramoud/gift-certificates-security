package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.utils.TriFunction;
import com.epam.esm.repository.api.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User, Long> implements UserRepository {

    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String EMAIL = "email";
    private static final String[] FIELDS_FOR_SEARCH = {LOGIN, EMAIL};
    private static final Map<String, BiFunction<CriteriaBuilder, Root<User>, Order>> SORT_ORDERS_MAP = Map.of(
            "+id", (cb, root) -> cb.asc(root.get(ID)),
            "-id", (cb, root) -> cb.desc(root.get(ID)),
            "+login", (cb, root) -> cb.asc(root.get(LOGIN)),
            "-login", (cb, root) -> cb.desc(root.get(LOGIN)),
            "+email", (cb, root) -> cb.asc(root.get(EMAIL)),
            "-email", (cb, root) -> cb.desc(root.get(EMAIL))
    );

    private static final Map<String,
            TriFunction<CriteriaBuilder, Root<User>, String, Predicate>> FIELDS_TO_FILTERS_MAP =
            Map.ofEntries(
                    Map.entry(LOGIN, (cb, r, filterValue) -> cb.equal(r.get(LOGIN), filterValue)),
                    Map.entry(EMAIL, (cb, r, filterValue) -> cb.equal(r.get(EMAIL), filterValue))
            );

    @Autowired
    public UserRepositoryImpl(EntityManager em) {
        super(User.class, em, SORT_ORDERS_MAP, FIELDS_TO_FILTERS_MAP, FIELDS_FOR_SEARCH);
    }
}
