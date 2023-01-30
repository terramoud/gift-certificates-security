package com.epam.esm.config;

import com.epam.esm.domain.entity.User;

import static com.epam.esm.domain.entity.Role.ADMIN;
import static com.epam.esm.domain.entity.Role.USER;

public class TestUsers {
    public User user1 = new User(1L, "admin", "admin@gmail.com", "adminPass", ADMIN);
    public User user2 = new User(2L, "Peter", "Peter@gmail.com", "PeterPass", USER);
    public User user3 = new User(3L, "testUser", "testUser@gmail.com", "testUserPass", USER);
    public User user4 = new User(4L, "Jon", "Jon@gmail.com", "JonPass", USER);
    public User user5 = new User(5L, "Wick", "Wick@gmail.com", "WickPass", USER);
    public User user6 = new User(6L, "Neo", "Neo@gmail.com", "NeoPass", USER);
    public User user7 = new User(7L, "Morpheus", "Morpheus@gmail.com", "MorpheusPass", USER);
    public User user8 = new User(8L, "Igor", "Igor@gmail.com", "IgorPass", USER);
    public User user9 = new User(9L, "Stepan", "Stepan@gmail.com", "StepanPass", USER);
    public User user10 = new User(10L, "Jason", "Jason@gmail.com", "JasonPass", USER);
    public User user11 = new User(11L, "Statham", "Statham@gmail.com", "StathamPass", USER);
    public User user12 = new User(12L, "Trinity", "Trinity@gmail.com", "TrinityPass", USER);
}

