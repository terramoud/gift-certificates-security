package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.api.UserRepository;
import com.epam.esm.service.api.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class UserServiceImpl extends AbstractService<UserDto, Long> implements UserService {

    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String EMAIL = "email";
    private static final String[] FIELDS_FOR_SEARCH = {LOGIN, EMAIL};
    private static final Map<String, Sort> sortMap = Map.of(
            "+id", Sort.by(Sort.Direction.ASC, ID),
            "-id", Sort.by(Sort.Direction.DESC, ID),
            "+login", Sort.by(Sort.Direction.ASC, LOGIN),
            "-login", Sort.by(Sort.Direction.DESC, LOGIN),
            "+email", Sort.by(Sort.Direction.ASC, EMAIL),
            "-email", Sort.by(Sort.Direction.DESC, EMAIL)
    );

    private static final Map<String, Function<String, Specification<User>>> filterMap = Map.of(
            LOGIN, filterValue -> (root, query, cb) -> cb.equal(root.get(LOGIN), filterValue),
            EMAIL, filterValue -> (root, query, cb) -> cb.equal(root.get(EMAIL), filterValue)
    );

    private static final Map<String, Function<String, Specification<User>>> searchMap = Map.of(
            LOGIN, searchValue -> (r, query, cb) -> cb.like(r.get(LOGIN), createLikeQuery(searchValue)),
            EMAIL, searchValue -> (r, query, cb) -> cb.like(r.get(EMAIL), createLikeQuery(searchValue))
    );

    private final UserRepository userRepository;
    private final DtoConverter<User, UserDto> converter;
    private final DtoConverter<Order, OrderDto> orderConverter;

    @Override
    public List<UserDto> findAll(LinkedMultiValueMap<String, String> requestParams, PageDto pageDto) {
        List<User> users = findAllAbstract(requestParams, pageDto, userRepository, sortMap, filterMap, searchMap);
        return converter.toDto(users);
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_USER_RESOURCE));
        return converter.toDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = converter.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return converter.toDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (!isEqualsIds(userDto.getId(), id)) {
            throw new InvalidResourcePropertyException(USER_ID_NOT_MAPPED, userDto.getId(), INVALID_ID_PROPERTY);
        }
        Optional<User> sourceUser = userRepository.findById(id);
        if (sourceUser.isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_USER_RESOURCE);
        }
        String password = sourceUser.get().getPassword();
        User userToUpdate = converter.toEntity(userDto);
        userToUpdate.setPassword(password);
        User updated = userRepository.save(userToUpdate);
        return converter.toDto(updated);
    }

    @Override
    public UserDto deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_USER_RESOURCE));
        userRepository.delete(user);
        return converter.toDto(user);
    }
}
