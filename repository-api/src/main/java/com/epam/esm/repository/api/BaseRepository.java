package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractEntity, N> extends JpaRepository<E, N>, JpaSpecificationExecutor<E> {

}