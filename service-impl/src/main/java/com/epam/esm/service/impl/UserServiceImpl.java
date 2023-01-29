package com.epam.esm.service.impl;

import com.epam.esm.domain.converter.DtoConverter;
import com.epam.esm.domain.entity.Order;
import com.epam.esm.domain.entity.User;
import com.epam.esm.domain.payload.OrderDto;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.domain.payload.UserDto;
import com.epam.esm.domain.validation.OnCreate;
import com.epam.esm.domain.validation.UserValidator;
import com.epam.esm.exceptions.ErrorCodes;
import com.epam.esm.exceptions.InvalidResourcePropertyException;
import com.epam.esm.exceptions.ResourceNotFoundException;
import com.epam.esm.repository.api.UserRepository;
import com.epam.esm.service.api.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.epam.esm.exceptions.ErrorCodes.INVALID_ID_PROPERTY;

@EqualsAndHashCode(callSuper = true)
@Data
@Service
@Transactional
@Validated
public class UserServiceImpl extends AbstractService<UserDto, Long> implements UserService {

    private static final String WRONG_USER_ID = "wrong.tag.id";
    private static final String USER_ID_NOT_MAPPED = "tag.id.not.mapped";
    private static final String USER_NOT_FOUND = "user.not.found";

    private final UserRepository userRepository;
    private final UserValidator validator;
    private final DtoConverter<User, UserDto> converter;
    private final DtoConverter<Order, OrderDto> orderConverter;

    @Override
    public List<UserDto> findAll(LinkedMultiValueMap<String, String> fields, @Valid PageDto pageDto) {
        Pageable pageRequest = PageRequest.of(pageDto.getPage(), pageDto.getSize());
        return converter.toDto(userRepository.findAll(fields, pageRequest));
    }

    @Override
    public UserDto findById(@Positive(message = WRONG_USER_ID) Long id) {
        return converter.toDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_USER_RESOURCE)));
    }

    @Validated(OnCreate.class)
    @Override
    public UserDto create(@Valid UserDto userDto) {
        return converter.toDto(userRepository.save(converter.toEntity(userDto)));
    }

    @Override
    public UserDto update(@Positive(message = WRONG_USER_ID) Long id,
                          @Valid UserDto userDto) {
        if (!isEqualsIds(userDto.getId(), id)) {
            throw new InvalidResourcePropertyException(USER_ID_NOT_MAPPED, userDto.getId(), INVALID_ID_PROPERTY);
        }
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, id, ErrorCodes.NOT_FOUND_USER_RESOURCE);
        }
        return converter.toDto(userRepository.update(converter.toEntity(userDto), id));
    }

    @Override
    public UserDto deleteById(@Positive(message = WRONG_USER_ID) Long id) {
        UserDto userDto = findById(id);
        userRepository.delete(converter.toEntity(userDto));
        return userDto;
    }
}
