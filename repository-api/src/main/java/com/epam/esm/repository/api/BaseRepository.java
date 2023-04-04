package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


/**
 * This interface serves as a base for all repositories in the
 * application. It extends the JpaRepository and
 * JpaSpecificationExecutor interfaces to provide basic CRUD
 * functionality and support for queries using JPA specifications.
 *
 * @param <E> the type of entity managed by the repository
 * @param <N> the type of the entity's ID
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@NoRepositoryBean
public interface BaseRepository<E extends AbstractEntity, N> extends JpaRepository<E, N>, JpaSpecificationExecutor<E> {

}
