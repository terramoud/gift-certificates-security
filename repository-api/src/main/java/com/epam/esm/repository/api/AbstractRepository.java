package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;


public abstract class AbstractRepository<T extends AbstractEntity, N> implements BaseRepository<T, N> {

    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<T> entity;

    protected AbstractRepository(Class<T> entity) {
        this.entity = entity;
    }

    @Override
    public Optional<T> findById(N id) {
        T obtainedEntity = entityManager.find(entity, id);
        return Optional.ofNullable(obtainedEntity);
    }

    @Override
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(N id) {
        T obtainedEntity = entityManager.find(entity, id);
        entityManager.remove(obtainedEntity);
    }
}
