package com.epam.esm.service.impl;

import com.epam.esm.service.api.BaseService;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base implementation for {@link BaseService} interface.
 * Implements {@link #isEqualsIds} method to check if
 * provided ids are equal.
 *
 * @param <T> the type of the service
 * @param <N> the type of the ID of the service
 */
@AllArgsConstructor
public abstract class AbstractService<T, N> implements BaseService<T, N> {

    /**
     * Returns {@code true} if all provided IDs are equal,
     * {@code false} otherwise.
     *
     * @param ids the IDs to check for equality
     * @return {@code true} if all IDs are equal, {@code false} otherwise
     */
    @SafeVarargs
    protected final boolean isEqualsIds(N... ids) {
        return List.of(ids).isEmpty() || Arrays.stream(ids).allMatch(ids[0]::equals);
    }
}
