package com.epam.esm.repository;

import com.epam.esm.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * The interface that defines the methods for interacting
 * with the "users" database table
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by login
     *
     * @param login the user's login.
     * @return an optional containing the user with the specified
     *          login, or an empty optional if no such user exists
     */
    Optional<User> findByLogin(String login);

    /**
     * Finds a user by ID
     *
     * @param id the user's ID.
     * @return an optional containing the user with the specified ID,
     *          or an empty optional if no such user exists.
     * @throws NullPointerException if the given ID is {@code null}.
     */
    @NonNull
    Optional<User> findById(@NonNull Long id);
}

