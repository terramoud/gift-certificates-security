package com.epam.esm.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Represents relevant entity from database's table
 *
 * @author Oleksadr Koreshev
 * @since 1.0
 */
@Entity
@Table(name = "tags")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Tag extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
