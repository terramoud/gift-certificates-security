package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User, Long> implements UserRepository {

    private static final String USER_ID = "id";
    private static final String USER_LOGIN = "login";
    private static final String USER_EMAIL = "email";

    private static final String REQUEST_PARAM_LOGIN = "login";
    private static final String REQUEST_PARAM_EMAIL = "email";

    private static final Map<String, BiFunction<CriteriaBuilder, Root<User>, Order>> SORT_ORDERS_MAP = Map.of(
            "+id", (cb, root) -> cb.asc(root.get(USER_ID)),
            "-id", (cb, root) -> cb.desc(root.get(USER_ID)),
            "+login", (cb, root) -> cb.asc(root.get(USER_LOGIN)),
            "-login", (cb, root) -> cb.desc(root.get(USER_LOGIN)),
            "+email", (cb, root) -> cb.asc(root.get(USER_EMAIL)),
            "-email", (cb, root) -> cb.desc(root.get(USER_EMAIL))
    );

    private static final String[] ADMITTED_SORT_PARAMS = {
            USER_ID,
            USER_LOGIN,
            USER_EMAIL
    };

    private static final Map<String, String> REQUEST_PARAM_TO_ENTITY_FIELD_NAME = Map.of(
            REQUEST_PARAM_LOGIN, USER_LOGIN,
            REQUEST_PARAM_EMAIL, USER_EMAIL
    );

    private static final String[] FIELDS_FOR_SEARCH = {USER_LOGIN, USER_EMAIL};

    @Autowired
    public UserRepositoryImpl(EntityManager entityManager) {
        super(User.class,
                entityManager,
                SORT_ORDERS_MAP,
                ADMITTED_SORT_PARAMS,
                REQUEST_PARAM_TO_ENTITY_FIELD_NAME,
                FIELDS_FOR_SEARCH);
    }
}
