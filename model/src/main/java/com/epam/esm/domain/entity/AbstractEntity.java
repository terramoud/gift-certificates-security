package com.epam.esm.domain.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Basic entity for every model's entity
 *
 * @author Oleksadr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    private static final long serialVersionUID = -1491456600908233143L;
}
