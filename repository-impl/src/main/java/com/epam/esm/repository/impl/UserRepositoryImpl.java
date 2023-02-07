package com.epam.esm.repository.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.repository.api.UserRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User, Long> implements UserRepository {

    private static final String USER_ID = "id";
    private static final String USER_LOGIN = "login";
    private static final String USER_EMAIL = "email";
    private static final String USER_ROLE = "role";

    public static final String REQUEST_PARAM_LOGIN = "login";
    public static final String REQUEST_PARAM_EMAIL = "email";
    public static final String REQUEST_PARAM_ROLE = "role";

    private static final Map<String, BiFunction<CriteriaBuilder, Root<User>, Order>> SORT_ORDERS_MAP = Map.of(
            "+id", (cb, root) -> cb.asc(root.get(USER_ID)),
            "-id", (cb, root) -> cb.desc(root.get(USER_ID)),
            "+login", (cb, root) -> cb.asc(root.get(USER_LOGIN)),
            "-login", (cb, root) -> cb.desc(root.get(USER_LOGIN)),
            "+email", (cb, root) -> cb.asc(root.get(USER_EMAIL)),
            "-email", (cb, root) -> cb.desc(root.get(USER_EMAIL)),
            "+role", (cb, root) -> cb.asc(root.get(USER_ROLE)),
            "-role", (cb, root) -> cb.desc(root.get(USER_ROLE))
    );

    private static final String[] ADMITTED_SORT_PARAMS = {
            USER_ID,
            USER_LOGIN,
            USER_EMAIL,
            USER_ROLE
    };

    private static final Map<String, String> REQUEST_PARAM_TO_ENTITY_FIELD_NAME = Map.of(
            REQUEST_PARAM_LOGIN, USER_LOGIN,
            REQUEST_PARAM_EMAIL, USER_EMAIL,
            REQUEST_PARAM_ROLE, USER_ROLE
    );

    private static final String[] FIELDS_FOR_SEARCH = {USER_LOGIN, USER_EMAIL, USER_ROLE};

    @Override
    protected void fetchLeftJoin(Root<User> root) {
        throw new UnsupportedOperationException();
    }

    public UserRepositoryImpl() {
        super(User.class,
                SORT_ORDERS_MAP,
                ADMITTED_SORT_PARAMS,
                REQUEST_PARAM_TO_ENTITY_FIELD_NAME,
                FIELDS_FOR_SEARCH);
    }
}
