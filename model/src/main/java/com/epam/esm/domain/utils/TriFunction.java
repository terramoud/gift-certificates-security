package com.epam.esm.domain.utils;

/**
 * Represents a function that accepts three arguments and produces a result.
 *
 * @param <S> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <V> the type of the third argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<S, U, V, R> {
    R apply(S s, U u, V v);
}
