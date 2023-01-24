package com.epam.esm.service.api;

import com.epam.esm.domain.entity.User;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;


public interface UserService {
    List<User> findAllUsers(LinkedMultiValueMap<String, String> fields, int size, int page);
}
