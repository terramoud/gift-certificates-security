package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.payload.UserFilterDto;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.api.UserRepository;
import com.epam.esm.service.api.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.domain.validation.ValidationConstants.*;
import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

/**
 * UserServiceImpl is an implementation of the {@link UserService}
 * interface that provides methods for managing users.
 * <p>
 * This implementation uses {@link UserRepository} to
 * interact with the database and {@link DtoConverter} to
 * convert between entity and DTO objects.
 * </p>
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
public class UserServiceImpl extends AbstractService<UserDto, Long> implements UserService {

    private final UserRepository userRepository;
    private final DtoConverter<User, UserDto> converter;
    private final DtoConverter<Order, OrderDto> orderConverter;

    @Override
    public List<UserDto> findAll(UserFilterDto filter, Pageable pageable) {
        List<User> users = userRepository.findAll(filter, pageable);
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
