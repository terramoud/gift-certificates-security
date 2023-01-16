package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<E extends AbstractEntity, N> {
    List<E> findAll(MultiValueMap<String, String> fields, Pageable pageable);
    Optional<E> findById(N id);
    E save(E entity);
    void delete(N id);
}