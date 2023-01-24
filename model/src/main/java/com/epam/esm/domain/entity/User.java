package com.epam.esm.domain.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
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

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
    }
}
