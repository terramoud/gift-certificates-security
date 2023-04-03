package com.epam.esm.config;

import com.epam.esm.domain.entity.User;

import static com.epam.esm.domain.entity.Role.ADMIN;
import static com.epam.esm.domain.entity.Role.USER;

public class TestUsers {
    public User user1 = new User(1L, "admin", "admin@gmail.com", "$2a$10$A98lGpoVba4tTSWMUUBl/.j/d7vuLBoiCMxb25hMtBj8S0JzUc6Nu", ADMIN);
    public User user2 = new User(2L, "Peter", "Peter@gmail.com", "$2a$10$ag9qW9c1gbV407bY5/GGzuIgUi75NiTS9MV9iK2ZFbH6AuGER.lLO", USER);
    public User user3 = new User(3L, "testUser", "testUser@gmail.com", "$2a$10$jaoRM4TZRnWrC83jbyhA2OSOZ0liSJhXPpoUB88XSwyub7EFz/v.O", USER);
    public User user4 = new User(4L, "Jon", "Jon@gmail.com", "$2a$10$MihRjy3CcgJGu/.JTB511ufB7iPJHyk9BiffASo..DB4gkN81Yqie", USER);
    public User user5 = new User(5L, "Wick", "Wick@gmail.com", "$2a$10$eV4pAxO/xV9PorfG1.Qvt.vhvGcVgFE429PwFRHywiwucIAPsOG9q", USER);
    public User user6 = new User(6L, "Neo", "Neo@gmail.com", "$2a$10$wKAPChZ62wUyDCJKtntYbuT1AijIstg.cbVdIFIYCGNPULBl7e/Ra", USER);
    public User user7 = new User(7L, "Morpheus", "Morpheus@gmail.com", "$2a$10$kWuVaKrM0UNRdOkUNz8EXO3tWk6yv//KnL4U/hJfAXf4UgYj3g62m", USER);
    public User user8 = new User(8L, "Igor", "Igor@gmail.com", "$2a$10$ObJEGHYBvLWC3b93hzwPc.MMi.51fLFt8CPjOFtmmMt9hfOORsnjG", USER);
    public User user9 = new User(9L, "Stepan", "Stepan@gmail.com", "$2a$10$B9Z9adkyzcvP5uncdgAD1ulioPEudvZmZ3rPEVbwuiphKRr4ke.4q", USER);
    public User user10 = new User(10L, "Jason", "Jason@gmail.com", "$2a$10$XsqEhDSe0m2tj4p3o/aIKeWNuj4dK7carFeKRJRONXCf/xkwv805K", USER);
    public User user11 = new User(11L, "Statham", "Statham@gmail.com", "$2a$10$7LmogaHyjT6ozv0FjybEHu1M0.xhrTWZSR6vvsB8Nc9ZoQFTsiwBC", USER);
    public User user12 = new User(12L, "Trinity", "Trinity@gmail.com", "$2a$10$PY3YtSItPOQ4AuHpFF5ICupCrfCuMB91.9r4CbWL5VaEUc.AYvpha", USER);
}

