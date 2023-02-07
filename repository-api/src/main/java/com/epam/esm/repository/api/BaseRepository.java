package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;
import com.epam.esm.domain.entity.Certificate;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;


public interface BaseRepository<E extends AbstractEntity, N> {

    List<E> findAll(LinkedMultiValueMap<String, String> fields, Pageable pageable);

    Optional<E> findById(N id);

    E save(E entity);

    E update(E entity);

    void delete(E entity);
}