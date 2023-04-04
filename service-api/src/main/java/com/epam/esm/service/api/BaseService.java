package com.epam.esm.service.api;


/**
 * This interface defines basic CRUD operations that can be
 * performed on any entity.
 *
 * @param <T> the type of entity to be managed
 * @param <N> the type of the entity's identifier
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface BaseService<T, N> {

    /**
     * Finds an entity by its ID.
     *
     * @param id the ID of the entity to be found
     * @return the found entity or null if not found
     * @throws RuntimeException if any error occurs during the operation
     */
    T findById(N id);

    /**
     * Creates a new entity.
     *
     * @param t the entity to be created
     * @return the created entity
     * @throws RuntimeException if any error occurs during the operation
     */
    T create(T t);

    /**
     * Updates an existing entity.
     *
     * @param id the ID of the entity to be updated
     * @param t the updated entity
     * @return the updated entity
     * @throws RuntimeException if any error occurs during the operation
     */
    T update(N id, T t);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to be deleted
     * @return the deleted entity or null if not found
     * @throws RuntimeException if any error occurs during the operation
     */
    T deleteById(N id);
}
