package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;


public interface BaseRepository<E extends AbstractEntity, N> {
    String SQL_LIKE_PATTERN = "%{0}%";

    List<E> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable);
    Optional<E> findById(N id);
    E save(E entity);
    E update(E entity, N id);
    void delete(E entity);

    default String createLikeQuery(String searchQuery) {
        return MessageFormat.format(SQL_LIKE_PATTERN, searchQuery);
    }
}