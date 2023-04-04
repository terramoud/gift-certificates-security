package com.epam.esm.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This class represents a user entity that is stored in the database.
 * It extends the {@link AbstractEntity} class and
 * implements the {@link Serializable} interface.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class User extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = -3722920263866649342L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;
}
