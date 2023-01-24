package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.InvalidPaginationParameterException;
import com.epam.esm.repository.api.UserRepository;
import com.epam.esm.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAllUsers(LinkedMultiValueMap<String, String> fields,
                                   int size,
                                   int page) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(fields, pageRequest);
    }
}
