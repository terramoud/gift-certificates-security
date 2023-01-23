package com.epam.esm.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Represents relevant entity from database's table
 *
 * @author Oleksadr Koreshev
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 4970441839353680890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
