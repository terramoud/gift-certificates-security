package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.validation.UserValidator;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.InvalidPaginationParameterException;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
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
    private final UserValidator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public List<User> getAllUsers(LinkedMultiValueMap<String, String> fields,
                                  int size,
                                  int page) {
        if (size < 1) throw new InvalidPaginationParameterException(
                "limit", size + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        if (page < 0) throw new InvalidPaginationParameterException(
                "offset", page + "", ErrorCodes.INVALID_PAGINATION_PARAMETER);
        Pageable pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(fields, pageRequest);
    }

    @Override
    public User getUserById(Long id) {
        if (!validator.validateId(id)) throw new InvalidResourcePropertyException(
                "user.error.invalid.id", id, ErrorCodes.INVALID_USER_ID_PROPERTY);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user.not.found" , id, ErrorCodes.NOT_FOUND_USER_RESOURCE));
    }
}
